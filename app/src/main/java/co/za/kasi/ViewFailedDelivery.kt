package co.za.kasi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import co.za.kasi.databinding.ActivityViewFailedDeliveryBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.dialogs.Loader
import co.za.kasi.dialogs.OnWaybillsScannedListener
import co.za.kasi.dialogs.ScanWaybillDialog
import co.za.kasi.model.superApp.a.waybillData.Coordinate
import co.za.kasi.model.superApp.a.waybillData.DriverEventDTO
import co.za.kasi.model.superApp.a.waybillData.WaybillAttending
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.AppCache
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.CoordinateHelper
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Objects

class ViewFailedDelivery : AppCompatActivity(), OnWaybillsScannedListener {

    private lateinit var binding : ActivityViewFailedDeliveryBinding
    private lateinit var service: SuperAppHttpService
    private var loader: Loader? = null
    private var snackbar: Snackbar? = null
    var waybill : Waybills? = null
    private var btnBackButton: AppCompatImageButton? = null
    private lateinit var title: MaterialTextView
    private var waybillList: List<Waybills> = emptyList()
    private var waybillCounters: MutableList<Waybills> = mutableListOf()



    private lateinit var location : CoordinateHelper

    private lateinit var coordinate : Coordinate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFailedDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        waybill = intent.getSerializableExtra("waybill") as? Waybills

        binding.tvFailureReason.text = waybill?.statusDescription
        binding.tvPickUpAddress.text = "${waybill?.sender?.addressLine1} ${waybill?.sender?.addressLine2}"
        binding.tvDroppOffAddress.text = "${waybill?.consignee?.addressLine1}, ${waybill?.consignee?.addressLine2}, ${waybill?.consignee?.addressLine2}"
       // binding.tvClientNames.text = waybill?.consignee?.name
        binding.tvClientNames.text = "ABC Traders"
        binding.tvWaybillNumber.text = waybill?.number
        binding.tvSpecialInstractions.text = waybill?.specialInstructions
        binding.tvPickUpDate.text = waybill?.deliveryDate?.substring(0,10)
        binding.tvTime.text = waybill?.deliveryDate?.substring(11,16)
        binding.tvCompanyName.text = waybill?.consignee?.name
        binding.tvServiceType.text = waybill?.serviceType
        binding.tvParcels.text =  waybill?.parcels?.size.toString()
      //  binding.tvPhoneNo.text = waybill?.consignee?.telephoneNumber
        binding.tvPhoneNo.text = "12345678"
        binding.tvContact.text = "Contact"

        Log.d("WAYBILL_OBJ", "onCreate: " + waybill?.consignee.toString())

        val name = waybill?.consignee?.name?.trim().orEmpty()
        val contact = waybill?.consignee?.contact?.trim().orEmpty()

        if (contact.isEmpty() || contact.equals(name, ignoreCase = true)) {
            binding.lytContact.visibility = View.GONE
        } else {
            binding.lytContact.visibility = View.VISIBLE
        }


