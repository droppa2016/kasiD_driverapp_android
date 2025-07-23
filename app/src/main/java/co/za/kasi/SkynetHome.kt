package co.za.kasi

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import co.za.kasi.databinding.ActivitySkynetHomeBinding
import co.za.kasi.fragments.ActionConfirmationDialogFragment
import co.za.kasi.fragments.SkyNetNewSyncFragment
import co.za.kasi.fragments.bottomnav.SkyAccountFragment
import co.za.kasi.fragments.bottomnav.SkyHomeFragment
import co.za.kasi.model.AppUpdateResponse
import co.za.kasi.model.TokenUpdateBody
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriverAppLoginBodyResponse
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.GpsStatusReceiver
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.utils.SharedState
import co.za.kasi.utils.networkMonitorService.NetworkConnectivity
import co.za.kasi.utils.networkMonitorService.NetworkObserver
import co.za.kasi.utils.webSocket.WebSocketListenerImplementation
import co.za.kasi.R
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class SkynetHome : AppCompatActivity() {


    internal lateinit var binding: ActivitySkynetHomeBinding
    private lateinit var service: SuperAppHttpService
    private var networkConnectivity: NetworkConnectivity? = null
    private val client = OkHttpClient()
    private lateinit var gpsStatusReceiver: GpsStatusReceiver

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkynetHomeBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        binding.customTitle.text = getString(R.string.home)
        binding.backButton.visibility = View.INVISIBLE
        alertDriverSyncNeeded()

        ReusableFunctions.checkAndRequestNotificationPermission(this@SkynetHome)

        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )

        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.skynet_color))
        window.statusBarColor = ContextCompat.getColor(this, R.color.skynet_color)

        init()

        if (LocationService1.isNetworkAvailable(this@SkynetHome)) {
            checkVersion()
            checkDeviceIdAndUpdateToken()
            checkIfSyncRequired()
        }
        checkVersion()

        networkConnectivity = NetworkConnectivity(applicationContext)
        networkConnectivity!!.observe().onEach {
            resetSignalImage("$it")
        }.launchIn(lifecycleScope)
        gpsStatusReceiver = GpsStatusReceiver { isGpsEnabled ->
            if (isGpsEnabled) {
                Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show()
                binding.txtGpsStatus.text = "Enabled"
            } else {
                Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show()
                binding.txtGpsStatus.text = "Disabled"
            }
        }

        binding.backButton.visibility = View.GONE

        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(gpsStatusReceiver, intentFilter)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_frame_layout1, SkyHomeFragment())
                .commit()
            binding.homeTabIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.home_icon_red
                )
            )
            binding.homeTabText.setTextColor(getColor(R.color.skynet_color))
        }

        binding.homeTab.setOnClickListener {
            replaceFragment(SkyHomeFragment())
            resetTabs()
            binding.homeTabIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.home_icon_red
                )
            )
            binding.homeTabText.setTextColor(getColor(R.color.skynet_color))
        }

        binding.syncDataTab.setOnClickListener {
            replaceFragment(SkyNetNewSyncFragment.newInstance("LOGGED_IN"))
            resetTabs()
            binding.syncDataIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.refresh_tab_icon_red
                )
            )
            binding.syncDataText.setTextColor(getColor(R.color.skynet_color))
        }

        binding.accountsTab.setOnClickListener {
            replaceFragment(SkyAccountFragment())
            resetTabs()
            binding.accountsTabIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.accounts_tab_icon_red
                )
            )
            binding.accountsTabText.setTextColor(getColor(R.color.skynet_color))
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    ActionConfirmationDialogFragment(
                        isIconNeeded = true,
                        isError = true,
                        buttonNegativeText = getString(R.string.stay),
                        buttonPositiveText = getString(R.string.exit),
                        title = getString(R.string.exiting_app),
                        message = getString(R.string.please_confirm_this_action_by_clicking_exit_or_stay_to_remain),
                        onPositive = {
                            finishAffinity()
                        },
                        onNegative = {}
                    ).show(supportFragmentManager, "exit_dialog")
                }
            }
        )

    }

    private fun init() {
        service = ReusableFunctions.initiateSuperAppRetrofit(this@SkynetHome)
    }

    fun replaceFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.home_frame_layout1)
        if (currentFragment != null && currentFragment.javaClass == fragment.javaClass) {
            return  // Prevent unnecessary transactions
        }
        binding.homeFrameLayout1.post {
            val fragmentManager = supportFragmentManager
            if (fragmentManager.isStateSaved) return@post
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.home_frame_layout1, fragment)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    private fun resetSignalImage(value: String) {

        val imageDraw = when (value) {

            NetworkObserver.Status.Available.name -> ContextCompat.getDrawable(
                this@SkynetHome,
                R.drawable.wifi_green
            )

            NetworkObserver.Status.Losing.name -> ContextCompat.getDrawable(
                this@SkynetHome,
                R.drawable.wifi_amber
            )

            NetworkObserver.Status.Lost.name -> ContextCompat.getDrawable(
                this@SkynetHome,
                R.drawable.wifi_amber
            )

            NetworkObserver.Status.Unavailable.name -> ContextCompat.getDrawable(
                this@SkynetHome,
                R.drawable.wifi_amber
            )

            else -> ContextCompat.getDrawable(this@SkynetHome, R.drawable.wifi_green)
        }

        binding.signalStatus.setImageDrawable(imageDraw)

    }

    internal fun resetTabs() {
        binding.homeTabIcon.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.home_icon
            )
        )
        binding.homeTabText.setTextColor(getColor(R.color.black))
        binding.syncDataIcon.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.refresh_icon
            )
        )
        binding.syncDataText.setTextColor(getColor(R.color.black))
        binding.accountsTabIcon.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.account_icon
            )
        )
        binding.accountsTabText.setTextColor(getColor(R.color.black))
    }

    fun connectWebSocket(driverId: String) {

        val request: Request = Request.Builder()
            .url("ws://88.99.94.84:8185/drivers-websocket")
            .build()

        val listener = WebSocketListenerImplementation()
        val ws: WebSocket = client.newWebSocket(request, listener)

    }

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun String.compareSemanticVersion(other: String): Int {
        val thisParts = this.split(".")
        val otherParts = other.split(".")

        val maxLength = maxOf(thisParts.size, otherParts.size)
        for (i in 0 until maxLength) {
            val thisPart = thisParts.getOrNull(i)?.toIntOrNull() ?: 0
            val otherPart = otherParts.getOrNull(i)?.toIntOrNull() ?: 0

            if (thisPart != otherPart) {
                return thisPart - otherPart
            }
        }

        return 0
    }

    fun checkVersion() {
        var currentAppVersion = this.packageManager.getPackageInfo(
            this.packageName,
            0
        ).versionName ?: "1.0.0"

        if (LocalStorage.getAppVersionCode().isNullOrBlank()) {
            updateVersionCode(currentAppVersion)
        } else if ((LocalStorage.getAppVersionCode()).compareSemanticVersion(
                currentAppVersion
            ) < 0
        ) {
            updateVersionCode(currentAppVersion)
        }
    }

    fun updateVersionCode(currentAppVersion: String) {

        val updateVersionCall = service.updateAppVersion(
            LocalStorage.getSuperAppToken(this),
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            currentAppVersion
        )

        updateVersionCall?.enqueue(object : Callback<AppUpdateResponse> {
            override fun onResponse(
                call: Call<AppUpdateResponse?>,
                response: Response<AppUpdateResponse?>
            ) {
                if (response.isSuccessful) {
                    LocalStorage.storeAppVersionCode(currentAppVersion)
//                        Toast.makeText(
//                            applicationContext,
//                            "App version updated",
//                            Toast.LENGTH_SHORT
//                        ).show()
                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(this@SkynetHome)
                        return
                    }
                }
            }

            override fun onFailure(
                call: Call<AppUpdateResponse?>,
                t: Throwable
            ) {

//                    Toast.makeText(
//                        applicationContext,
//                        getString(R.string.failed_to_get_rates_structure), Toast.LENGTH_SHORT
//                    )
//                        .show()
            }
        })

    }

    fun alertDriverSyncNeeded() {
        lifecycleScope.launch {
            SharedState.syncRequired.collect { isSyncRequired ->
                if (isSyncRequired) {
                    binding.syncDataNeeded.visibility = View.VISIBLE
                } else {
                    binding.syncDataNeeded.visibility = View.GONE
                }
            }
        }
    }

    fun checkDeviceIdAndUpdateToken() {
        val token = LocalStorage.getFcmToken()

        if (token != null) {
            val tokenBody = TokenUpdateBody(
                fcmToken = token,
                driverIdNo = LocalStorage.getSkynetDriverAccount().driver.identityNo,
                deviceId = LocalStorage.getOrCacheAndroidId(this)
            )
            sendTokenToServer(tokenBody)
        }
    }

    fun sendTokenToServer(tokenUpdateBody: TokenUpdateBody) {

        service.updateFcmToken(
            LocalStorage.getSuperAppToken(this),
            tokenUpdateBody
        )
            ?.enqueue(object : Callback<SkynetDriverAppLoginBodyResponse> {
                override fun onResponse(
                    call: Call<SkynetDriverAppLoginBodyResponse>,
                    response: Response<SkynetDriverAppLoginBodyResponse>
                ) {
                    if (response.isSuccessful) {
                        // Token updated successfully
                        Log.d("FCM", "Token updated successfully on server")
                    } else {
                        if (response.code() == 403) {
                            ReusableFunctions.handleBlockedUsers(this@SkynetHome)
                            return
                        }
                    }
                }

                override fun onFailure(call: Call<SkynetDriverAppLoginBodyResponse>, t: Throwable) {
                    // Network error
                    Log.e("FCM", "Network error: ${t.localizedMessage}")
                }
            })
    }

    override fun onResume() {
        super.onResume()
        //        if (getSupportFragmentManager().findFragmentById(R.id.home_frame_layout1) == null) {
//            replaceFragment(new HomeFragment());
//        }
    }

    private fun checkIfSyncRequired() {
        val driverIdNo = LocalStorage.getSkynetDriverAccount().driver.identityNo
        val token = LocalStorage.getSuperAppToken(this)
        val isAppSyncedCall = service.isDriverSynced(token, driverIdNo)

        isAppSyncedCall?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean?>, response: Response<Boolean?>) {
                Log.d("SyncCheck", "######Unsuccessful response: ${response.code()}")
                if (response.isSuccessful) {
                    val isSynced = response.body() == true
//                    lifecycleScope.launch {
//                        SharedState.updateSyncRequired(this@SkynetHome, isSynced)
//                    }
//
//                    if (isSynced) {
//                        alertDriverSyncNeeded()
//                    }
                } else {
                    Log.e("SyncCheck", "Unsuccessful response: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                Log.e("SyncCheck", "Failed to check sync: ${t.localizedMessage}")
            }
        })
    }

    companion object {

        fun launchHomeActivity(context: Context) {
            val intent = Intent(context, SkynetHome::class.java)
            context.startActivity(intent)
        }

    }
}