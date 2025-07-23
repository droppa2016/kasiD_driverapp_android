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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import co.za.kasi.databinding.ActivityWaybillDetailBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.db.sharedPreferance.PendingWaybillsStorage
import co.za.kasi.dialogs.DeliveryIssueDialog
import co.za.kasi.dialogs.Loader
import co.za.kasi.dialogs.OnWaybillsScannedListener
import co.za.kasi.dialogs.ScanWaybillDialog
import co.za.kasi.model.superApp.a.waybillData.Coordinate
import co.za.kasi.model.superApp.a.waybillData.DeliveryCondition
import co.za.kasi.model.superApp.a.waybillData.DriverEventDTO
import co.za.kasi.model.superApp.a.waybillData.WaybillAttending
import co.za.kasi.model.superApp.a.waybillData.WaybillRequest
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.AppCache
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.CoordinateHelper
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.utils.SharedState
import co.za.kasi.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.util.Objects


class WaybillDetailActivity : AppCompatActivity(), OnWaybillsScannedListener {

    private lateinit var binding: ActivityWaybillDetailBinding
    private lateinit var service: SuperAppHttpService
    private var loader: Loader? = null
    private var snackbar: Snackbar? = null
    var waybill: Waybills? = null
    private var btnBackButton: AppCompatImageButton? = null
    private lateinit var title: MaterialTextView
    private lateinit var location: CoordinateHelper
    private var waybillList: List<Waybills> = emptyList()
    private var waybillCounters: MutableList<Waybills> = mutableListOf()

