package co.za.kasi.fragments.secureDeliverypackage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.za.kasi.databinding.FragmentIDConditionBinding
import co.za.kasi.services.AppCache
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors


class IDConditionFragment : Fragment() {
    private lateinit var binding: FragmentIDConditionBinding

    private var layoutSource: String? = null

    private var cameraExecutor = Executors.newSingleThreadExecutor()
    private var imageCapture: ImageCapture? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIDConditionBinding.inflate(inflater, container, false)

        layoutSource = arguments?.getString("fragmentSource")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.captureButton.isEnabled = false
        binding.captureButton.setOnClickListener {
            captureImage()
        }
        startCamera()
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val fragmentSource = arguments?.getString("openedFromLayout") ?: "Unknown"

            val resultBundle = Bundle().apply {
                putBoolean("isImageCaptured", result.resultCode == Activity.RESULT_OK)
                putString("layoutSource", fragmentSource)
            }

            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let { bitmap ->
                    processImageAndValidate(bitmap)
                } ?: run {
                    sendResultBack(false, "")
                    parentFragmentManager.setFragmentResult("imageCaptured", resultBundle)
                }
            } else {
                sendResultBack(false, "")
                parentFragmentManager.setFragmentResult("imageCaptured", resultBundle)
            }
        }


    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {

            }
        }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun encodeImageToBase64(bitmap: android.graphics.Bitmap): String {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }

    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    private fun sendResultBack(result: Boolean, value: String) {
        val resultBundle = Bundle().apply {
            putString("layoutSource", layoutSource)
            putBoolean("result", result)
        }

        if (result && layoutSource != null) {
            AppCache.deliveryConditions.find { it.code == layoutSource }?.let { condition ->
                condition.value = value
                condition.images = listOf(value)
                condition.type = "image"

            }
        }

        layoutSource?.let {
            parentFragmentManager.setFragmentResult(it, resultBundle)
        }
        parentFragmentManager.popBackStack()
    }

    private fun processImageAndValidate(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text

                val hasIdNumber = Regex("\\b\\d{13}\\b").containsMatchIn(text)
                val hasCountryText = text.contains("REPUBLIC OF SOUTH AFRICA", ignoreCase = true)

                Log.e("MLKIT", "========================================OCR Text:\n$text")
                Log.e("MLKIT", "Detected ID Number: $hasIdNumber, Country Text: $hasCountryText")

                if (hasCountryText) {

                    val base64 = encodeImageToBase64(bitmap)
                    sendResultBack(true, base64)
                } else {
                    Toast.makeText(requireContext(), "Not a valid ID document", Toast.LENGTH_SHORT)
                        .show()
                    sendResultBack(false, "")
                }

                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Log.e("MLKIT", "OCR failed", it)
                sendResultBack(false, "")
                //Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show()
            }
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor, FaceTextAnalyzer { hasFace, hasText ->
                        if (isAdded) {
                            activity?.runOnUiThread {
                                val conditionMet = hasFace && hasText
                                binding.captureButton.isEnabled = conditionMet
                                binding.statusText.text = if (conditionMet)
                                    "✅ Ready to capture"
                                else
                                    "Show ID clearly"
                            }
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer
            )
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = imageProxyToBitmap(image)
                    val base64 = encodeImageToBase64(bitmap)
                    sendResultBack(true, base64)
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    class FaceTextAnalyzer(
        private val callback: (Boolean, Boolean) -> Unit
    ) : ImageAnalysis.Analyzer {

        private val faceDetector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build()
        )

        private val textRecognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        @OptIn(ExperimentalGetImage::class)
        override fun analyze(image: ImageProxy) {
            val mediaImage = image.image ?: run {
                image.close()
                return
            }

            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    val hasFace = faces.isNotEmpty()
                    textRecognizer.process(inputImage).addOnSuccessListener { result ->
                        val hasText = result.textBlocks.isNotEmpty()
                        callback(hasFace, hasText)
                        image.close()
                    }
                        .addOnFailureListener {
                            image.close()
                        }
                }
                .addOnFailureListener {
                    image.close()
                }
        }


    }
}