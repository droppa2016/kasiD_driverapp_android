package co.za.kasi.fragments.bottomnav

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import co.za.kasi.R
import co.za.kasi.SkynetHome
import co.za.kasi.databinding.FragmentSkySyncBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.ReusableFunctions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SkySyncFragment : Fragment() {

    private var _binding: FragmentSkySyncBinding? = null
    private val binding get() = _binding!!
    private var service: SuperAppHttpService? = null
    private lateinit var safeActivity: Activity
    private lateinit var safeContext: Context
    private val snackbar: Snackbar? = null
    private var loader: Loader? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSkySyncBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var homeActivity = activity as SkynetHome

        init()
        safeActivity = requireActivity()
        safeContext = requireContext()

        binding.btnRefresh.setOnClickListener {
            homeActivity.binding.syncDataNeeded.visibility = View.GONE
            if (LocationService1.isNetworkAvailable(safeContext)) {

                getDeliveryWaybills()
                getDriverWaybillsFailed()
                getDriverWaybillsSuccessful()
                //setDriverSynced()

            } else {
                Toast.makeText(
                    safeContext,
                    "Network not available. Application will sync once a network connection has been established. ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                val homeActivity = activity as SkynetHome
                override fun handleOnBackPressed() {
                    homeActivity.replaceFragment(SkyHomeFragment())
                    homeActivity.resetTabs()
                    homeActivity.binding.homeTabIcon.setImageDrawable(
                        AppCompatResources.getDrawable(
                            safeContext,
                            R.drawable.home_icon_red
                        )
                    )
                    homeActivity.binding.homeTabText.setTextColor(
                        getColor(
                            safeContext,
                            R.color.skynet_color
                        )
                    )

                    homeActivity.binding.syncDataText.setTextColor(
                        getColor(
                            safeContext,
                            R.color.black
                        )
                    )
                    homeActivity.binding.syncDataIcon.setImageDrawable(
                        AppCompatResources.getDrawable(
                            safeContext,
                            R.drawable.refresh_icon
                        )
                    )
                }
            }
        )


    }

    private fun init() {
        service = ReusableFunctions.initiateSuperAppRetrofit(context)
    }

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getDeliveryWaybills() {

        loader = ReusableFunctions.showLoader(safeContext)

        val driverWaybillCall = service?.getDriverDeliveryWaybills(
            LocalStorage.getSuperAppToken(safeContext),
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            getFormattedDate()
        )

        driverWaybillCall?.enqueue(object : Callback<List<Waybills>> {

            override fun onResponse(
                call: Call<List<Waybills>>,
                response: retrofit2.Response<List<Waybills>>
            ) {

                ReusableFunctions.hideLoader(loader)

                if (response.code() == 200) {
                    lifecycleScope.launch {

                    }
                    val trip = response.body()

                    if (trip != null) {
                        Log.e("", "----------------------------- total waybills = ${trip.size}")

                        val db = AppDatabase.getDatabase(safeContext)
                        val waybillDao = db.waybillDao()

                        CoroutineScope(Dispatchers.IO).launch {
                            waybillDao.deleteAllWaybills()
                            val waybillEntities = trip.map { waybill ->
                                Waybills(
                                    number = waybill.number,
                                    serviceType = waybill.serviceType,
                                    date = waybill.date,
                                    status = waybill.status,
                                    statusDescription = waybill.statusDescription,
                                    deliveryBranch = waybill.deliveryBranch,
                                    deliveryDate = waybill.deliveryDate,
                                    deliveryConditions = waybill.deliveryConditions,
                                    accountNumber = waybill.accountNumber,
                                    parcels = waybill.parcels,
                                    sender = waybill.sender,
                                    captureDate = waybill.captureDate,
                                    clientReference = waybill.clientReference,
                                    consignee = waybill.consignee,
                                    specialInstructions = waybill.specialInstructions,
                                    orderNumber = waybill.orderNumber,
                                    options = waybill.options,
                                    consolidated = waybill.consolidated,
                                    tripNumber = waybill.tripNumber,
                                    consolidationId = waybill.consolidationId
                                )
                            }
                            waybillDao.insertWaybills(waybillEntities)
                        }

                    }
                    Toast.makeText(safeContext, "Sync Complete", Toast.LENGTH_SHORT).show()

                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }
                }
            }

            override fun onFailure(call: Call<List<Waybills>>, t: Throwable) {
                Log.e("API_RESPONSE", "-----------------  on failure Error: ${t.localizedMessage}")
                Log.e("TAG_error", "onFailure: " + t.message, t)
            }
        })
    }

    private fun getDriverWaybillsFailed() {

        //loader = ReusableFunctions.showLoader(requireContext())

        val driverWaybillCall = service?.getDriverWaybillsConditional(
            LocalStorage.getSuperAppToken(safeContext),
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            getFormattedDate(),
            "DELIVERY_FAILURE"
        )

        Log.e("", "TOKEN= " + LocalStorage.getSuperAppToken(safeContext))
        Log.e("", "DRIVER ID= " + LocalStorage.getSkynetDriverAccount().driver.id)
        Log.e("", "DATE= " + getFormattedDate())

        driverWaybillCall?.enqueue(object : Callback<List<Waybills>> {

            override fun onResponse(
                call: Call<List<Waybills>>,
                response: retrofit2.Response<List<Waybills>>
            ) {

                Log.e("", "=================== waybill condition ==" + response.code())
                if (response.code() == 200) {
                    val trip = response.body()

                    if (trip != null) {

                    }
                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }
                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    Log.e(
                        "API_ERROR",
                        "Failed to fetch waybills, Response message: ${errorResponse.message}"
                    )
                }
            }

            override fun onFailure(call: Call<List<Waybills>>, t: Throwable) {
                Log.e("API_RESPONSE", "-----------------  on failure Error: ${t.localizedMessage}")
                Log.e("TAG_error", "onFailure: " + t.message, t)
            }
        })
    }

    private fun getDriverWaybillsSuccessful() {

        // loader = ReusableFunctions.showLoader(requireContext())

        val driverWaybillCall = service?.getDriverWaybillsConditional(
            LocalStorage.getSuperAppToken(safeContext),
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            getFormattedDate(),
            "DELIVERED"
        )

        Log.e("", "TOKEN= " + LocalStorage.getSuperAppToken(safeContext))
        Log.e("", "DRIVER ID= " + LocalStorage.getSkynetDriverAccount().driver.id)
        Log.e("", "DATE= " + getFormattedDate())

        driverWaybillCall?.enqueue(object : Callback<List<Waybills>> {

            override fun onResponse(
                call: Call<List<Waybills>>,
                response: retrofit2.Response<List<Waybills>>
            ) {

                //   loader?.hide()
                Log.e("", "=================== successful waybill  ==" + response.code())
                if (response.code() == 200) {
                    val trip = response.body()

                    if (trip != null) {

                    }

                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    Log.e(
                        "API_ERROR",
                        "Failed to fetch waybills, Response message: ${errorResponse.message}"
                    )
                }
            }

            override fun onFailure(call: Call<List<Waybills>>, t: Throwable) {
                Log.e("API_RESPONSE", "-----------------  on failure Error: ${t.localizedMessage}")
                Log.e("TAG_error", "onFailure: " + t.message, t)
            }
        })
    }

    private fun setDriverSynced() {

        val driverWaybillCall = service?.setDriverSynced(
            LocalStorage.getSuperAppToken(safeContext),
            LocalStorage.getSkynetDriverAccount().driver.identityNo
        )

        driverWaybillCall?.enqueue(object : Callback<String> {

            override fun onResponse(
                call: Call<String>,
                response: retrofit2.Response<String>
            ) {

                ReusableFunctions.hideLoader(loader)

                if (response.code() == 200) {
                    lifecycleScope.launch {
//                        SharedState.updateSyncRequired(
//                            safeContext,
//                            false
//                        )
                    }
                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("API_RESPONSE", "-----------------  on failure Error: ${t.localizedMessage}")
                Log.e("TAG_error", "onFailure: " + t.message, t)
            }
        })
    }
}