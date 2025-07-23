package co.za.kasi.fragments.secureDeliverypackage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.za.kasi.DeliveriesList
import co.za.kasi.R
import co.za.kasi.SkynetHome
import co.za.kasi.adapters.ParcelAdapter
import co.za.kasi.databinding.FragmentScanWaybillBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.db.sharedPreferance.PendingWaybillsStorage
import co.za.kasi.dialogs.IssueSelectionDialogFragment
import co.za.kasi.dialogs.Loader
import co.za.kasi.dialogs.ScanBarcodeDialog
import co.za.kasi.fragments.ActionConfirmationDialogFragment
import co.za.kasi.model.superApp.a.waybillData.Coordinate
import co.za.kasi.model.superApp.a.waybillData.ScanParcel
import co.za.kasi.model.superApp.a.waybillData.WaybillListItem
import co.za.kasi.model.superApp.a.waybillData.WaybillRequest
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.AppCache
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.CoordinateHelper
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.IssuesLists.failParcelIssues
import co.za.kasi.utils.ReusableFunctions
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SuppressLint("NotifyDataSetChanged")
class ScanWaybillFragment : Fragment() {

    private lateinit var binding: FragmentScanWaybillBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private var isProcessingBarcode = false

    private var layoutSource: String? = null

    private lateinit var service: SuperAppHttpService

    private var loader: Loader? = null
    private lateinit var location: CoordinateHelper
    private lateinit var coordinate: Coordinate

    private lateinit var scanParcelList: MutableList<WaybillListItem>

    private lateinit var waybillList: MutableList<Waybills>

    private var scannedParcels = mutableListOf<String>()
    private lateinit var parcelAdapter: ParcelAdapter

    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity

    private var snackbar: Snackbar? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                // Handle permission denial
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanWaybillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = context ?: return
        safeActivity = activity ?: return
        init()
        layoutSource = arguments?.getString("fragmentSource")

        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        waybillList = AppCache.getCurrentWaybillList().toMutableList()
        buildScanParcelListIfNeeded()

        markParcelsAsScanned(AppCache.getParcelsScanned())

