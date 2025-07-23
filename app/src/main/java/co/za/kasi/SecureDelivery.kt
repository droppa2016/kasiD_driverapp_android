package co.za.kasi

import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import co.za.kasi.databinding.ActivitySecureDeliveryBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.db.sharedPreferance.PendingWaybillsStorage
import co.za.kasi.dialogs.DeliveryIssueDialog
import co.za.kasi.dialogs.IssueSelectionDialogFragment
import co.za.kasi.dialogs.Loader
import co.za.kasi.dialogs.SignOnScreenDialog
import co.za.kasi.fragments.ActionConfirmationDialogFragment
import co.za.kasi.fragments.secureDeliverypackage.GRVFragment
import co.za.kasi.fragments.secureDeliverypackage.IDConditionFragment
import co.za.kasi.fragments.secureDeliverypackage.OTPClientVerification
import co.za.kasi.fragments.secureDeliverypackage.ProofOfResidenceFragment
import co.za.kasi.fragments.secureDeliverypackage.ScanBarcodeFragment
import co.za.kasi.fragments.secureDeliverypackage.ScanWaybillFragment
import co.za.kasi.model.superApp.a.waybillData.Coordinate
import co.za.kasi.model.superApp.a.waybillData.DeliveryCondition
import co.za.kasi.model.superApp.a.waybillData.DeliveryConditionsDTO
import co.za.kasi.model.superApp.a.waybillData.WaybillRequest
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.AppCache
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.CoordinateHelper
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.IssuesLists.failDeliveriesListWithOtp
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.utils.SharedState
import co.za.kasi.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.util.Objects

class SecureDelivery : AppCompatActivity() {

    internal var binding: ActivitySecureDeliveryBinding? = null
    private var waybill: Waybills? = null
    private var btnBackButton: AppCompatImageButton? = null
    private var source: String = ""
    private lateinit var title: MaterialTextView
    private var completeSteps = mutableListOf<String?>()
    private lateinit var service: SuperAppHttpService
    private var loader: Loader? = null
    private var snackbar: Snackbar? = null

    private lateinit var location: CoordinateHelper
    private lateinit var incompleteStepsMessage: String

    private lateinit var coordinate: Coordinate

    private var completedDeliveryConditions: List<DeliveryCondition> = emptyList()
    private var incompleteDeliveryConditions: List<DeliveryCondition> = emptyList()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecureDeliveryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        init()

        binding?.root?.setBackgroundColor(ContextCompat.getColor(this, R.color.skynet_color))

        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.statusBarColor = getColor(R.color.skynet_color)

        binding?.scanWaybillLayout?.setOnClickListener {
            openFragment(ScanWaybillFragment(), "scanParcels")
        }

