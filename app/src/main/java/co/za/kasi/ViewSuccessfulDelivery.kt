package co.za.kasi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import co.za.kasi.databinding.ActivityViewSuccessfulDeliveryBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import java.util.Objects

class ViewSuccessfulDelivery : AppCompatActivity() {

    private lateinit var binding: ActivityViewSuccessfulDeliveryBinding

    private lateinit var service: SuperAppHttpService
    private var loader: Loader? = null
    private var snackbar: Snackbar? = null
    var waybill : Waybills? = null

    private var btnBackButton: AppCompatImageButton? = null
    private lateinit var title: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewSuccessfulDeliveryBinding.inflate(layoutInflater)
        init()

        init()

        waybill = intent.getSerializableExtra("waybill") as? Waybills

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

        onBackPressedDispatcher.addCallback(this) {
            startActivity(Intent(this@ViewSuccessfulDelivery, SuccessfulDeliveriesList::class.java))
        }


        binding.btnDone.setOnClickListener{
            startActivity(Intent(this@ViewSuccessfulDelivery, SuccessfulDeliveriesList::class.java))
        }

        setContentView(binding.root)
    }


    private fun init(){
        Objects.requireNonNull(supportActionBar)?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custon_action_bar)
        title = supportActionBar!!.customView.findViewById(R.id.txtTitle)
        btnBackButton = supportActionBar!!.customView.findViewById(R.id.btnBack)
        title.setText("Successful Deliveries ")

        btnBackButton?.setOnClickListener{
            startActivity(Intent(this@ViewSuccessfulDelivery, SuccessfulDeliveriesList::class.java))
        }
    }
}