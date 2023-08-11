package com.atakan.mainclient.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.Process
import android.os.RemoteException
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.atakan.mainclient.common.Constants.CURR1
import com.atakan.mainclient.common.Constants.CURR2
import com.atakan.mainclient.common.Constants.CURR3
import com.atakan.mainclient.common.Constants.PACKAGE_NAME
import com.atakan.mainclient.common.Constants.PID
import com.atakan.mainclient.common.Constants.RATE1
import com.atakan.mainclient.common.Constants.RATE2
import com.atakan.mainclient.common.Constants.RATE3
import com.atakan.mainclient.common.Constants.TIME
import com.atakan.mainclient.common.Resource
import com.atakan.mainclient.domain.model.Currency
import com.atakan.mainclient.domain.use_case.GetCurrencyUseCase
import com.atakan.mainclient.presentation.MainActivity
import com.atakan.mainclient.presentation.currency.CurrencyViewModel
import com.atakan.mainclient.presentation.currency.screen.ServiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MessengerService : Service(){
    @Inject
    lateinit var getCurrencyUseCase: GetCurrencyUseCase
    // Get viewModel instance
    @Inject
    lateinit var viewModel: CurrencyViewModel

    @Inject
    lateinit var clickViewModel: ServiceViewModel

    var sendMessage: Boolean = false

    private var serverMessenger: Messenger? = null

    // Messenger on the client
    private var clientMessenger: Messenger? = null

    // Service Connection
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Called when the connection to the server service is established
            serverMessenger = Messenger(service)
            clientMessenger = Messenger(handler)
            sendMessageToServer()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Called when the connection to the server service is disconnected
            serverMessenger = null
            clientMessenger = null
        }
    }

    // Handle messages from the remote service (server app)
    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // Update UI with remote process info

            val bundle = msg.data
            //val serverClass = Serverprop(bundle.getInt(PID).toString(), bundle.getInt(
            //    CONNECTION_COUNT).toString())

            //serverprop.serverData.postValue(serverClass)
        }
    }

    private val clickObserver = Observer<Boolean> {
        if(it){
            try {
                doBindService()
            }
            catch (e : Exception){
                Log.w("MessengerError", e.toString())
                val LaunchIntent = packageManager.getLaunchIntentForPackage("com.atakan.mainserver")
                runBlocking {
                    startActivity(LaunchIntent)
                    delay(2000)
                }
                Log.w("Messenger", "Trying to wake up Server App")
                try {
                    doBindService()
                }catch (e : Exception){
                    Log.w("MessengerError", e.toString())
                }
            }
        }
        else{
            try {
                doUnbindService()
            }catch (e : Exception){
                // First Start
            }

        }
    }

    val apiHandler = Handler()
    private val apiRunnable = object : Runnable {
        override fun run() {

            // Make the API call and update viewModel with the response data
            Log.d("Messenger", "Fetching data from API")

            val job = CoroutineScope(Dispatchers.IO).launch {
                getCurrencyUseCase.invoke().collect{
                    when (it) {
                        is Resource.Success -> {
                            // Update viewModel with the fetched resource
                            viewModel.refreshData(it)
                        }
                        is Resource.Loading -> {
                            //
                        }
                        is Resource.Error -> {
                            // Handle error state if needed
                            println("Error: ${it.message}")
                        }
                    }
                }

            }

            // Make sure to cancel the coroutine when the service is stopped
            job.invokeOnCompletion {
                if (it != null) {
                    Log.e("Messenger Error","Coroutine completed with an exception: ${it.message}")
                } else {
                    //
                }
            }

            if(sendMessage){
                sendMessageToServer()
            }

            // Repeat this process every minute
            apiHandler.postDelayed(this, 5000)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        // ?
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Foreground Service Channel"
            val descriptionText = "Foreground service channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ForegroundServiceChannel", name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Start the service as a foreground service with a notification
        val notification = createNotification() // Implement the createNotification() method
        startForeground(2, notification)
        clickViewModel.isServiceConnected.observeForever(clickObserver)
    }
    private fun createNotification(): Notification {
        // Create and return the notification for the foreground service
        // You can customize the notification as needed
        // For example:
        val notificationTitle = "Messenger Service"
        val notificationText = "Service is running in the background"
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                // Add the extra value for fragment identification
                //putExtra("FRAGMENT_ID", R.id.navigation_messenger)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "ForegroundServiceChannel")
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .build()
    }


    override fun onDestroy() {
        apiHandler.removeCallbacks(apiRunnable)
        doUnbindService()
        clickViewModel.isServiceConnected.removeObserver(clickObserver)
        // Stop the foreground service and remove the notification
        stopForeground(true)
        super.onDestroy()
    }

    // Start service according to the button
    private fun doBindService(){
        if(sendMessage){
            Log.d("Messenger", "ALREADY connected")
            return
        }
        // Start the server service
        Intent("messenger").also { intent ->
            intent.`package` = "com.atakan.mainserver"
            startService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        Log.d("Messenger", "SUCCESSFULLY connected")
        sendMessage = true
    }

    // Stop service activity according to the button activity
    private fun doUnbindService(){
        if(sendMessage){
            sendMessage = false
            try {
                unbindService(serviceConnection)
                Log.d("Messenger", "SUCCESSFULLY disconnected")

            }catch (e : Exception)
            {
                Log.w("Messenger", e.toString())
            }
        }
        else{
            try {
                unbindService(serviceConnection)
            } catch (e: Exception){
                Log.e("Messenger", "Failed to disconnect.")
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Start the periodic API calls and send data if the service is bounded
        apiHandler.post(apiRunnable)

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d("Messenger", "Task Removed, Restarting Service")
        apiHandler.removeCallbacks(apiRunnable)
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun sendMessageToServer(){
        if (serverMessenger == null) {
            // Server service connection is lost or not available
            Log.e("Messenger", "Server service connection lost. Cannot send message.")
            return
        }
        val message = Message.obtain(handler)
        val bundle = Bundle()
        when (val resource: Resource<Currency>? = viewModel.currencyLiveData.value) {
            is Resource.Success -> {
                Log.d("Messenger", "Sending Data")
                val currencyData: Currency = resource.data!!
                bundle.putString(CURR1, currencyData?.bpi?.USD?.code)
                bundle.putString(CURR2, currencyData?.bpi?.EUR?.code)
                bundle.putString(CURR3, currencyData?.bpi?.GBP?.code)

                bundle.putDouble(RATE1, currencyData?.bpi?.USD?.rate_float?.toDouble()!!)
                bundle.putDouble(RATE2, currencyData?.bpi?.EUR?.rate_float?.toDouble()!!)
                bundle.putDouble(RATE3, currencyData?.bpi?.GBP?.rate_float?.toDouble()!!)
                bundle.putString(TIME, currencyData?.time?.updated)

                bundle.putString(PACKAGE_NAME, applicationContext.packageName)
                bundle.putInt(PID, Process.myPid())
                message.data = bundle
                message.replyTo = clientMessenger //for communication to be two-way
                try {
                    serverMessenger?.send(message)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                } finally {
                    message.recycle()
                }
            }

            is Resource.Loading -> {
                Log.d("Messenger", "Loading")
            }

            is Resource.Error -> {
                Log.e("Messenger", "Unexpecte error occurred.")
            }

            else -> {
                Log.w("Messenger", "Null")
            }
        }
    }
}