        binding?.btnGoToPickUp?.setOnClickListener{

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

                    showBulkDeliveryDialog(this@ViewFailedDelivery, waybill?.consolidationId!!)

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
                                this@ViewFailedDelivery,
                                it2
                            )
                        }



                        if (waybills != null) {
                            waybill?.let { it1 -> listOf(it1.number) }
                                ?.let { it2 -> attendWaybill(it2, waybills) }
                        }
                    }
                }

            }

        }


    }

    private fun init(){
        Objects.requireNonNull(supportActionBar)?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custon_action_bar)
        title = supportActionBar!!.customView.findViewById(R.id.txtTitle)
        btnBackButton = supportActionBar!!.customView.findViewById(R.id.btnBack)
        title.setText("Delivery Waybills")
        service = ReusableFunctions.initiateSuperAppRetrofit(this@ViewFailedDelivery)

        location = CoordinateHelper(this@ViewFailedDelivery)

        location.getCurrentCoordinate { coordinate ->
            if (coordinate != null) {
                Log.d("Coordinate", "Lat: ${coordinate.latitude}, Lng: ${coordinate.longitude}")
                this.coordinate = Coordinate(coordinate.latitude, coordinate.longitude)
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun attendWaybill(waybillNumber : List<String>, way : List<Waybills>) {
        loader = ReusableFunctions.showLoader(this@ViewFailedDelivery)

        var attendWaybill = service.attendWaybill(LocalStorage.getSuperAppToken(applicationContext),
            WaybillAttending(waybillNumber,
                LocalStorage.getSkynetDriverAccount().driver.id,
                LocalStorage.getOrCacheAndroidId(this@ViewFailedDelivery),
                coordinate))


        attendWaybill.enqueue(object : Callback<DriverEventDTO> {
            override fun onResponse(
                call: Call<DriverEventDTO>,
                response: Response<DriverEventDTO>
            ) {

                ReusableFunctions.hideLoader(loader)


                Log.e("", "===================attend waybill ==" + response.code())
                if(response.isSuccessful){


                    val intent = Intent(this@ViewFailedDelivery, SecureDelivery::class.java).apply {
                        putExtra("waybill", waybill)
                    }

                    AppCache.setCurrentWaybillList(null)
                    AppCache.setCurrentWaybillList(listOf(waybill))


                    startActivity(intent)



                }else{

                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(this@ViewFailedDelivery)
                        return
                    }

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string())

                    snackbar = ReusableFunctions.snackbarInstance(
                        binding.btnGoToPickUp, errorResponse.message, Snackbar.LENGTH_SHORT,
                        getColor(R.color.snackbar_red),
                        getColor(R.color.white),
                        Gravity.TOP)

                    snackbar?.show()
                }
            }

            override fun onFailure(call: Call<DriverEventDTO>, t: Throwable) {
                ReusableFunctions.hideLoader(loader)
                val intent = Intent(this@ViewFailedDelivery, SecureDelivery::class.java).apply {
                    putExtra("waybill", waybill)
                }

                AppCache.setCurrentWaybillList(way)

                startActivity(intent)
            }

        })
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
        val dialogView = LayoutInflater.from(context).inflate(R.layout.counter_bulk_delivery_widget, null)


        val mainTextView = dialogView.findViewById<TextView>(R.id.main_text)
        mainTextView.text = message

        val button = dialogView.findViewById<Button>(R.id.btnBulkDelivery)
        button.setOnClickListener {

            // attendWaybill()

            val intent =
                Intent(this@ViewFailedDelivery, SecureDelivery::class.java).apply {
                    putExtra("waybill", waybill)
                }
            startActivity(intent)
        }


        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        alertDialog.show()
    }

    suspend fun getWaybillsFromDatabase(context: Context, numbers: List<String>): List<Waybills> {
        val db = AppDatabase.getDatabase(context)
        return numbers.mapNotNull { number ->
            db.waybillDao().getWaybillByNumber(number)
        }
    }



    private fun showResultDialog(barcodeValue: String) {

        runOnUiThread {
            val dialogView =
                LayoutInflater.from(this@ViewFailedDelivery)
                    .inflate(R.layout.start_deliveries_widget, null)

            val alertDialog = AlertDialog.Builder(this@ViewFailedDelivery)
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
                            this@ViewFailedDelivery,
                            it2
                        )
                    }



                    if (waybills != null) {
                        waybill?.let { it1 -> listOf(it1.number) }
                            ?.let { it2 -> attendWaybill(it2, waybills) }
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

    override fun onWaybillsScanned(scannedWaybills: List<String>,parcelsScanned: List<String>) {

        if (scannedWaybills.isNotEmpty()) {
            val list: MutableList<Waybills> = mutableListOf()
            list.add(waybill!!)
            attendWaybill(scannedWaybills, list)
        } else {
            Toast.makeText(
                this@ViewFailedDelivery,
                "Scan Waybill(s) in order to continue",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}