package com.atakan.mainclient.service

import com.atakan.mainclient.common.Resource
import com.atakan.mainclient.domain.model.Currency
import com.atakan.mainclient.domain.use_case.GetCurrencyUseCase
import com.atakan.mainclient.presentation.currency.CurrencyViewModel
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
import android.os.Handler
import android.os.IBinder
import android.os.Process
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.atakan.mainclient.presentation.MainActivity
import com.atakan.mainclient.presentation.currency.screen.ServiceViewModel
import com.atakan.mainserver.IIPCExample
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AIDLService : Service() {
    @Inject
    lateinit var getCurrencyUseCase: GetCurrencyUseCase
    // Get viewModel instance
    @Inject
    lateinit var viewModel: CurrencyViewModel

    @Inject
    lateinit var clickViewModel: ServiceViewModel

    var sendMessage: Boolean = false

    var iRemoteService: IIPCExample? = null

    private val clickObserver = Observer<Boolean> {
        if(it){
            if(sendMessage) {
            }
            else{
                connectToRemoteService()
            }
        }
        else{
            if(sendMessage) {
                disconnectToRemoteService()
            } else{

            }
        }
    }

    val apiHandler = Handler()
    private val apiRunnable = object : Runnable {
        override fun run() {

            // Make the API call and update viewModel with the response data
            Log.d("AIDL", "Fetching data from API")

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
                    Log.e("AIDL Error","Coroutine completed with an exception: ${it.message}")
                } else {
                    //
                }
            }

            if(sendMessage){
                sendDataToServer()
            }

            // Repeat this process every minute
            apiHandler.postDelayed(this, 5000)
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Gets an instance of the AIDL interface
            iRemoteService = IIPCExample.Stub.asInterface(service)
            if(sendMessage) {
                sendDataToServer()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            iRemoteService = null
        }
    }

    // apiHandler.removeCallbacks(apiRunnable)


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        apiHandler.post(apiRunnable)
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d("AIDL", "Task Removed, Restarting Service")
        apiHandler.removeCallbacks(apiRunnable)
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun connectToRemoteService() {
        Log.d("AIDL", "Connected")
        val intent = Intent("aidl")
        sendMessage = true
        val pack = IIPCExample::class.java.`package`
        pack?.let {
            intent.setPackage(pack.name)
            bindService(
                intent, connection, Context.BIND_AUTO_CREATE
            )
        }
        sendDataToServer()
    }

    private fun disconnectToRemoteService() {
        try {
            sendMessage = false
            unbindService(connection)
        }catch (e : Exception){
            Log.w("AIDLError", e.toString())
            sendMessage = false
        }
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

        val notification = createNotification()
        startForeground(1, notification)
        clickViewModel.isServiceConnected.observeForever(clickObserver)
    }
    private fun createNotification(): Notification {
        val notificationTitle = "AIDL Service"
        val notificationText = "Service is running in the background"
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                // Add the extra value for fragment identification
                //putExtra("FRAGMENT_ID", R.id.navigation_aidl)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "ForegroundServiceChannel")
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        apiHandler.removeCallbacks(apiRunnable)
        disconnectToRemoteService()
        clickViewModel.isServiceConnected.removeObserver(clickObserver)
        // Stop the foreground service and remove the notification
        stopForeground(true)
        super.onDestroy()

    }
    // Method to send data to the server application
    fun sendDataToServer() {
        Log.d("AIDL", "isHEre")
        when (val resource: Resource<Currency>? = viewModel.currencyLiveData.value) {
            is Resource.Success -> {
                val currencyData: Currency = resource.data!!
                Log.d("AIDL", "Sending Data")
                iRemoteService?.postVal(
                    applicationContext.packageName,
                    Process.myPid(),
                    currencyData.bpi.USD.code,
                    currencyData.bpi.EUR.code,
                    currencyData.bpi.GBP.code,
                    currencyData.bpi.USD.rate_float.toDouble(),
                    currencyData.bpi.EUR.rate_float.toDouble(),
                    currencyData.bpi.GBP.rate_float.toDouble(),
                    currencyData.time.updated
                )
                /*val serverClass = Serverprop(iRemoteService?.pid.toString(), iRemoteService?.connectionCount.toString())
                serverprop.serverData.postValue(serverClass)
                 */
            }
            is Resource.Loading -> {
                Log.d("AIDL", "Loading")
            }

            is Resource.Error -> {
                Log.w("AIDL", "Unexpected error occurred.")
            }
            else -> {
                Log.e("AIDL", "Null")
            }
        }
    }
}
