package co.za.kasi

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import co.za.kasi.FirstLoginSyncActivity.Companion.launchSyncActivity
import co.za.kasi.databinding.ActivityVehicleRegistrationSelectionBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriver
import co.za.kasi.model.superApp.a.vehicle.DriverVehicleAssignBody
import co.za.kasi.model.superApp.a.vehicle.SkynetVehicle
import co.za.kasi.model.superApp.a.waybillData.Coordinate
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.CoordinateHelper
import co.za.kasi.utils.GpsStatusReceiver
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.utils.networkMonitorService.NetworkConnectivity
import co.za.kasi.utils.networkMonitorService.NetworkObserver
import co.za.kasi.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleRegistrationSelection : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleRegistrationSelectionBinding
    private lateinit var service: SuperAppHttpService
    private var regNum: String? = null
    private var loader: Loader? = null
    private var networkConnectivity: NetworkConnectivity? = null
    private var snackbar: Snackbar? = null
    private lateinit var location: CoordinateHelper
    private lateinit var coordinate: Coordinate
    private lateinit var skynetVehicle: SkynetVehicle

    private lateinit var gpsStatusReceiver: GpsStatusReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleRegistrationSelectionBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        enableEdgeToEdge()
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // Add bottom padding when keyboard is visible
            view.setPadding(
                0,
                0,
                0,
                maxOf(imeInsets.bottom, navInsets.bottom)
            )
            insets
        }
        init()

        networkConnectivity = NetworkConnectivity(applicationContext)
        networkConnectivity!!.observe().onEach {
            resetSignalImage("$it")
        }.launchIn(lifecycleScope)

        gpsStatusReceiver = GpsStatusReceiver { isGpsEnabled ->
            if (isGpsEnabled) {
                Toast.makeText(this, getString(R.string.gps_is_enabled), Toast.LENGTH_SHORT).show()
                binding.txtGpsStatus.text = getString(R.string.enabled)
            } else {
                Toast.makeText(this, getString(R.string.gps_is_disabled), Toast.LENGTH_SHORT).show()
                binding.txtGpsStatus.text = getString(R.string.disabled)
            }
        }

        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(gpsStatusReceiver, intentFilter)

        binding.btnSubmit.setOnClickListener {
            if (binding.btnSubmit.text == getString(R.string.search)) {
                if (TextUtils.isEmpty(binding.vehicleRegNumSelect.getText())) {
                    highlightInputs()
                } else {
                    loader = ReusableFunctions.showLoader(this@VehicleRegistrationSelection)
                    submitVehicleRegistration(binding.vehicleRegNumSelect.getText().toString())
                }
            } else if (binding.btnSubmit.text == getString(R.string.assign)) {
                regNum?.let { it1 -> assignVehicle(it1) }
            }
        }

        binding.vehicleRegNumSelect.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.btnSubmit.text = getString(R.string.search)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun init() {
        service = ReusableFunctions.initiateSuperAppRetrofit(this@VehicleRegistrationSelection)
        binding.headerUn.unauthorizedHeading.setText(R.string.vehicle_registration)

        location = CoordinateHelper(this@VehicleRegistrationSelection)

        location.getCurrentCoordinate { coordinate ->
            if (coordinate != null) {
                Log.d("Coordinate", "Lat: ${coordinate.latitude}, Lng: ${coordinate.longitude}")
                this.coordinate = Coordinate(coordinate.latitude, coordinate.longitude)
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun highlightInputs() {
        if (TextUtils.isEmpty(binding.vehicleRegNumSelect.getText().toString())) {
            binding.vehicleRegNumSelect.background =AppCompatResources.getDrawable(this,R.drawable.text_background_error)
            binding.vehicleRegNumSelect.startAnimation(ReusableFunctions.shakeError())
            binding.vehicleRegNumSelect.error = "Registration Number is required"
        }
    }

    private fun resetSignalImage(value: String) {

        val imageDraw = when (value) {

            NetworkObserver.Status.Available.name -> ContextCompat.getDrawable(
                this@VehicleRegistrationSelection,
                R.drawable.wifi_green
            )

            NetworkObserver.Status.Losing.name -> ContextCompat.getDrawable(
                this@VehicleRegistrationSelection,
                R.drawable.wifi_amber
            )

            NetworkObserver.Status.Lost.name -> ContextCompat.getDrawable(
                this@VehicleRegistrationSelection,
                R.drawable.wifi_amber
            )

            NetworkObserver.Status.Unavailable.name -> ContextCompat.getDrawable(
                this@VehicleRegistrationSelection,
                R.drawable.wifi_amber
            )

            else -> ContextCompat.getDrawable(
                this@VehicleRegistrationSelection,
                R.drawable.wifi_green
            )
        }

        binding.signalStatus.setImageDrawable(imageDraw)

    }

    private fun submitVehicleRegistration(vehicleReg: String) {

        val selectVehicleCall = service.driverVehicleSelection(
            LocalStorage.getSkynetDriverAccount().tokenResponse.token,
            vehicleReg
        )
        selectVehicleCall.enqueue(object : Callback<SkynetVehicle?> {
            override fun onResponse(
                call: Call<SkynetVehicle?>,
                response: Response<SkynetVehicle?>
            ) {
                ReusableFunctions.hideLoader(loader)
                Log.e("", "===================response vehicle select ==" + response.code())
                if (response.code() == 200) {
                    skynetVehicle = response.body()!!

                    assert(response.body() != null)
                    binding.txtVehicleRegNumber.text = response.body()!!.registration_number
                    regNum = response.body()!!.registration_number
                    binding.txtRegVehicleType.text = response.body()!!.make
                    binding.txtRegVehicleColor.text = response.body()!!.model
                    binding.lytVehicleRegDetails.visibility = View.VISIBLE
                    binding.btnSubmit.text = getString(R.string.assign)
                    binding.vehicleRegNumSelect.clearFocus()

                } else {

                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(this@VehicleRegistrationSelection)
                        return
                    }

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    snackbar = ReusableFunctions.snackbarInstance(
                        binding.btnSubmit, errorResponse.message, Snackbar.LENGTH_SHORT,
                        getColor(R.color.snackbar_red),
                        getColor(R.color.white),
                        Gravity.BOTTOM
                    )

                    snackbar?.show()
                }
            }

            override fun onFailure(call: Call<SkynetVehicle?>, t: Throwable) {
                ReusableFunctions.hideLoader(loader)
            }
        })


    }

    private fun assignVehicle(regNum: String) {
        loader = ReusableFunctions.showLoader(this@VehicleRegistrationSelection)
        Log.e("", "===================reg num =$regNum")
        val data = DriverVehicleAssignBody(
            LocalStorage.getSkynetDriverAccount().driver.id,
            regNum,
            coordinate
        )
        Log.e("", "===================reg num =$data")
        val vehicleAssignCall =
            service.driverAssignVehicle(LocalStorage.getSuperAppToken(applicationContext), data)
        val token = LocalStorage.getSuperAppToken(applicationContext)
        Log.e("", "===================Token = $token")
        vehicleAssignCall.enqueue(object : Callback<SkynetDriver?> {


            override fun onResponse(call: Call<SkynetDriver?>, response: Response<SkynetDriver?>) {
                ReusableFunctions.hideLoader(loader)
                Log.e("", "===================response vehicle assign ==" + response.code())

                if (response.code() == 200) {
                    LocalStorage.storeSkynetDriverVehicle(skynetVehicle)
                    launchSyncActivity(this@VehicleRegistrationSelection)
                } else {

                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(this@VehicleRegistrationSelection)
                        return
                    }

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    snackbar = ReusableFunctions.snackbarInstance(
                        binding.btnSubmit, errorResponse.message, Snackbar.LENGTH_SHORT,
                        getColor(R.color.snackbar_red),
                        getColor(R.color.white),
                        Gravity.BOTTOM
                    )

                    snackbar?.show()
                }
            }

            override fun onFailure(call: Call<SkynetDriver?>, t: Throwable) {

                snackbar = ReusableFunctions.snackbarInstance(
                    binding.btnSubmit, t.message, Snackbar.LENGTH_SHORT,
                    getColor(R.color.snackbar_red),
                    getColor(R.color.white),
                    Gravity.BOTTOM
                )

                snackbar?.show()
            }
        })
    }

    companion object{
        fun launchVehicleRegistrationSelection(context: Context) {
            val intent = Intent(context, VehicleRegistrationSelection::class.java)
            context.startActivity(intent)
        }
    }
}