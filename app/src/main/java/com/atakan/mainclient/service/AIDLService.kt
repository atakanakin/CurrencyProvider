package com.atakan.mainclient.service

import com.atakan.mainclient.common.Resource
import com.atakan.mainclient.domain.model.Currency
import com.atakan.mainclient.domain.use_case.GetCurrencyUseCase
import com.atakan.mainclient.presentation.currency.CurrencyViewModel
import kotlinx.coroutines.flow.onEach
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.atakan.mainclient.CurrencyApplication
import com.atakan.mainclient.presentation.MainActivity
import com.atakan.mainserver.IIPCExample
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AIDLService : Service() {
    @Inject
    lateinit var getCurrencyUseCase: GetCurrencyUseCase

    companion object{
        var sendMessage: Boolean = false
    }

    @Inject
    lateinit var viewModel: CurrencyViewModel


    var iRemoteService: IIPCExample? = null

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
                            println(it.data)
                        }
                        is Resource.Loading -> {
                            println("Fetching data from API")
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
                    Log.d("AIDL","Coroutine completed successfully")
                }
            }

            if(sendMessage){
                sendDataToServer()
            }

            // Repeat this process every minute
            apiHandler.postDelayed(this, 6000)
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

    fun connectToRemoteService() {
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
    }

    fun disconnectToRemoteService() {
        try {
            sendMessage = false
            unbindService(connection)
        }catch (e : Exception){
            Log.w("AIDLError", e.toString())
            sendMessage = false
        }
    }

    override fun onCreate() {
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
        startForeground(3, notification)

        super.onCreate()
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
        // Stop the foreground service and remove the notification
        stopForeground(true)
        super.onDestroy()

    }
    // Method to send data to the server application
    fun sendDataToServer() {
        val resource: Resource<Currency> = viewModel.currencyLiveData.value!!

        when (resource) {
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
        }
    }
}
