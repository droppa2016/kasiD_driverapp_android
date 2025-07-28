package co.za.kasi

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import co.za.kasi.adapters.WaybillAdapter
import co.za.kasi.databinding.ActivityDeliveriesListBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.dialogs.Loader
import co.za.kasi.dialogs.ScanBarcodeDialog
import co.za.kasi.model.superApp.a.waybillData.Coordinate
import co.za.kasi.model.superApp.a.waybillData.Trip
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.AppCache
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.CoordinateHelper
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.ReusableFunctions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import co.za.kasi.R

class DeliveriesList : AppCompatActivity() {

    private lateinit var binding: ActivityDeliveriesListBinding
    private lateinit var service: SuperAppHttpService
    private lateinit var adapter: WaybillAdapter
    private var loader: Loader? = null
    private var snackbar: Snackbar? = null
    private var btnBackButton: AppCompatImageButton? = null
    private lateinit var title: MaterialTextView
    private lateinit var location: CoordinateHelper
    private lateinit var coordinate: Coordinate
    private var waybillList: List<Waybills> = emptyList()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDeliveriesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        // getDriverWaybills()
        fetchWaybillsFromDatabase()

        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.skynet_color))

        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.statusBarColor = getColor(R.color.skynet_color)

        onBackPressedDispatcher.addCallback(this) {
            startActivity(Intent(this@DeliveriesList, SkynetHome::class.java))
        }

        binding.scanSearchLayout.setOnClickListener {
            val dialog = ScanBarcodeDialog()
            dialog.setOnBarcodeScannedListener { scannedValue ->
                //Toast.makeText(this, "Scanned: $scannedValue", Toast.LENGTH_SHORT).show()
                filterList(scannedValue)
            }
            dialog.show(supportFragmentManager, "BarcodeScanDialog")
        }

        binding.clearListBtn.setOnClickListener {
            adapter.setItemList(waybillList)
            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()
            binding.clearListBtn.visibility = View.INVISIBLE
            binding.scanSearchLayout.visibility = View.VISIBLE
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // adapter.setItemList(waybillList)
                    //Toast.makeText(this@DeliveriesList,"No waybills found.",Toast.LENGTH_SHORT).show()
                } else {
                    filterList(newText)
                }
                return true
            }
        })
    }

    private fun init() {
        Objects.requireNonNull(supportActionBar)?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custon_action_bar)
        title = supportActionBar!!.customView.findViewById(R.id.txtTitle)
        btnBackButton = supportActionBar!!.customView.findViewById(R.id.btnBack)
        title.setText("Delivery Waybills")
        service = ReusableFunctions.initiateSuperAppRetrofit(this@DeliveriesList)

        btnBackButton?.setOnClickListener {
            startActivity(Intent(this@DeliveriesList, SkynetHome::class.java))
        }

        binding.searchView.clearFocus()

        location = CoordinateHelper(this@DeliveriesList)

        location.getCurrentCoordinate { coordinate ->
            if (coordinate != null) {
                Log.d("Coordinate", "Lat: ${coordinate.latitude}, Lng: ${coordinate.longitude}")
                this.coordinate = Coordinate(coordinate.latitude, coordinate.longitude)
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkDriverTripStatus() {
        loader = ReusableFunctions.showLoader(this@DeliveriesList)

        val checkDriverTripStatus = service.startTrip(
            LocalStorage.getSuperAppToken(applicationContext),
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            coordinate
        )

        checkDriverTripStatus.enqueue(object : Callback<Trip> {
            override fun onResponse(call: Call<Trip>, response: Response<Trip>) {
                ReusableFunctions.hideLoader(loader)

                ReusableFunctions.convertErrorResponse(
                    response.errorBody()?.string()
                )

                if (response.code() == 200) {
                    LocalStorage.setDailyDriverTripStatus(true)
                    binding.startDeliveriesBtn.visibility = View.GONE
                    Toast.makeText(this@DeliveriesList, "Trip Started.", Toast.LENGTH_SHORT).show()

                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(this@DeliveriesList)
                        return
                    }
                }
            }

            override fun onFailure(call: Call<Trip>, t: Throwable) {
                ReusableFunctions.hideLoader(loader)
                Log.e("", "=================== Driver Started Trip  onfailure ==" + t.message)
                LocalStorage.setDailyDriverTripStatus(true)
            }
        })
    }

    private fun setupRecyclerView(waybillList: List<Waybills>) {
        adapter = WaybillAdapter(waybillList) { selectedWaybill ->

            if (LocalStorage.getDailyDriverTripStatus()) {
                AppCache.setCurrentWaybillList(null)
                AppCache.setCurrentWaybill(null)
                val intent = Intent(this, WaybillDetailActivity::class.java).apply {
                    putExtra("waybill", selectedWaybill)
                }
                startActivity(intent)
            } else {
                showStartTripDialog(this)
                Toast.makeText(
                    this@DeliveriesList,
                    "Please start trip in order to attend waybill.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.myRec.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = this@DeliveriesList.adapter
        }
    }

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getDriverWaybills() {

        if (LocationService1.isNetworkAvailable(applicationContext)) {

            loader = ReusableFunctions.showLoader(this@DeliveriesList)

            val driverWaybillCall = service.getDriverDeliveryWaybills(
                LocalStorage.getSuperAppToken(applicationContext),
                LocalStorage.getSkynetDriverAccount().driver.identityNo,
                getFormattedDate()
            )

            driverWaybillCall.enqueue(object : Callback<List<Waybills>> {

                override fun onResponse(
                    call: Call<List<Waybills>>,
                    response: Response<List<Waybills>>
                ) {

                    ReusableFunctions.hideLoader(loader)

                    if (response.code() == 200) {
                        val waybills = response.body()

                        if (waybills != null) {
                            for ((index, waybill) in waybills.withIndex()) {
                                if (waybill.deliveryConditions!!.isNotEmpty() && waybill.serviceType != "COU") {
                                    Log.d("WAYBILL_LIST", "Waybill $index : $waybill")

                                }
                            }
                            setupRecyclerView(waybills.sortedBy { it.deliveryDate })
                            waybillList = waybills
                        }

                    } else {
                        if (response.code() == 403) {
                            ReusableFunctions.handleBlockedUsers(this@DeliveriesList)
                            return
                        }

                        val errorResponse = ReusableFunctions.convertErrorResponse(
                            response.errorBody()!!.string()
                        )

                        snackbar = ReusableFunctions.snackbarInstance(
                            binding.startDeliveriesBtn,
                            errorResponse.message,
                            Snackbar.LENGTH_INDEFINITE,
                            getColor(R.color.snackbar_red),
                            getColor(R.color.white),
                            Gravity.BOTTOM
                        )
                        snackbar?.show()
                    }
                }

                override fun onFailure(call: Call<List<Waybills>>, t: Throwable) {
                    ReusableFunctions.hideLoader(loader)
                    Log.e("TAG_error", "onFailure: " + t.message, t)
                }
            })
        } else {
            fetchWaybillsFromDatabase()
        }
    }

    private fun showStartTripDialog(context: Context) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.start_deliveries_widget, null)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnPositive = dialogView.findViewById<AppCompatButton>(R.id.btnStartTrip)
        val btnNegative = dialogView.findViewById<AppCompatButton>(R.id.btnDismiss)
        btnPositive.setOnClickListener {
            checkDriverTripStatus()
            // LocalStorage.setDailyDriverTripStatus(true)
            //  Toast.makeText(context, "Starting trip...", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        btnNegative.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchWaybillsFromDatabase() {
        val db = AppDatabase.getDatabase(applicationContext)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val waybills = db.waybillDao().getWaybills()
                withContext(Dispatchers.Main) {
                    if (waybills.isEmpty()) {
                        Toast.makeText(
                            applicationContext,
                            "No waybills available offline",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        AppCache.setCurrentWaybillList(null)
                        AppCache.setCurrentWaybill(null)

                        setupRecyclerView(
                            waybills
                                .filter { it.status != "DELIVERED" }
                                .sortedWith(
                                compareBy({ it.consignee?.town ?: "ZZZ" },
                                    {it.consignee?.addressLine1 ?: "ZZZ"},{ it.number })
                            )
                        )
                                    waybillList = waybills

                    }
                }
            } catch (e: Exception) {
                Log.e("Database Error", "Failed to fetch waybills from database: ${e.message}")
            }
        }
    }

    private fun filterList(text: String) {
        val filteredList = waybillList.filter { waybill ->
            waybill.number.contains(text, ignoreCase = true) ||
                    waybill.serviceType?.contains(text, ignoreCase = true) == true ||
                    waybill.clientReference?.contains(text, ignoreCase = true) == true ||
                    waybill.consignee?.town?.contains(text, ignoreCase = true) == true ||
                    waybill.parcels?.any { it.number!!.contains(text, ignoreCase = true) } == true
        }

        adapter.setItemList(filteredList)
        binding.clearListBtn.visibility = View.VISIBLE
        binding.scanSearchLayout.visibility = View.INVISIBLE

        if (filteredList.isEmpty()) {
            Toast.makeText(
                this@DeliveriesList,
                "Not found",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun findWaybillByParcelNumber(
        waybills: List<Waybills>,
        parcelNumber: String
    ): Waybills? {
        return waybills.firstOrNull { waybill ->
            waybill.parcels?.any { it.number == parcelNumber } == true
        }
    }
}