        binding!!.btnRetry.setOnClickListener {

            val scanList = AppCache.scanParcelList
            val hasScannedParcels = scanList?.any { it.scanned } == true

            if (!hasScannedParcels) {
                val dialog = DeliveryIssueDialog()
                dialog.setOnResult { reason, base64, name, notes ->
                    submitFailedWaybill(reason, base64, name)
                }
                dialog.show(supportFragmentManager, "DeliveryIssueDialog")
            } else {
                Toast.makeText(
                    this@SecureDelivery,
                    "Please unscan the scanned waybills. ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        binding!!.btnDone.setOnClickListener {

            if (areAllConditionsCompleted()) {

                if (AppCache.getSignatureForWaybill(AppCache.getCurrentWaybillList()[0].number) != null) {
                    submitWaybillList()
                } else {
                    Toast.makeText(
                        this@SecureDelivery,
                        "Get the receivers Signature on the sign on glass. ",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(
                    this@SecureDelivery,
                    "Complete the $incompleteStepsMessage ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding!!.btnSignOnGlass.setOnClickListener {
            if (areAllConditionsCompleted()) {
                Log.e("", "----------complete steps")
                val existing = supportFragmentManager.findFragmentByTag("sign on screen")
                if (existing == null || !existing.isAdded) {
                    val signOnScreen = SignOnScreenDialog()
                    signOnScreen.setCallback(object : SignOnScreenDialog.ReturnStatus {
                        override fun getSignatureStatus(status: Boolean) {
                            if (status) {
                                submitWaybillList()
                            }
                        }
                    })
                    signOnScreen.show(supportFragmentManager, "sign on screen")

                }
            } else {
                Toast.makeText(this@SecureDelivery, incompleteStepsMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            showExitConfirmationDialog()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                hideFragmentContainer()
            }
        }
    }

    private fun init() {
        Objects.requireNonNull(supportActionBar)?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custon_action_bar)
        title = supportActionBar!!.customView.findViewById(R.id.txtTitle)
        btnBackButton = supportActionBar!!.customView.findViewById(R.id.btnBack)
        title.text = "Delivery"

        PendingWaybillsStorage.init(this@SecureDelivery)

        btnBackButton?.setOnClickListener {
            showExitConfirmationDialog()
        }

        location = CoordinateHelper(this@SecureDelivery)

        location.getCurrentCoordinate { coordinate ->
            if (coordinate != null) {
                Log.d("Coordinate", "Lat: ${coordinate.latitude}, Lng: ${coordinate.longitude}")
                this.coordinate = Coordinate(coordinate.latitude, coordinate.longitude)
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }

        service = ReusableFunctions.initiateSuperAppRetrofit(this@SecureDelivery)

        waybill = intent.getSerializableExtra("waybill") as? Waybills
        mapDeliveryConditions(waybill!!.deliveryConditions)
    }

    private fun mapDeliveryConditions(codes: List<DeliveryConditionsDTO>?) {

        Log.e("","====================cdddd== $codes")

        if (codes.isNullOrEmpty()) {
            binding?.scanWaybillLayout?.visibility = View.VISIBLE
            return
        }

        codes.forEach { dto ->
            val deliveryCondition = DeliveryCondition(
                code = dto.code!!,
                value = dto.value!!,
                type = "",
                images = emptyList(),
                captureDateTime = ""
            )
            AppCache.addDeliveryCondition(deliveryCondition)
        }

        val preConditions =
            setOf("IDC", "POR", "ID", "INV", "BIN", "SEAL", "OTP", "CONT", "GRVD", "BAST")
        val postConditions = setOf("GRV", "CDN")
        val scanParcelsCode = "scanParcels"

        val layoutMap = mapOf(
            "IDC" to binding?.idcLayout,
            "POR" to binding?.proofOfLayout,
            "ID" to binding?.idLayout,
            "INV" to binding?.invLayout,
            "BIN" to binding?.binLayout,
            "SEAL" to binding?.sealLayout,
            "OTP" to binding?.otpLayout,
            "GRV" to binding?.grvLayout,
            "CDN" to binding?.cdnLayout,
            "CONT" to binding?.contLayout,
            "GRVD" to binding?.grvdLayout,
            "BAST" to binding?.bastLayout
        )

        val checkMap = mapOf(
            "IDC" to binding?.idcCheck,
            "POR" to binding?.proofOfCheck,
            "ID" to binding?.idCheck,
            "INV" to binding?.invCheck,
            "GRV" to binding?.grvCheck,
            "BIN" to binding?.binCheck,
            "SEAL" to binding?.sealCheck,
            "OTP" to binding?.otpCheck,
            "CONT" to binding?.contCheck,
            "CDN" to binding?.cdnCheck,
            "GRVD" to binding?.grvdCheck,
            "BAST" to binding?.bastCheck,
            "scanParcels" to binding?.scanWaybillCheck
        )

        codes.forEach { code ->
//            AppCache.addDeliveryCondition(
//                DeliveryCondition(
//                    code.code.orEmpty(),
//                    code.value.orEmpty()
//                )
//            )
            val isPre = code.code in preConditions
            if (isPre) {
                layoutMap[code.code]?.apply {
                    visibility = View.VISIBLE

                    setOnClickListener {
                        when (code.code) {
                             "POR", "INV", "CONT", "GRVD" ->
                                openFragment(ProofOfResidenceFragment(), code.code)

                            "ID","IDC" ->
                                openFragment(IDConditionFragment(), code.code)

                            "BIN", "SEAL", "BAST" ->
                                openFragment(ScanBarcodeFragment(), code.code)

                            "OTP" ->
                                openFragment(OTPClientVerification(), "OTP")
                        }
                    }
                }


            }
        }

        val allPreCompleted = preConditions.all { condition ->
            !codes.any { it.code == condition } || checkMap[condition]?.visibility == View.VISIBLE
        }

        if (allPreCompleted) {
            binding?.scanWaybillLayout?.visibility = View.VISIBLE
        }

        val scanParcelsDone = checkMap["scanParcels"]?.visibility == View.VISIBLE

        if (allPreCompleted && scanParcelsDone) {
            codes.forEach { code ->

                val isPost = code.code in postConditions
                if (isPost) {
                    layoutMap[code.code]?.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            code.code?.let { it1 ->
                                Log.e("","++++++++++++++++++++++++++++++++= $it1")

                                openFragment(GRVFragment(), it1)

                            }
                        }
                    }

                }
            }
        }
    }

    private fun showDeliveryIssueDialog(context: Context, onIssueSelected: (String) -> Unit) {

        IssueSelectionDialogFragment(
            title = "Select Delivery Issue",
            positiveButtonText = "Ok",
            negativeButtonText = "Cancel",
            onPositiveButtonClick = { selectedIssue ->
                onIssueSelected(selectedIssue)
            },
            issues = failDeliveriesListWithOtp

        ).show(supportFragmentManager, "DeliveryIssueDialog")

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openFragment(fragment: Fragment, source: String) {
        this.source = source
        binding?.secureDeliveryContainer?.visibility = View.VISIBLE
        binding?.mainLayout?.visibility = View.INVISIBLE

        supportFragmentManager.setFragmentResultListener(source, this) { requestKey, bundle ->
            val source = bundle.getString("layoutSource")
            val result = bundle.getBoolean("result")

            runOnUiThread {
                when (source) {

                    "scanParcels" -> {
                        when (result) {
                            false -> binding!!.scanWaybillCheck.visibility = View.INVISIBLE

                            else -> {
                                if (AppCache.getCurrentWaybillList() != null) {
                                    binding!!.scanWaybillCheck.visibility = View.VISIBLE

                                    if (areAllConditionsCompleted() == false) {
                                        if (incompleteStepsMessage.contains("CDN")) {
                                            binding?.cdnLayout?.visibility = View.VISIBLE
                                            binding?.cdnLayout?.setOnClickListener {
                                                openFragment(
                                                    GRVFragment(),
                                                    "CDN"
                                                )
                                            }
                                        }

                                        if (incompleteStepsMessage.contains("GRV")) {
                                            binding?.grvLayout?.visibility = View.VISIBLE
                                            binding?.grvLayout?.setOnClickListener {
                                                openFragment(
                                                    GRVFragment(),
                                                    "GRV"
                                                )
                                            }
                                        }
                                    }

                                } else {
                                    startActivity(
                                        Intent(
                                            this@SecureDelivery,
                                            SkynetHome::class.java
                                        )
                                    )
                                }

                            }
                        }
                    }

                    "ID" -> {
                        when (result) {
                            false -> showToast("Image capture failed. Try again.")
                            else -> {
                                binding!!.idCheck.visibility = View.VISIBLE
                            }
                        }
                    }

                    "IDC" -> {
                        when (result) {
                            false -> showToast("Image capture failed. Try again.")
                            else -> {
                                binding!!.idcCheck.visibility = View.VISIBLE
                            }
                        }
                    }

                    "OTP" -> {
                        when (result) {
                            false -> showToast("OTP Validation failed. Try again.")
                            else -> {
                                binding!!.otpCheck.visibility = View.VISIBLE
                            }
                        }
                    }

                    "POR" -> {
                        when (result) {
                            false -> showToast("Image capture failed. Try again.")
                            else -> {
                                binding!!.proofOfCheck.visibility = View.VISIBLE
                            }

                        }
                    }

                    "GRV" -> {
                        when (result) {
                            false -> showToast("INVALID GRV")
                            else -> {
                                binding!!.grvCheck.visibility = View.VISIBLE
                            }
                        }
                    }

                    "BIN" -> {
                        when (result) {
                            false -> showToast("INVALID BIN")
                            else -> {
                                binding!!.binCheck.visibility = View.VISIBLE
                                binding!!.scanWaybillCheck.visibility = View.VISIBLE
                                binding!!.scanWaybillLayout.visibility = View.GONE
                            }
                        }
                    }

                    "SEAL" -> {
                        when (result) {
                            false -> showToast("INVALID SEAL")
                            else -> {
                                binding!!.sealCheck.visibility = View.VISIBLE
                            }
                        }
                    }

                    "INV" -> {
                        when (result) {
                            false -> showToast("INVALID INV")
                            else -> {
                                binding!!.invCheck.visibility = View.VISIBLE

                            }

                        }
                    }

                    "CONT" -> {
                        when (result) {
                            false -> showToast("INVALID Contract")
                            else -> {
                                binding!!.contCheck.visibility = View.VISIBLE

                            }

                        }
                    }

                    "GRVD" -> {
                        when (result) {
                            false -> showToast("INVALID GRVD")
                            else -> {
                                binding!!.grvdCheck.visibility = View.VISIBLE

                            }

                        }
                    }

                    "CDN" -> {
                        when (result) {
                            false -> showToast("INVALID CDN")
                            else -> {
                                binding!!.cdnCheck.visibility = View.VISIBLE
                            }
                        }
                    }

                    "BAST" -> {
                        when (result) {
                            false -> showToast("INVALID BAST")
                            else -> {
                                binding!!.bastCheck.visibility = View.VISIBLE
                            }
                        }
                    }

                    else -> showToast("Unknown result received.")
                }
            }

            if (areAllConditionsCompleted()) {
                binding!!.glassLayout.visibility = View.VISIBLE
                val signOnScreen = SignOnScreenDialog()
                signOnScreen.setCallback(object : SignOnScreenDialog.ReturnStatus {
                    override fun getSignatureStatus(status: Boolean) {
                        if (status) {
                            submitWaybillList()
                        }
                    }
                })
                signOnScreen.show(supportFragmentManager, "sign on screen")
            }
        }

        val bundle = Bundle()
        bundle.putString("fragmentSource", source)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.secure_delivery_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun hideFragmentContainer() {
        binding?.secureDeliveryContainer?.visibility = View.GONE
        binding?.mainLayout?.visibility = View.VISIBLE
    }

    private fun areAllConditionsCompleted(): Boolean {

        val checkMap = mapOf(
            "IDC" to binding?.idcCheck,
            "POR" to binding?.proofOfCheck,
            "ID" to binding?.idCheck,
            "INV" to binding?.invCheck,
            "GRV" to binding?.grvCheck,
            "BIN" to binding?.binCheck,
            "SEAL" to binding?.sealCheck,
            "OTP" to binding?.otpCheck,
            "CONT" to binding?.contCheck,
            "CDN" to binding?.cdnCheck,
            "GRVD" to binding?.grvdCheck,
            "BAST" to binding?.bastCheck,
            "scanParcels" to binding?.scanWaybillCheck
        )

        val requiredCodes =
            waybill?.deliveryConditions?.map { it.code }?.toMutableList() ?: mutableListOf()

        val preConditions =
            setOf("IDC", "POR", "ID", "INV", "BIN", "SEAL", "OTP", "CONT", "GRVD", "BAST")

        val allPreCompleted = preConditions.all { code ->
            !requiredCodes.contains(code) || checkMap[code]?.visibility == View.VISIBLE
        }


        if (!requiredCodes.contains("scanParcels")) {
            requiredCodes.add("scanParcels")
        }

        requiredCodes.remove("CINV")
        requiredCodes.remove("CCON")
        requiredCodes.remove("CDMG")
        requiredCodes.remove("BINC")

        val incompleteSteps = mutableListOf<String>()

        requiredCodes.forEach { code ->
            val checkView = checkMap[code]
            if (checkView?.visibility != View.VISIBLE) {

                incompleteSteps.add(code!!)
            }
        }

        return if (incompleteSteps.isEmpty()) {

            completeSteps = requiredCodes

            completedDeliveryConditions = completeSteps
                .map { code ->
                    DeliveryCondition(code.toString(), "","", emptyList(),"")
                }

            Log.e("", "*************Complete *** ${completeSteps.joinToString(", ")}")

            true
        } else {
            incompleteStepsMessage = "Incomplete step(s): ${incompleteSteps.joinToString(", ")}"
            val size = incompleteSteps.size

            Log.e(
                "",
                "-----------size = $size------------------------ incomplete steps == $incompleteSteps"
            )
            if (allPreCompleted) {
                binding?.scanWaybillLayout?.visibility = View.VISIBLE
                binding?.scrollView?.scrollTo(0, 0)
                showToast(incompleteStepsMessage)
            }

            false
        }
    }

    private fun submitFailedWaybill(selectedIssue: String, base64: String,name : String) {

        loader = ReusableFunctions.showLoader(this@SecureDelivery)

        val dc = DeliveryCondition("FAILED", base64, "image", listOf(base64),LocalDateTime.now().toString())
        val body = WaybillRequest(
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            waybill?.number.toString(),
            "DELIVERY_FAILURE",
            selectedIssue,
            emptyList(),
            listOf(dc),
            coordinate, "", LocalStorage.getOrCacheAndroidId(this@SecureDelivery), name
        )

        if (!LocationService1.isNetworkAvailable(this@SecureDelivery)) {
            PendingWaybillsStorage.saveWaybill(body)
            PendingWaybillsStorage.saveFailedWaybill(AppCache.getCurrentWaybillList()[0])
            lifecycleScope.launch {
                SharedState.updateSyncedWaybills(
                    this@SecureDelivery,
                    SharedState.syncedWaybills.value + 1
                )
            }

            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(this@SecureDelivery)
                    .waybillDao()
                    .deleteWaybillByNumber(body.waybillNumber)
            }

            Toast.makeText(this, "No Internet. Delivery failure saved offline.", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(this@SecureDelivery, SkynetHome::class.java))
            return
        }

        val submitWaybillCall =
            service.submitWaybill(LocalStorage.getSuperAppToken(this@SecureDelivery), body)

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonBody = gson.toJson(body)

        Log.e("", "================= submitWaybillCall== $submitWaybillCall")

        submitWaybillCall.enqueue(object : Callback<Waybills> {
            override fun onResponse(call: Call<Waybills>, response: Response<Waybills>) {


                ReusableFunctions.hideLoader(loader)
                Log.e("", "================= waybill submit == ${response.code()}")

                if (response.code() == 200) {
                    Log.e("", "================= waybill submit == ${response.code()}")
                    AppCache.scanParcelList = null

                    startActivity(Intent(this@SecureDelivery, SkynetHome::class.java))

                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(this@SecureDelivery)
                        return
                    }

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    snackbar = ReusableFunctions.snackbarInstance(
                        binding?.binCheck, errorResponse.message, 2500,
                        getColor(R.color.snackbar_red),
                        getColor(R.color.white),
                        Gravity.TOP
                    )
                    snackbar?.show()
                }

            }

            override fun onFailure(call: Call<Waybills>, t: Throwable) {

            }
        })
    }

    private fun submitWaybillList() {
        loader = ReusableFunctions.showLoader(this@SecureDelivery)

        val waybillList = AppCache.getCurrentWaybillList()
        val scannedParcelList = AppCache.getScanParcelList()

        if (!LocationService1.isNetworkAvailable(this@SecureDelivery)) {
            waybillList.forEach { waybill ->
                val matchingParcels =
                    scannedParcelList.filter { it.parcel_number.startsWith(waybill.number) }

                val body = WaybillRequest(
                    LocalStorage.getSkynetDriverAccount().driver.identityNo,
                    waybill.number.toString(),
                    "DELIVERED",
                    "",
                    matchingParcels,
                    AppCache.getDeliveryConditions(),
                    coordinate,
                    AppCache.getSignatureForWaybill(waybill.number),
                    LocalStorage.getOrCacheAndroidId(this@SecureDelivery),AppCache.getReceiverName()
                )


                PendingWaybillsStorage.saveWaybill(body)
                PendingWaybillsStorage.saveCompletedWaybill(waybill)
                lifecycleScope.launch {
                    SharedState.updateAwaitingSync(
                        this@SecureDelivery,
                        SharedState.awaitingSync.value + 1
                    )
                }
                SharedState.updateProgress(
                    current = SharedState.syncedWaybills.value,
                    total = SharedState.awaitingSync.value
                )
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getDatabase(this@SecureDelivery)
                        .waybillDao()
                        .deleteWaybillByNumber(waybill.number)
                }
            }

            Toast.makeText(this, "No Internet. Deliveries saved offline.", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(this@SecureDelivery, SkynetHome::class.java))
            return
        }

        submitWaybillSequentially(waybillList)

    }

    private fun submitWaybillSequentially(waybills: List<Waybills>, index: Int = 0) {

        if (index >= waybills.size) {
            ReusableFunctions.hideLoader(loader)
            Toast.makeText(this, "Delivery(s) completed", Toast.LENGTH_SHORT).show()
            AppCache.setCurrentWaybillList(null)
            startActivity(Intent(this@SecureDelivery, SkynetHome::class.java))
            AppCache.scanParcelList = null
            return
        }

        val waybill = waybills[index]
        val scannedParcels = AppCache.getScanParcelList()
            .filter { it.parcel_number.startsWith(waybill.number) }

        val body = WaybillRequest(
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            waybill.number.toString(),
            "DELIVERED",
            "",
            scannedParcels,
            AppCache.getDeliveryConditions(),
            coordinate,
            AppCache.getSignatureForWaybill(waybill.number),
            LocalStorage.getOrCacheAndroidId(this@SecureDelivery),AppCache.getReceiverName()
        )

        val call = service.submitWaybill(LocalStorage.getSuperAppToken(this@SecureDelivery), body)

        call.enqueue(object : Callback<Waybills> {

            override fun onResponse(call: Call<Waybills>, response: Response<Waybills>) {
                if (response.code() == 200) {
                    submitWaybillSequentially(waybills, index + 1)

                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(this@SecureDelivery)
                        return
                    }

                    ReusableFunctions.hideLoader(loader)

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    Log.e("","-----error response waybill complete --------------------------------- ${errorResponse.message}")



                    
                    snackbar = ReusableFunctions.snackbarInstance(
                        binding?.btnDone,
                        "Failed to deliver ${waybill.number}: ${errorResponse.message}",
                        2500,
                        getColor(R.color.snackbar_red),
                        getColor(R.color.white),
                        Gravity.TOP
                    )

                    snackbar?.show()
                }
            }

            override fun onFailure(call: Call<Waybills>, t: Throwable) {
                ReusableFunctions.hideLoader(loader)
                Toast.makeText(this@SecureDelivery, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun showExitConfirmationDialog() {
        val scanList = AppCache.scanParcelList
        val hasScannedParcels = scanList?.any { it.scanned } == true

        if (hasScannedParcels) {

            ActionConfirmationDialogFragment(
                title = "Exit Blocked",
                message = "You cannot leave this screen because some parcel(s) have already been scanned.\n\nPlease unscan the parcel(s) first.",
                hasASingleButton = true,
                buttonConfirmText = "OK"
            ).show(supportFragmentManager, "confirm_action_dialog")
        } else {
            ActionConfirmationDialogFragment(
                title = "Exit Confirmation",
                message = "Are you sure you want to leave this screen?\n" +
                        "\n" +
                        "PS- All progress made on this waybill will be lost.",
                buttonPositiveText = "Yes",
                buttonNegativeText = "No",
                onPositive = {
                    startActivity(Intent(this@SecureDelivery, WaybillDetailActivity::class.java))
                    AppCache.scanParcelList = null
                }
            ).show(supportFragmentManager, "confirm_action_dialog")
        }
    }
}