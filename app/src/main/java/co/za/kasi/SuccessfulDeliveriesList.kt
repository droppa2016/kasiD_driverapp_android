package co.za.kasi

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import co.za.kasi.adapters.WaybillzAdapter
import co.za.kasi.databinding.ActivitySuccessfulDeliveriesListBinding
import co.za.kasi.db.sharedPreferance.PendingWaybillsStorage
import co.za.kasi.dialogs.Loader
import co.za.kasi.dialogs.ScanBarcodeDialog
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

class SuccessfulDeliveriesList : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessfulDeliveriesListBinding
    private var loader: Loader? = null
    private var snackbar: Snackbar? = null
    private var btnBackButton: AppCompatImageButton? = null
    private lateinit var title: MaterialTextView
    private lateinit var service: SuperAppHttpService
    private var waybillList: List<Waybills> = emptyList()
    private lateinit var adapter: WaybillzAdapter

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessfulDeliveriesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        getDriverWaybills()

        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.skynet_color))

        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.statusBarColor = getColor(R.color.skynet_color)

        binding.scanSearchLayout.setOnClickListener {
            val dialog = ScanBarcodeDialog()
            dialog.setOnBarcodeScannedListener { scannedValue ->
                Toast.makeText(this, "Scanned: $scannedValue", Toast.LENGTH_SHORT).show()
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

                    adapter.setItemList(waybillList)
                } else {

                    filterList(newText)
                }
                return true
            }
        })

        onBackPressedDispatcher.addCallback(this) {
            startActivity(Intent(this@SuccessfulDeliveriesList, SkynetHome::class.java))
        }
    }

    private fun setupRecyclerView(waybillList: List<Waybills>) {
        adapter = WaybillzAdapter(waybillList) { selectedWaybill ->
            val intent = Intent(this, ViewSuccessfulDelivery::class.java).apply {
                putExtra("waybill", selectedWaybill)
            }
            startActivity(intent)
        }
        binding.myRec.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = this@SuccessfulDeliveriesList.adapter
        }
    }

    private fun init() {
        Objects.requireNonNull(supportActionBar)?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custon_action_bar)
        title = supportActionBar!!.customView.findViewById(R.id.txtTitle)
        btnBackButton = supportActionBar!!.customView.findViewById(R.id.btnBack)

        btnBackButton?.setOnClickListener {
            startActivity(Intent(this@SuccessfulDeliveriesList, SkynetHome::class.java))
        }

        title.setText(getString(R.string.successful_waybills))
        service = ReusableFunctions.initiateSuperAppRetrofit(this@SuccessfulDeliveriesList)
    }

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getDriverWaybills() {

        loader = ReusableFunctions.showLoader(this@SuccessfulDeliveriesList)

        val driverWaybillCall = service.getDriverWaybillsConditional(
            LocalStorage.getSuperAppToken(applicationContext),
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            getFormattedDate(),
            "DELIVERED"
        )

        Log.e("", "TOKEN= " + LocalStorage.getSuperAppToken(applicationContext))
        Log.e("", "DRIVER ID= " + LocalStorage.getSkynetDriverAccount().driver.id)
        Log.e("", "DATE= " + getFormattedDate())

        driverWaybillCall.enqueue(object : Callback<List<Waybills>> {


            override fun onResponse(
                call: Call<List<Waybills>>,
                response: retrofit2.Response<List<Waybills>>
            ) {

                ReusableFunctions.hideLoader(loader)

                Log.e("", "=================== waybill Data ==" + response.code())
                if (response.code() == 200) {
                    val waybills = response.body()

                    if (waybills != null) {
                        setupRecyclerView(waybills.sortedBy { it.deliveryDate })
                        waybillList = waybills
                    }

                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(this@SuccessfulDeliveriesList)
                        return
                    }

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )


                    Log.e(
                        "API_ERROR",
                        "Failed to fetch waybills, Response message: ${errorResponse.message}"
                    )

                    snackbar = ReusableFunctions.snackbarInstance(
                        binding.myRec, errorResponse.message, Snackbar.LENGTH_INDEFINITE,
                        getColor(R.color.snackbar_red),
                        getColor(R.color.white),
                        Gravity.BOTTOM
                    )

                    snackbar?.show()
                }
            }


            override fun onFailure(call: Call<List<Waybills>>, t: Throwable) {
                ReusableFunctions.hideLoader(loader)
                Log.e(
                    "API_RESPONSE",
                    "---------------condit--  on failure Error: ${t.localizedMessage}"
                )
                Log.e("TAG_error", "onFailure: " + t.message, t)

                val completedWaybills = PendingWaybillsStorage.getCompletedWaybills()
                setupRecyclerView(completedWaybills.sortedBy { it.deliveryDate })

            }

        })

    }

    private fun filterList(text: String) {
        val filteredList = waybillList.filter { waybill ->
            waybill.number.contains(text, ignoreCase = true) ||
                    waybill.serviceType?.contains(text, ignoreCase = true) == true ||
                    waybill.consignee?.town?.contains(text, ignoreCase = true) == true ||
                    waybill.parcels?.any { it.number!!.contains(text, ignoreCase = true)} == true
        }

        adapter.setItemList(filteredList)
        binding.clearListBtn.visibility = View.VISIBLE
        binding.scanSearchLayout.visibility = View.INVISIBLE

        if (filteredList.isEmpty()) {
            Toast.makeText(
                this@SuccessfulDeliveriesList,
                "Waybill not found",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}




