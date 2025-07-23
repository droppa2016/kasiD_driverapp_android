package co.za.kasi.services.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import co.za.kasi.MainApplication
import co.za.kasi.R
import co.za.kasi.db.sharedPreferance.PendingWaybillsStorage
import co.za.kasi.model.superApp.a.location.CreateDriverLocation
import co.za.kasi.model.superApp.a.waybillData.WaybillRequest
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.LocalStorage
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.utils.ReusableMethods
import co.za.kasi.utils.SharedState
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class LocationService1 : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var realtimeClient: RealtimeMessagingClient
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    val currentActivity = MainApplication.instance?.currentActivity


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        PendingWaybillsStorage.init(applicationContext)

        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

        val ktorClient = HttpClient(CIO) {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        realtimeClient = KtorRealtimeMessagingClient(ktorClient)

        setupNetworkMonitoring()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {

        Log.e("", "=========================================== Start")

        val channel = NotificationChannel(
            "location",
            "Location Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Skynet Driver App ")
            .setContentText("Skynet Driver App is running in the background")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(30000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->

                val lat = location.latitude.toString()
                val lng = location.longitude.toString()
                val battery = getBatteryLevel()
                val time = LocalDateTime.now().toString()
                val prefs = getSharedPreferences("inactivity", Context.MODE_PRIVATE)
                val now = System.currentTimeMillis()
                val lastInteraction = prefs.getLong("last_interaction", now)
                val inactivityTimeout = 2 * 60 * 60 * 1000L

                if (now - lastInteraction >= inactivityTimeout) {
                    ReusableMethods.performLightSignOut(applicationContext, currentActivity)
                    return@onEach
                }


                val locationData = CreateDriverLocation(
                    driverId = LocalStorage.getSkynetDriverAccount().driver.identityNo,
                    latitude = lat,
                    longitude = lng,
                    batteryPercentage = battery,
                    dateTime = time,
                    deviceId = LocalStorage.getOrCacheAndroidId(this)
                )

                Log.e(
                    "",
                    "=========================================== Location: ($lat, $lng)  -- bat - ${battery}"
                )
                Log.e(
                    "",
                    "=========================================== Location: ($lat, $lng)  -- bat - ${battery} -- deviceId - ${
                        LocalStorage.getOrCacheAndroidId(applicationContext)
                    }"
                )

                val updatedNotification =
                    notification.setContentText("Skynet Driver App is running in the background")
                notificationManager.notify(1, updatedNotification.build())

                if (isNetworkAvailable(applicationContext)) {
                    realtimeClient.sendDriverLocation(locationData)
                } else {

                    Log.e(
                        "",
                        "=========================================== Offline mode: Skipping location send"
                    )
                }

            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    private fun getBatteryLevel(): String {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return "$level"
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        if (::connectivityManager.isInitialized && ::networkCallback.isInitialized) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }


    private fun setupNetworkMonitoring() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                super.onAvailable(network)
                Log.e("", "=========================================== Network Available")
                showToast("Network Connected")

                syncPendingWaybills()
            }

            override fun onLost(network: android.net.Network) {
                super.onLost(network)
                Log.e("", "=========================================== Network Lost")
                showToast("Network Disconnected")
            }
        }

        val networkRequest = android.net.NetworkRequest.Builder()
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun showToast(message: String) {
        android.os.Handler(mainLooper).post {
            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun syncPendingWaybills() {

        Log.e("", "=========================================== SYNC START")

        serviceScope.launch {
            val pendingWaybills = PendingWaybillsStorage.getPendingWaybills()

            if (pendingWaybills.isNotEmpty()) {
                Log.e(
                    "",
                    "=========================================== Syncing ${pendingWaybills.size} pending waybills"
                )

                val successfullySynced = mutableListOf<WaybillRequest>()
                val totalWaybills = pendingWaybills.size
                var completedCount = 0

                for (waybill in pendingWaybills) {
                    try {

                        val retro = ReusableFunctions.initiateSuperAppRetrofit(applicationContext)
                            .submitWaybill(
                                LocalStorage.getSuperAppToken(applicationContext),
                                waybill
                            )

                        retro.enqueue(object : Callback<Waybills> {
                            override fun onResponse(
                                call: Call<Waybills>,
                                response: Response<Waybills>
                            ) {
                                if (response.isSuccessful) {
                                    successfullySynced.add(waybill)
                                    serviceScope.launch {
                                        SharedState.updateSyncedWaybills(
                                            applicationContext,
                                            SharedState.syncedWaybills.value + 1
                                        )
                                        SharedState.updateProgress(
                                            current = SharedState.syncedWaybills.value,
                                            total = SharedState.awaitingSync.value
                                        )
                                    }

                                    Log.e("", "Waybill synced: ${waybill.waybillNumber}")
                                } else {
                                    Log.e("", "Failed to sync ${waybill.waybillNumber}: ${response.code()}")
                                }

                                checkIfLastWaybillSynced()
                            }

                            override fun onFailure(call: Call<Waybills>, t: Throwable) {
                                Log.e(
                                    "",
                                    " Exception syncing waybill ${waybill.waybillNumber}: ${t.message}"
                                )
                            }

                            fun checkIfLastWaybillSynced() {
                                completedCount++
                                if (completedCount == totalWaybills) {
                                    Log.e("", "All waybills have been processed.")
                                    PendingWaybillsStorage.clearAllWaybillLists()
                                }
                            }


                        })


                    } catch (e: Exception) {
                        Log.e(
                            "",
                            " Exception syncing waybill ${waybill.waybillNumber}: ${e.message}"
                        )
                    }
                }


                if (successfullySynced.isNotEmpty()) {
                    val remaining = pendingWaybills.filter { it !in successfullySynced }
                    saveRemainingWaybills(remaining)
                }
            }
        }
    }

    private fun saveRemainingWaybills(remaining: List<WaybillRequest>) {
        if (remaining.isEmpty()) {
            PendingWaybillsStorage.clearPendingWaybills()
        } else {
            val json = Gson().toJson(remaining)
            getSharedPreferences("PendingWaybills", Context.MODE_PRIVATE)
                .edit()
                .putString("PendingWaybillsList", json)
                .apply()
        }
    }


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            } else {
                network != null
            }
        }

    }
}