        updateParcelCount()
        scanParcelList = buildWaybillListItems(waybillList).toMutableList()
        parcelAdapter = ParcelAdapter(scanParcelList) { parcelNumber ->
            unscanParcel(parcelNumber)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = parcelAdapter

        binding.btnDone.setOnClickListener {
            updateParcelCount()
            val totalParcels = waybillList.sumOf { it.parcels?.size ?: 0 }
            val scannedCount = AppCache.scanParcelList.count { it.scanned }

            if (scannedCount == totalParcels) {
                sendResultBack(true)
            } else {
                Toast.makeText(requireContext(), "Scan all Parcels", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnManualEntry.setOnClickListener {
            showManualWaybillDialog()
        }

        binding.scanAllParcels.setOnClickListener {
            AppCache.scanParcelList.forEach { it.scanned = true }
            scanParcelList.forEach { item ->
                if (item is WaybillListItem.ParcelItem) {
                    item.scanParcel.scanned = true
                }
            }

            parcelAdapter.notifyDataSetChanged()
            updateParcelCount()
        }

        checkCameraPermissionAndLaunch()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            updateParcelCount()
            val totalParcels = waybillList.sumOf { it.parcels?.size ?: 0 }
            val scannedCount = AppCache.scanParcelList.count { it.scanned }

            if (scannedCount == totalParcels) {
                sendResultBack(true)
            } else {
                sendResultBack(false)
            }
            parentFragmentManager.popBackStack()
        }
    }

    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startScanningParcels()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun init() {

        location = CoordinateHelper(requireContext())

        location.getCurrentCoordinate { coordinate ->
            if (coordinate != null) {
                Log.d("Coordinate", "Lat: ${coordinate.latitude}, Lng: ${coordinate.longitude}")
                this.coordinate = Coordinate(coordinate.latitude, coordinate.longitude)
            } else {
                Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        service = ReusableFunctions.initiateSuperAppRetrofit(requireContext())
    }

    private fun startCamera() {

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processParcelImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    analyzer
                )
            } catch (exc: Exception) {
                Log.e("BarcodeScanner", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun restartCamera() {

        startScanningParcels()
    }

    private fun startScanningParcels() {
        isProcessingBarcode = false

        startCamera()
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processParcelImageProxy(imageProxy: ImageProxy) {
        if (isProcessingBarcode) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (!rawValue.isNullOrEmpty()) {
                            isProcessingBarcode = true
                            imageProxy.close()
                            handleParcelScan(rawValue)
                            return@addOnSuccessListener
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("BarcodeScanner", "Parcel barcode scanning failed", it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun handleParcelScan(parcelBarcode: String) {
        val allParcels = waybillList.flatMap { it.parcels ?: emptyList() }

        if (scannedParcels.size != allParcels.size) {
            val parcelMatch = allParcels.find { it.number.equals(parcelBarcode, ignoreCase = true) }

            if (parcelMatch != null && scannedParcels.none {
                    it.equals(
                        parcelBarcode,
                        ignoreCase = true
                    )
                }) {

                scannedParcels.add(parcelBarcode)

                val index = scanParcelList.indexOfFirst {
                    it is WaybillListItem.ParcelItem &&
                            it.scanParcel.parcel_number.equals(parcelBarcode, ignoreCase = true)
                }

                if (index != -1) {
                    (scanParcelList[index] as WaybillListItem.ParcelItem).scanParcel.scanned = true
                    parcelAdapter.notifyItemChanged(index)
                    AppCache.scanParcelList.find {
                        it.parcel_number.equals(
                            parcelBarcode,
                            ignoreCase = true
                        )
                    }?.scanned =
                        true
                }

                updateParcelCount()

                binding.viewFinder.postDelayed({
                    isProcessingBarcode = false
                }, 100)

            } else if (scannedParcels.any { it.equals(parcelBarcode, ignoreCase = true) }) {
                Toast.makeText(requireContext(), "Parcel successfully scanned.", Toast.LENGTH_SHORT)
                    .show()
                isProcessingBarcode = false
            } else {
                Toast.makeText(requireContext(), "Parcel not found.", Toast.LENGTH_SHORT).show()
                isProcessingBarcode = false
            }
        } else {
            Toast.makeText(requireContext(), "Parcels complete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendResultBack(result: Boolean) {
        val resultBundle = Bundle().apply {
            putString("layoutSource", layoutSource)
            putBoolean("result", result)
        }
        layoutSource?.let {
            parentFragmentManager.setFragmentResult(it, resultBundle)
        }
        parentFragmentManager.popBackStack()
    }

    private fun showManualWaybillDialog() {
        val input = android.widget.EditText(requireContext())
        input.hint = "Enter Parcel number"

        binding.recyclerView.visibility = View.GONE

        AlertDialog.Builder(requireContext())
            .setTitle("Manual Parcel Entry")
            .setView(input)
            .setCancelable(true)
            .setPositiveButton("Submit") { dialog, _ ->
                val enteredWaybill = input.text.toString().trim()
                if (enteredWaybill.isNotEmpty()) {
                    handleParcelScan(enteredWaybill)
                } else {
                    Toast.makeText(requireContext(), "Waybill cannot be empty", Toast.LENGTH_SHORT)
                        .show()
                }
                binding.recyclerView.visibility = View.VISIBLE

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                binding.recyclerView.visibility = View.VISIBLE

                dialog.dismiss()
            }
            .setOnDismissListener {
                binding.recyclerView.visibility = View.VISIBLE
            }
            .show()
    }


    private fun unscanParcel(parcelBarcode: String) {

        if (scannedParcels.contains(parcelBarcode) || AppCache.scanParcelList.find { it.parcel_number.equals(parcelBarcode, ignoreCase = true)  }!!.scanned) {
            updateParcelCount()
            showResultDialog(parcelBarcode)
            restartCamera()
        }else{

            val waybill = findWaybillByParcelNumber(waybillList,parcelBarcode)

            showDeliveryIssueDialog { reason ->
                if (waybill != null) {
                    submitFailedWaybill(reason, waybill)
                    removeWaybillAndParcels(waybill.number, waybillList)
                }
            }

        }

    }

    private fun showResultDialog(barcodeValue: String) {
        ActionConfirmationDialogFragment(
            isError = true,
            isCancelable = false,
            buttonConfirmText = getString(R.string.reset_password),
            title = getString(R.string.scan_back_parcel),
            message = getString(
                R.string.would_you_like_to_scan_back_the_parcel
            ),
            buttonNegativeText = getString(R.string.no),
            buttonPositiveText = getString(R.string.yes),
            onPositive = {
                val waybill = findWaybillByParcelNumber(waybillList, barcodeValue)

                isProcessingBarcode = false

                if (scannedParcels.contains(barcodeValue) || AppCache.scanParcelList.find { it.parcel_number.equals(barcodeValue, ignoreCase = true)  }!!.scanned) {
                    val dialog = ScanBarcodeDialog()
                    dialog.setOnBarcodeScannedListener { scannedValue ->
                        if (scannedValue.equals(barcodeValue, ignoreCase = true)) {
                            scannedParcels.remove(barcodeValue)
                            parcelAdapter.unscanParcel(barcodeValue)

                            val scanParcel =
                                AppCache.scanParcelList.find { it.parcel_number.equals(barcodeValue, ignoreCase = true)}
                            scanParcel?.scanned = false

                            updateParcelCount()
                            showDeliveryIssueDialog { reason ->
                                if (waybill != null) {
                                    submitFailedWaybill(reason, waybill)
                                    removeWaybillAndParcels(waybill.number, waybillList)
                                }
                            }
                            restartCamera()
                        }

                    }
                    dialog.show(requireActivity().supportFragmentManager, "BarcodeScanDialog")

                }


            },
            onNegative = {
                isProcessingBarcode = false
            }
        ).show(parentFragmentManager, "fail_waybill_dialog")
    }

    fun findWaybillByParcelNumber(
        waybills: List<Waybills>,
        parcelNumber: String
    ): Waybills? {
        return waybills.firstOrNull { waybill ->
            waybill.parcels?.any { it.number.equals(parcelNumber, ignoreCase = true)} == true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    fun removeWaybillAndParcels(waybillNumber: String, fullWaybillList: List<Waybills>) {

        val parcelNumbers = fullWaybillList
            .find { it.number.equals(waybillNumber, ignoreCase = true)  }
            ?.parcels
            ?.mapNotNull { it.number }
            ?: emptyList()

        scanParcelList.removeAll { item ->
            (item is WaybillListItem.Header && item.waybillNumber.equals(waybillNumber, ignoreCase = true)) ||
                    (item is WaybillListItem.ParcelItem && item.scanParcel.parcel_number in parcelNumbers)
        }
        AppCache.scanParcelList.removeAll { scanParcel ->
            scanParcel.parcel_number in parcelNumbers
        }



        waybillList.removeIf { it.number.equals(waybillNumber, ignoreCase = true)}
        AppCache.getCurrentWaybillList().removeIf { it.number.equals(waybillNumber, ignoreCase = true)}

        parcelAdapter.notifyDataSetChanged()

        updateParcelCount()
    }

    private fun submitFailedWaybill(selectedIssue: String, waybill: Waybills) {

        loader = ReusableFunctions.showLoader(requireContext())

        val body = WaybillRequest(
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            waybill.number.toString(),
            getString(R.string.delivery_failure),
            selectedIssue,
            emptyList(),
            emptyList(),
            coordinate, "", LocalStorage.getOrCacheAndroidId(requireContext()), "receiver"
        )

        if (!LocationService1.isNetworkAvailable(requireContext())) {
            PendingWaybillsStorage.saveWaybill(body)
            PendingWaybillsStorage.saveFailedWaybill(AppCache.getCurrentWaybill())

            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(requireContext())
                    .waybillDao()
                    .deleteWaybillByNumber(body.waybillNumber)
            }

            Toast.makeText(
                requireContext(),
                getString(R.string.no_internet_delivery_failure_saved_offline),
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(requireContext(), DeliveriesList::class.java))
            return
        }

        val submitWaybillCall =
            service.submitWaybill(LocalStorage.getSuperAppToken(requireContext()), body)


        submitWaybillCall.enqueue(object : Callback<Waybills> {
            override fun onResponse(call: Call<Waybills>, response: Response<Waybills>) {

                ReusableFunctions.hideLoader(loader)

                if (response.code() == 200) {

                    if (AppCache.getCurrentWaybillList().isEmpty()) {
                        startActivity(Intent(requireContext(), SkynetHome::class.java))
                        AppCache.setCurrentWaybillList(null)
                        AppCache.setCurrentWaybill(null)
                    }

                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    snackbar = ReusableFunctions.snackbarInstance(
                        binding.btnDone, errorResponse.message, Snackbar.LENGTH_INDEFINITE,
                        ContextCompat.getColor(requireContext(), R.color.snackbar_red),
                        ContextCompat.getColor(requireContext(), R.color.white),
                        Gravity.TOP
                    )
                    snackbar?.show()
                }
            }

            override fun onFailure(call: Call<Waybills>, t: Throwable) {

            }
        })
    }

    private fun showDeliveryIssueDialog(onIssueSelected: (String) -> Unit) {
        IssueSelectionDialogFragment(
            title = getString(R.string.select_parcel_issue),
            issues = failParcelIssues,
            positiveButtonText = getString(R.string.ok),
            negativeButtonText = getString(R.string.cancel),
            onPositiveButtonClick = { selectedIssue ->
                onIssueSelected(selectedIssue)
            }
        ).show(parentFragmentManager, getString(R.string.delivery_issue_dialog))
    }

    private fun buildScanParcelListIfNeeded() {
        if (AppCache.scanParcelList.isNullOrEmpty()) {
            val scanList = waybillList.flatMap { waybill ->
                waybill.parcels?.map { parcel ->
                    val scanned = scannedParcels.contains(parcel.number)
                    ScanParcel(parcel_number = parcel.number.orEmpty(), scanned = scanned)
                } ?: emptyList()
            }

            AppCache.scanParcelList = scanList.toMutableList()
        }
    }

    fun buildWaybillListItems(waybills: List<Waybills>): List<WaybillListItem> {
        val items = mutableListOf<WaybillListItem>()
        val scanParcelMap = AppCache.scanParcelList.associateBy { it.parcel_number }

        waybills.forEach { waybill ->
            items.add(WaybillListItem.Header(waybill.number))
            waybill.parcels?.forEach { parcel ->
                val scanned = scanParcelMap[parcel.number]?.scanned ?: false
                items.add(
                    WaybillListItem.ParcelItem(
                        waybillNumber = waybill.number,
                        scanParcel = ScanParcel(parcel.number.orEmpty(), scanned)
                    )
                )
            }
        }


        return items
    }

    private fun markParcelsAsScanned(scannedParcelNumbers: List<String>) {
        AppCache.scanParcelList.forEach { scanParcel ->
            if (scannedParcelNumbers.contains(scanParcel.parcel_number)) {
                scanParcel.scanned = true
            }
        }


    }

    private fun updateParcelCount() {
        val total = waybillList.sumOf { it.parcels?.size ?: 0 }
        val scanned = AppCache.scanParcelList.count { it.scanned }

        binding.numTotalParcels.text =
            getString(R.string.totaled, scanned.toString(), total.toString())


        if (total >= 10) {
            binding.scanAllParcels.visibility = View.VISIBLE
        }

        binding.numTotalParcels.text =
            getString(R.string.totaled, scanned.toString(), total.toString())
    }
}