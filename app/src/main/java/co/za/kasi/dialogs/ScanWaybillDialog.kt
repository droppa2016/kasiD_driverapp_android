package co.za.kasi.dialogs

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.za.kasi.R
import co.za.kasi.adapters.ScannedWaybillsAdapter
import co.za.kasi.databinding.ScanWaybillDialogBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.model.superApp.a.waybillData.Waybills
import com.google.android.material.textview.MaterialTextView
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService

class ScanWaybillDialog(
    private val waybill: Waybills,
    private val waybillList: List<Waybills>
) : DialogFragment() {

    private var _binding: ScanWaybillDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private var isProcessingBarcode = false

    private var isFirstScanValidated = false

    private val scannedWaybills = mutableListOf<String>()
    private val parcelScan = mutableListOf<String>()
    private lateinit var adapter: ScannedWaybillsAdapter

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                // Handle permission denial

            }
        }

    private var listener: OnWaybillsScannedListener? = null

    fun setOnWaybillsScannedListener(listener: OnWaybillsScannedListener) {
        this.listener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScanWaybillDialogBinding.inflate(inflater, container, false)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraExecutor = java.util.concurrent.Executors.newSingleThreadExecutor()


        initRecyclerView()
        checkCameraPermissionAndLaunch()

        binding.btnDone.setOnClickListener {
            listener?.onWaybillsScanned(scannedWaybills,parcelScan)
            Log.e("","=====List of parcel numbers == $parcelScan")
            dismiss()
        }

        binding.btnManualAdd.setOnClickListener {
            openManualAddDialog()
        }

        return binding.root
    }

    private fun initRecyclerView() {
        adapter = ScannedWaybillsAdapter(scannedWaybills)
        scannedWaybills.add(waybill.number)
        adapter.notifyDataSetChanged()
        binding.waybillsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.waybillsRecyclerView.adapter = adapter
    }

    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
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
                        processImageProxy(imageProxy)
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

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        if (isProcessingBarcode) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }


        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue ?: continue
                    isProcessingBarcode = true
                    imageProxy.close()

                    lifecycleScope.launch {
                        val matchedWaybill = when {
                            rawValue == waybill.number -> waybill
                            else -> findWaybillByParcelNumber(waybillList, rawValue)
                                ?: getWaybillFromDatabase(requireContext(), rawValue)
                        }

                        if (matchedWaybill == null) {
                            showToast("Waybill not found")
                            restartCamera()
                            return@launch
                        }

                        val hasConditions = matchedWaybill.deliveryConditions?.isNotEmpty() == true
                        val isCOU = matchedWaybill.serviceType == "COU"

                        if (hasConditions || isCOU) {
                            showToast("Waybill with conditions or COU may not be consolidated.")
                            restartCamera()
                            return@launch
                        }


                        if (!scannedWaybills.contains(matchedWaybill.number)) {
                            scannedWaybills.add(matchedWaybill.number)
                            if (!isFirstScanValidated) {
                                isFirstScanValidated = true
                                binding.numTotalWaybills.visibility = View.VISIBLE
                            }
                            adapter.notifyItemInserted(scannedWaybills.size - 1)
                            parcelScan.add(rawValue)
                            restartCamera()
                            Log.d("Waybill", "Waybill added: ${matchedWaybill.number}")
                        } else {
                            showToast("Waybill already added.")
                            restartCamera()
                        }
                        isProcessingBarcode = false
                    }

                    return@addOnSuccessListener
                }
            }
            .addOnFailureListener {
                Log.e("BarcodeScanner", "Barcode scanning failed", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        isProcessingBarcode = false
    }

    private fun showResultDialog(barcodeValue: String) {

        requireActivity().runOnUiThread {
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.start_deliveries_widget, null)

            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val btnPositive = dialogView.findViewById<AppCompatButton>(R.id.btnStartTrip)
            btnPositive.text = "No"
            val btnNegative = dialogView.findViewById<AppCompatButton>(R.id.btnDismiss)
            btnNegative.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.button_background_red_borders))
            btnNegative.text = "Yes"
            btnNegative.setTextColor(requireActivity().getColor(R.color.skynet_color))
            val heading = dialogView.findViewById<MaterialTextView>(R.id.alertTitle)
            heading.text = barcodeValue
            val message = dialogView.findViewById<MaterialTextView>(R.id.alertMessage)
            message.text = "Would you like to add another waybill?"
            val image = dialogView.findViewById<AppCompatImageView>(R.id.image_widget)

            btnPositive.setOnClickListener {
                listener?.onWaybillsScanned(scannedWaybills,parcelScan)
                dismiss()
            }

            btnNegative.setOnClickListener {
                if (waybill.deliveryConditions?.size == 0) {
                    isProcessingBarcode = false
                    startCamera()
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You cannot add another waybill as this waybill contains special delivery conditions.",
                        Toast.LENGTH_SHORT
                    ).show()
                    restartCamera()
                }
            }

            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.show()
        }
    }

    private fun openManualAddDialog() {
        val input = android.widget.EditText(requireContext())
        input.hint = "Enter Waybill Number"


        fun Int.toPx(): Int =
            (this * resources.displayMetrics.density).toInt()

        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(30.toPx(), 0, 30.toPx(), 0)
        }
        input.layoutParams = layoutParams

        AlertDialog.Builder(requireContext())
            .setTitle("Manual Waybill Entry")
            .setView(input)
            .setPositiveButton("Submit") { dialog, _ ->
                val enteredWaybillNo = input.text.toString().trim()

                if (enteredWaybillNo.isNotEmpty()) {
                    processManualWaybill(enteredWaybillNo)
                } else {
                    Toast.makeText(requireContext(), "Waybill cannot be empty", Toast.LENGTH_SHORT)
                        .show()
                }

                dialog.dismiss()

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun processManualWaybill(waybillInput: String) {
        val allWaybills = waybillList

        if (!isFirstScanValidated) {
            if (waybillInput == waybill.number) {
                isFirstScanValidated = true
                scannedWaybills.add(waybillInput)
                adapter.notifyItemInserted(scannedWaybills.size - 1)
               // showResultDialog(waybillInput)
            } else {

                val parcelWaybill = findWaybillByParcelNumber(waybillList, waybillInput)

                if (parcelWaybill != null) {

                    if (parcelWaybill.number == waybill.number) {
                        isFirstScanValidated = true
                        scannedWaybills.add(parcelWaybill.number)
                        adapter.notifyItemInserted(scannedWaybills.size - 1)
                      //  showResultDialog(parcelWaybill.number)
                        restartCamera()

                    } else {
                        Toast.makeText(requireContext(), "Incorrect Waybill", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(requireContext(), "Incorrect WaybillL", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            return
        }

        if (scannedWaybills.contains(waybillInput)) {
            Toast.makeText(requireContext(), "Waybill already added.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val matchedWaybill =
                getWaybillFromDatabase(requireContext(), waybillInput)
                    ?: allWaybills.find { it.number == waybillInput }

            if (matchedWaybill == null) {
                Toast.makeText(requireContext(), "Waybill not found", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (matchedWaybill.deliveryConditions?.isNotEmpty() == true ||
                matchedWaybill.serviceType == "COU"
            ) {
                Toast.makeText(
                    requireContext(),
                    "This waybill has special delivery conditions and cannot be added.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                scannedWaybills.add(matchedWaybill.number)
                adapter.notifyItemInserted(scannedWaybills.size - 1)
                restartCamera()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val layoutParams = dialog.window?.attributes
            layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.attributes = layoutParams
        }
    }

    private fun restartCamera() {
        isProcessingBarcode = false
        startCamera()
    }

    private suspend fun getWaybillFromDatabase(context: Context, number: String): Waybills? {
        val db = AppDatabase.getDatabase(context)
        return db.waybillDao().getWaybillByNumber(number)
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

interface OnWaybillsScannedListener {
    fun onWaybillsScanned(scannedWaybills: List<String>, parcelsScanned : List<String>)

}