    private lateinit var coordinate: Coordinate

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaybillDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.skynet_color))

        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.statusBarColor = getColor(R.color.skynet_color)

        waybill = if (AppCache.currentWaybillList == null) {
            intent.getSerializableExtra("waybill") as? Waybills
        } else {
            AppCache.getCurrentWaybillList()[0]
        }

        binding.tvAccountNumber.text = waybill?.parcels?.size.toString()
        binding.tvCompanyName.text = waybill?.consignee?.name
        binding.tvContact.text = waybill?.consignee?.contact

        binding.tvPhoneNo.text = waybill?.consignee?.telephoneNumber

        if (waybill?.consignee?.contact == waybill?.consignee?.name || waybill?.consignee?.contact == "") {
            binding.lytContact.visibility = View.GONE
        }

        Log.d("Consignee", "onCreate: " + waybill.toString())

        binding.tvPickUpAddress.text =
            "${waybill?.sender?.addressLine1} ${waybill?.sender?.addressLine2}"
        binding.tvDroppOffAddress.text =
            "${waybill?.consignee?.addressLine1}, ${waybill?.consignee?.addressLine2}, ${waybill?.consignee?.addressLine3}, ${waybill?.consignee?.town}, ${waybill?.consignee?.postalCode} "
        binding.tvWaybillNumber.text = waybill?.number
        binding.tvSpecialInstractions.text = waybill?.specialInstructions
        binding.tvPickUpDate.text = waybill?.deliveryDate?.substring(0, 10)
        binding.tvTime.text = waybill?.deliveryDate?.substring(11, 16)
        binding.tvReference.text = waybill?.clientReference

        if (waybill?.clientReference == "" || waybill?.clientReference == null) {
            binding.tvReference.visibility = View.GONE
            binding.lytRefHeader.visibility == View.GONE
        }

        binding.tvServiceType.text = waybill?.serviceType


        binding.btnGoToPickUp.setOnClickListener {

            if (waybill!!.serviceType?.equals("COU") == true) {

                waybillCounters.add(waybill!!)


                waybillList.forEach {

                    if (it.tripNumber == waybill?.tripNumber && it.serviceType == waybill?.serviceType && it.consolidationId == waybill?.consolidationId && waybill?.deliveryConditions?.size!! < 2 && it.number != waybill?.number) {
                        waybillCounters.add(it)

                        val p = it.tripNumber + ", " + it.serviceType

                        Log.e("", "-----------------------------  p = ${p}")
                        Log.e("", "-----------------------------  p = $waybillCounters")


                    }

                }

                AppCache.setCurrentWaybillList(null)

                Log.e("", "-----------------------------  p5678 = $waybillCounters")
                AppCache.setCurrentWaybillList(waybillCounters)

                if (AppCache.getCurrentWaybillList() != null) {

                    showBulkDeliveryDialog(this@WaybillDetailActivity, waybill?.consolidationId!!)

                }

                val waybillsWithTrip =
                    waybillList.filter { it.tripNumber == waybill?.tripNumber && it.serviceType == waybill?.serviceType }

                Toast.makeText(
                    this,
                    "Total waybills at counter - ${waybillCounters.size}",
                    Toast.LENGTH_SHORT
                ).show()


            } else {
                if (waybill!!.deliveryConditions?.size == 0) {
                    showResultDialog(waybill!!.number)
                } else {
                    lifecycleScope.launch {
                        val waybills = waybill?.let { it1 -> listOf(it1.number) }?.let { it2 ->
                            getWaybillsFromDatabase(
                                this@WaybillDetailActivity,
                                it2
                            )
                        }



                        if (waybills != null) {
                            waybill?.let { it1 -> listOf(it1.number) }
                                ?.let { it2 -> attendWaybill(it2, waybills, emptyList()) }
                        }
                    }
                }

            }
        }

        binding.unableToDeliver.setOnClickListener {

            val dialog = DeliveryIssueDialog()
            dialog.setOnResult { reason, base64,name, notes ->
                submitFailedWaybill(reason, base64,name)
            }
            dialog.show(supportFragmentManager, "DeliveryIssueDialog")

        }

        onBackPressedDispatcher.addCallback(this) {
            startActivity(Intent(this@WaybillDetailActivity, DeliveriesList::class.java))
            AppCache.setCurrentWaybillList(null)
            AppCache.setCurrentWaybill(null)

        }

    }

    private fun init() {

        Objects.requireNonNull(supportActionBar)?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custon_action_bar)
        title = supportActionBar!!.customView.findViewById(R.id.txtTitle)
        btnBackButton = supportActionBar!!.customView.findViewById(R.id.btnBack)
        title.setText("Waybill Details")
        service = ReusableFunctions.initiateSuperAppRetrofit(this@WaybillDetailActivity)


        btnBackButton?.setOnClickListener {
            startActivity(Intent(this@WaybillDetailActivity, DeliveriesList::class.java))
            AppCache.setCurrentWaybillList(null)
            AppCache.setCurrentWaybill(null)
        }

        location = CoordinateHelper(this@WaybillDetailActivity)
        fetchWaybillsFromDatabase()

        location.getCurrentCoordinate { coordinate ->

            if (coordinate != null) {
                Log.d("Coordinate", "Lat: ${coordinate.latitude}, Lng: ${coordinate.longitude}")
                this.coordinate = Coordinate(coordinate.latitude, coordinate.longitude)
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun attendWaybill(waybillNumber: List<String>, way: List<Waybills>, parcelsScanned: List<String>) {
        loader = ReusableFunctions.showLoader(this@WaybillDetailActivity)




        var attendWaybill = service.attendWaybill(
            LocalStorage.getSuperAppToken(applicationContext),
            WaybillAttending(
                waybillNumber,
                LocalStorage.getSkynetDriverAccount().driver.id,
                LocalStorage.getOrCacheAndroidId(this@WaybillDetailActivity),
                coordinate
            )
        )


        attendWaybill.enqueue(object : Callback<DriverEventDTO> {
            override fun onResponse(
                call: Call<DriverEventDTO>,
                response: Response<DriverEventDTO>
            ) {

                ReusableFunctions.hideLoader(loader)

                Log.e("", "===================attend waybill ==" + response.code())
                if (response.isSuccessful) {


                    val intent =
                        Intent(this@WaybillDetailActivity, SecureDelivery::class.java).apply {
                            putExtra("waybill", waybill)
                        }

                    AppCache.setCurrentWaybillList(way)
                    AppCache.setParcelsScanned(parcelsScanned)

                    startActivity(intent)

                } else {

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    snackbar = ReusableFunctions.snackbarInstance(
                        binding.btnGoToPickUp, errorResponse.message, 2500,
                        getColor(R.color.snackbar_red),
                        getColor(R.color.white),
                        Gravity.TOP
                    )

                    Log.e("attend 403 ", "=========error=== ${errorResponse.message}")

                    snackbar?.show()

                }
            }

            override fun onFailure(call: Call<DriverEventDTO>, t: Throwable) {
                ReusableFunctions.hideLoader(loader)
                val intent = Intent(this@WaybillDetailActivity, SecureDelivery::class.java).apply {
                    putExtra("waybill", waybill)
                }

                AppCache.setCurrentWaybillList(way)

                startActivity(intent)
            }

        })
    }


    fun showDeliveryIssueDialog(context: Context, onIssueSelected: (String) -> Unit) {
        val issues = arrayOf(
            "Vehicle breakdown", "Robbery / Hijacking", "Missed cut-off",
            "Appointment requested", "Company closed", "Wrong product", "Industrial Action",
            "Client refused delivery", "Consignee not available", "Parcel damaged",
            "Insufficient documents", "Bad address"
        )

        var selectedIssue = issues[0] // Default selection

        AlertDialog.Builder(context)
            .setTitle("Select Delivery Issue")
            .setSingleChoiceItems(issues, -1) { _, which ->
                selectedIssue = issues[which]
            }
            .setPositiveButton("OK") { _, _ ->
                onIssueSelected(selectedIssue)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun submitFailedWaybill(selectedIssue: String, base64: String,name: String) {

        loader = ReusableFunctions.showLoader(this@WaybillDetailActivity)

        val dc = DeliveryCondition("FAILED", "base64","image", listOf(base64), LocalDateTime.now().toString())
        val body = WaybillRequest(
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            waybill?.number.toString(),
            "FAILED",
            selectedIssue,
            emptyList(),
            listOf(dc),
            coordinate,
            "", LocalStorage.getOrCacheAndroidId(this@WaybillDetailActivity),name
        )

        if (!LocationService1.isNetworkAvailable(this@WaybillDetailActivity)) {
            PendingWaybillsStorage.saveWaybill(body)
            PendingWaybillsStorage.saveFailedWaybill(waybill!!)

            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(this@WaybillDetailActivity)
                    .waybillDao()
                    .deleteWaybillByNumber(body.waybillNumber)
            }

            lifecycleScope.launch {
                SharedState.updateAwaitingSync(
                    this@WaybillDetailActivity,
                    SharedState.awaitingSync.value + 1
                )
            }

            Toast.makeText(this, "No Internet. Delivery failure saved offline.", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(this, DeliveriesList::class.java))
            return
        }

        val submitWaybillCall =
            service.submitWaybill(LocalStorage.getSuperAppToken(this@WaybillDetailActivity), body)

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonBody = gson.toJson(body)


        Log.d("WAYBILL_REQUEST_BODY", jsonBody)

        Log.e("", "================= submitWaybillCall== ${submitWaybillCall.toString()}")

        submitWaybillCall.enqueue(object : Callback<Waybills> {
            override fun onResponse(call: Call<Waybills>, response: Response<Waybills>) {


                ReusableFunctions.hideLoader(loader)
                Log.e("", "================= waybill submit FAILURE == ${response.code()}")

                if (response.code() == 200) {
                   // Log.e("", "================= waybill submit == ${response.code()}")

                    startActivity(Intent(this@WaybillDetailActivity, SkynetHome::class.java))
                    AppCache.setCurrentWaybillList(null)
                    AppCache.setCurrentWaybill(null)
                } else {
                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )


                    snackbar = ReusableFunctions.snackbarInstance(
                        binding?.unableToDeliver, errorResponse.message, 2500,
                        getColor(R.color.snackbar_red),
                        getColor(R.color.white),
                        Gravity.TOP
                    )

                    snackbar?.show()
                }

            }

            override fun onFailure(call: Call<Waybills>, t: Throwable) {
                ReusableFunctions.hideLoader(loader)
            }

        })

    }

    suspend fun getWaybillsFromDatabase(context: Context, numbers: List<String>): List<Waybills> {
        val db = AppDatabase.getDatabase(context)
        return numbers.mapNotNull { number ->
            db.waybillDao().getWaybillByNumber(number)
        }
    }


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
                        waybillList = waybills
                        Log.e("", "=********************** waybillList size ${waybillList.size}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Database Error", "Failed to fetch waybills from database: ${e.message}")
            }
        }
    }

    fun showBulkDeliveryDialog(context: Context, message: String) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.counter_bulk_delivery_widget, null)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val mainTextView = dialogView.findViewById<TextView>(R.id.main_text)
        mainTextView.text = message

        val button = dialogView.findViewById<Button>(R.id.btnBulkDelivery)
        val buttonCancel = dialogView.findViewById<Button>(R.id.btnCancelBulkDelivery)

        button.setOnClickListener {

           attendWaybill(waybillCounters.map { it.number }, waybillCounters, emptyList())

        }

        buttonCancel.setOnClickListener {
            waybillCounters.clear()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun showResultDialog(barcodeValue: String) {

        runOnUiThread {
            val dialogView =
                LayoutInflater.from(this@WaybillDetailActivity)
                    .inflate(R.layout.start_deliveries_widget, null)

            val alertDialog = AlertDialog.Builder(this@WaybillDetailActivity)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val btnPositive = dialogView.findViewById<AppCompatButton>(R.id.btnStartTrip)
            btnPositive.text = "No"
            val btnNegative = dialogView.findViewById<AppCompatButton>(R.id.btnDismiss)
            btnNegative.setBackgroundDrawable(getDrawable(R.drawable.button_background_red_borders))
            btnNegative.text = "Yes"
            btnNegative.setTextColor(getColor(R.color.skynet_color))
            val heading = dialogView.findViewById<MaterialTextView>(R.id.alertTitle)
            heading.text = barcodeValue
            val message = dialogView.findViewById<MaterialTextView>(R.id.alertMessage)
            message.text = "Would you like to add another waybill?"
            val image = dialogView.findViewById<AppCompatImageView>(R.id.image_widget)

            btnPositive.setOnClickListener {
                lifecycleScope.launch {
                    val waybills = waybill?.let { it1 -> listOf(it1.number) }?.let { it2 ->
                        getWaybillsFromDatabase(
                            this@WaybillDetailActivity,
                            it2
                        )
                    }

                    if (waybills != null) {
                        waybill?.let { it1 -> listOf(it1.number) }
                            ?.let { it2 -> attendWaybill(it2, waybills, emptyList()) }
                    }
                }
            }

            btnNegative.setOnClickListener {
                val dialog = ScanWaybillDialog(waybill!!, waybillList)
                dialog.setOnWaybillsScannedListener(this)
                dialog.show(supportFragmentManager, "ScanWaybillDialog")
            }

            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.show()
        }
    }


    override fun onWaybillsScanned(scannedWaybills: List<String>, parcelsScanned: List<String>) {
        Log.e("Activity", "********************** Received scanned waybills: $scannedWaybills")

        if (scannedWaybills.isNotEmpty()) {

            lifecycleScope.launch {
                val waybills = getWaybillsFromDatabase(this@WaybillDetailActivity, scannedWaybills)
                Log.e("Activity", "********************** waybills: $waybills")

                AppCache.setConsolidatedWaybills(scannedWaybills)

                attendWaybill(scannedWaybills, waybills,parcelsScanned)
            }

        } else {
            Toast.makeText(
                this@WaybillDetailActivity,
                "Scan Waybill(s) in order to continue",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}