package co.za.kasi.fragments.secureDeliverypackage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.za.kasi.adapters.CapturedImageAdapter
import co.za.kasi.databinding.FragmentProofOfResidenceBinding
import co.za.kasi.fragments.ActionConfirmationDialogFragment
import co.za.kasi.services.AppCache

class ProofOfResidenceFragment : Fragment() {

    private var _binding: FragmentProofOfResidenceBinding? = null
    private val binding get() = _binding!!

    private var layoutSource: String? = null

    private var code: String? = null
    private val capturedImages = mutableListOf<String>()
    private var selectedImageIndex: Int? = null


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        val fragmentSource = arguments?.getString("openedFromLayout") ?: "Unknown"

        val resultBundle = Bundle().apply {
            putBoolean("isImageCaptured", result.resultCode == Activity.RESULT_OK)
            putString("layoutSource", fragmentSource)
        }

        var base64Image = ""
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? android.graphics.Bitmap
            imageBitmap?.let {
                base64Image = encodeImageToBase64(it)
                capturedImages.add(base64Image)
                selectedImageIndex = capturedImages.lastIndex
                binding.imageView.setImageBitmap(it)
                binding.imageView.visibility = View.VISIBLE

                updateImagePreviewList()
            }
        }

    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            // Handle permission denial
            binding.captureButton.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You can initialize additional resources here if needed
        code = arguments?.getString("fragmentSource")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProofOfResidenceBinding.inflate(inflater, container, false)

        layoutSource = arguments?.getString("fragmentSource")

        if (code == "POR") {
            ActionConfirmationDialogFragment(
                isIconNeeded = false,
                isError = true,
                hasASingleButton = true,
                isCancelable = false,
                title = "Please Note:",
                message = "A valid Proof of Residence document:\n\n" +
                        "1. Must be from a recognised institution.\n" +
                        "2. Must not be older than 3 months.\n" +
                        "3. Must have an address that corresponds with the delivery address.",
                buttonConfirmText = "Continue",
                onConfirm = {
                    checkCameraPermissionAndLaunch()
                }

            ).show(parentFragmentManager, "POR_policies")
        } else {
            checkCameraPermissionAndLaunch()

        }

        binding.captureMoreButton.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        binding.removeButton.setOnClickListener {
            selectedImageIndex?.let { index ->
                if (capturedImages.isNotEmpty()) {
                    capturedImages.removeAt(index)

                    if (capturedImages.isNotEmpty()) {
                        selectedImageIndex = capturedImages.lastIndex
                        val base64 = capturedImages[selectedImageIndex!!]
                        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        binding.imageView.setImageBitmap(bitmap)
                        binding.imageView.visibility = View.VISIBLE
                    } else {
                        selectedImageIndex = null
                        binding.imageView.setImageDrawable(null)
                        binding.imageView.visibility = View.GONE
                    }

                    updateImagePreviewList()
                }
            }
        }


        binding.doneButton.setOnClickListener {
            sendResultBack(true,layoutSource!!)
        }

        return binding.root
    }

    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun sendResultBack(result: Boolean,value : String) {
        val resultBundle = Bundle().apply {
            putString("layoutSource", layoutSource)
            putBoolean("result", result)
        }

        if (result && layoutSource != null) {
            AppCache.deliveryConditions.find { it.code == layoutSource }?.let { condition ->
                condition.value = value
                condition.images= capturedImages
                condition.type ="image"
            }
        }

        layoutSource?.let {
            parentFragmentManager.setFragmentResult(it, resultBundle)
        }
        parentFragmentManager.popBackStack()
    }

    private fun updateImagePreviewList() {
        binding.imageRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = CapturedImageAdapter(capturedImages, selectedImageIndex) { base64, position ->
                val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                binding.imageView.setImageBitmap(bitmap)
                binding.imageView.visibility = View.VISIBLE
                selectedImageIndex = position
                updateImagePreviewList() // Refresh to show the tick
            }
        }
    }


    private fun encodeImageToBase64(bitmap: android.graphics.Bitmap): String {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }
}
