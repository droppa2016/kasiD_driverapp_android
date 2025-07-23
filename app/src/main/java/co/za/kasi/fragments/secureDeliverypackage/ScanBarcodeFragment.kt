package co.za.kasi.fragments.secureDeliverypackage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import co.za.kasi.databinding.FragmentScanBarcodeBinding
import co.za.kasi.services.AppCache
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ScanBarcodeFragment : Fragment() {

    private var _binding: FragmentScanBarcodeBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private var isProcessingBarcode = false

    private var layoutSource: String? = null

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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
        _binding = FragmentScanBarcodeBinding.inflate(inflater, container, false)

        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())


        layoutSource = arguments?.getString("fragmentSource")

        binding.mainHeading.text = "Scan the ${layoutSource} barcode."

        checkCameraPermissionAndLaunch()

        return binding.root
    }

    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
                        processBarcodeImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, analyzer)
            } catch (exc: Exception) {
                Log.e("BarcodeScanner", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    @OptIn(ExperimentalGetImage::class)
    private fun processBarcodeImageProxy(imageProxy: ImageProxy) {
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
                            handleBarcodeScan(rawValue)
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

    private fun handleBarcodeScan(rawValue: String) {

        //binding.barcodeResult.text = rawValue

        val number = AppCache.getCurrentWaybillList()[0].deliveryConditions?.find { it.code == layoutSource }?.value

        if(number == rawValue){
            Toast.makeText(requireContext(),"$layoutSource scanned.",Toast.LENGTH_SHORT).show()
            sendResultBack(true, rawValue)
        }else{
            Toast.makeText(requireContext(),"Barcode Unknown.",Toast.LENGTH_SHORT).show()
            restartCamera()
        }


    }

    private fun sendResultBack(result: Boolean,value : String) {
        val resultBundle = Bundle().apply {
            putString("layoutSource", layoutSource)
            putBoolean("result", result)
        }

        if (result && layoutSource != null) {
            // this might be one of my best ideas
            AppCache.deliveryConditions.find { it.code == layoutSource }?.let { condition ->
                condition.value = value
            }
        }

        layoutSource?.let {
            parentFragmentManager.setFragmentResult(it, resultBundle)
        }
        parentFragmentManager.popBackStack()
    }


    private fun restartCamera() {

        isProcessingBarcode = false

        startCamera()
    }


}