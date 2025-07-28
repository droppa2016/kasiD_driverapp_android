package co.za.kasi.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioAttributes
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import co.za.kasi.AccountAccessActivity
import co.za.kasi.R
import co.za.kasi.SkynetHome
import co.za.kasi.enums.PushType
import co.za.kasi.model.TokenUpdateBody
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriverAppLoginBodyResponse
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.LocationService1
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PushNotificationService : FirebaseMessagingService() {
    private var service: SuperAppHttpService? = null
    private val pushScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (LocationService1.isNetworkAvailable(this)) {
            if (LocalStorage.getSkynetDriverAccount() == null) {
                LocalStorage.storeFcmToken(token)
            } else {
                if (!LocalStorage.getIsUserBlocked()) {
                    updateBackEndToken(token)
                }
            }
        }

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (LocationService1.isNetworkAvailable(this)) {
            val data = message.data
            val status = data["status"] ?: "UNKNOWN"
            val title = message.notification?.title ?: ""
            val message = message.notification?.body ?: ""
            var shouldShowNotification = false

            when (status) {
                PushType.ACCOUNT_BLOCKED.name -> {
                    LocalStorage.storeIsUserBlocked(true)
                    shouldShowNotification = true
                    handleBlockedAccount(title, message)
                }

                PushType.ACCOUNT_UNBLOCK.name -> {
                    LocalStorage.storeIsUserBlocked(false)
                    shouldShowNotification = true
                }

                PushType.APP_SYNC_REQUIRED.name -> {
//                    pushScope.launch {
//                        SharedState.updateSyncRequired(
//                            this@PushNotificationService,
//                            true
//                        )
//                    }

                    shouldShowNotification = true
                }

                else -> {
                    shouldShowNotification = true
                }
            }

            if(shouldShowNotification){
                showNotificationWithIntent(
                    title = title,
                    message,
                    status
                )
            }
        }
    }

    private fun showNotificationWithIntent(title: String, message: String, status: String) {
        val intent = Intent(this, SkynetHome::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("STATUS", status)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "channel_$status"

        val soundUri = when (status) {
            "ALLOCATED" -> "android.resource://${packageName}/${R.raw.new_delivery_assigned}".toUri()
            "STOPPED" -> "android.resource://${packageName}/${R.raw.cancel_delivery}".toUri()
            else -> "android.resource://${packageName}/${R.raw.big_bell}".toUri()
        }

        val channelName = "Channel for $status"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            setSound(soundUri, audioAttributes)
            enableVibration(true)
        }

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.skynet_logo)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }

    fun updateBackEndToken(token: String) {
        val tokenBody = TokenUpdateBody(
            fcmToken = token,
            driverIdNo = LocalStorage.getSkynetDriverAccount().driver.identityNo,
            deviceId = LocalStorage.getOrCacheAndroidId(this)
        )

        service = ReusableFunctions.initiateSuperAppRetrofit(this)

        service?.updateFcmToken(
            LocalStorage.getSuperAppToken(this),
            tokenBody
        )
            ?.enqueue(object : Callback<SkynetDriverAppLoginBodyResponse> {
                override fun onResponse(
                    call: Call<SkynetDriverAppLoginBodyResponse>,
                    response: Response<SkynetDriverAppLoginBodyResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.code() == 403) {
                            // Token update failed
                        }
                    } else {
                        // Server error
                        Log.e("FCM", "Server error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<SkynetDriverAppLoginBodyResponse>, t: Throwable) {
                    // Network error
                    Log.e("FCM", "Network error: ${t.localizedMessage}")
                }
            })
    }

    fun handleBlockedAccount(title: String, message: String) {

        val serviceIntent = Intent(this, LocationService1::class.java).apply {
            action = LocationService1.ACTION_STOP
        }
        this.startService(serviceIntent)

        val intent = Intent(this, AccountAccessActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "blocked_account_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Blocked Account Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications when an account is blocked"
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.skynet_logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

}