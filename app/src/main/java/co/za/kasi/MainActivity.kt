package co.za.kasi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import co.za.kasi.AccountAccessActivity.Companion.launchAccountAccessActivity
import co.za.kasi.databinding.LayoutSplashBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.enums.AppUpdateStatus
import co.za.kasi.fragments.UpdateBottomSheet
import co.za.kasi.model.AppVersionResponse
import co.za.kasi.model.accountDTO.AutoLoginDTO
import co.za.kasi.services.DriverHttpService
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.LocalStorage.storeAdvisedUpdateLastShow
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.R
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: LayoutSplashBinding
    private var service: SuperAppHttpService? = null
    private var snackBar: Snackbar? = null
    private var updateBottomSheet: UpdateBottomSheet? = null

    private val autoLoginDTO: AutoLoginDTO? = null
    private val services: DriverHttpService? = null
    private val loader: Loader? = null
    var intentFallBack: Intent? = null
    private lateinit var appVersionResponse: AppVersionResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()

        binding = LayoutSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)  // Set the content view early
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        service = ReusableFunctions.initiateSuperAppRetrofit(this)

        if (LocationService1.isNetworkAvailable(context = this)) {
            getAppVersion()
        } else {
            launchAccountAccessActivity(this@MainActivity)
            finish()
        }
        LocalStorage.getOrCacheAndroidId(this)
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishAffinity()
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
    }

    private fun handleUpdateBottomSheets(status: String, code: String, latestCritical: String) {

        var currentAppVersion = this.packageManager.getPackageInfo(
            this.packageName,
            0
        ).versionName ?: "1.0.0"

        if (currentAppVersion.compareSemanticVersion(latestCritical) < 0) {
            updateBottomSheet = UpdateBottomSheet(true) {
                this.finishAffinity()
            }
            updateBottomSheet!!.show(supportFragmentManager, "ForcedUpdateBottomSheet")
            return
        }

        if (currentAppVersion.compareSemanticVersion(code) < 0) {
            if (status == AppUpdateStatus.MINOR.name && shouldShowAdvisedUpdate()) {
                updateBottomSheet = UpdateBottomSheet(false) {
                    launchAccountAccessActivity(this@MainActivity)
                }
                updateBottomSheet!!.show(supportFragmentManager, "AdvisedUpdateBottomSheet")
                storeAdvisedUpdateLastShow(System.currentTimeMillis())
            } else if (status == AppUpdateStatus.CRITICAL.name) {
                updateBottomSheet = UpdateBottomSheet(true) {
                    this.finishAffinity()
                }
                updateBottomSheet!!.show(supportFragmentManager, "ForcedUpdateBottomSheet")
            } else {
                launchAccountAccessActivity(this@MainActivity)
                finish()
            }
        } else {
            launchAccountAccessActivity(this@MainActivity)
            finish()
        }

    }

    private fun getAppVersion() {

        val getAppVersionCall = service?.latestAppVersion

        getAppVersionCall?.enqueue(object : Callback<AppVersionResponse> {
            override fun onResponse(
                call: Call<AppVersionResponse?>,
                response: Response<AppVersionResponse?>
            ) {
                if (response.isSuccessful) {
                    appVersionResponse = response.body()!!
                    handleUpdateBottomSheets(
                        appVersionResponse.latestVersion.status,
                        appVersionResponse.latestVersion.code,
                        appVersionResponse.latestCriticalVersionCode
                    )
                } else {
                    if (response.errorBody() != null) {

                        try {
                            if (response.code() == 400) {
                                val errorResponse = ReusableFunctions.convertErrorResponse(
                                    response.errorBody()!!.string()
                                )
                                snackBar = ReusableFunctions.snackbarInstance(
                                    binding.root,
                                    errorResponse.message,
                                    Snackbar.LENGTH_INDEFINITE,
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.snackbar_red
                                    ),
                                    ContextCompat.getColor(applicationContext, R.color.white),
                                    Gravity.BOTTOM
                                )
                                snackBar?.show()
                                ReusableFunctions.dismisSnackBar(snackBar)
                            } else {
                                intentFallBack!!.putExtra(
                                    "error",
                                    "Technical Error occurred. Please try in few minutes."
                                )
                                try {
                                    startActivity(intentFallBack!!)
                                } catch (e: Exception) {
                                    return
                                }

                                finish()
                            }
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<AppVersionResponse?>,
                t: Throwable
            ) {
                launchAccountAccessActivity(this@MainActivity)
                finish()
            }
        })
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

    fun shouldShowAdvisedUpdate(): Boolean {
        val lastShown = LocalStorage.getAdvisedUpdateLastShow()
        val now = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000
        return (now - lastShown) >= oneDayInMillis
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED


            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}