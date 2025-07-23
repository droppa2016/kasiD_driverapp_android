package co.za.kasi.dialogs

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import co.za.kasi.R
import java.io.ByteArrayInputStream
import java.io.File

class DeliveryIssueDialog : DialogFragment() {

    private lateinit var previewView: PreviewView
    private lateinit var issueSpinner: Spinner
    private lateinit var recipientName: EditText
    private lateinit var notesText: EditText
    private lateinit var btnCapture: Button
    private lateinit var previewLayout: RelativeLayout
    private lateinit var imagePreview: ImageView
    private lateinit var btnDone: Button
    private lateinit var base64: String

    private var callback: ((issue: String, base64: String, name: String, notes : String) -> Unit)? = null
    private var selectedIssue: String = ""
    private var imageCapture: ImageCapture? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        return inflater.inflate(R.layout.dialog_delivery_issue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        previewView = view.findViewById(R.id.previewView)
        issueSpinner = view.findViewById(R.id.issueSpinner)
        btnCapture = view.findViewById(R.id.btnCapture)
        recipientName = view.findViewById(R.id.nameEditText)
        notesText = view.findViewById(R.id.notesText)
        previewLayout = view.findViewById(R.id.previewLayout)
        imagePreview = view.findViewById(R.id.imagePreview)
        btnDone = view.findViewById(R.id.btnDoneFinish)

        setupSpinner()
        checkPermissionAndLaunch()

        recipientName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (selectedIssue != "") {
                    previewLayout.visibility = View.VISIBLE
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Select reason for failure.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        })

        btnCapture.setOnClickListener {
            if (btnCapture.isEnabled) {
                capturePhoto()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select a reason before capturing.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnDone.setOnClickListener {
            //  if(notesText.text.isNotEmpty()) {

            callback?.invoke(selectedIssue, base64, recipientName.text.toString(), notesText.text.toString())
            dismiss()
            //  }else{
            //      Toast.makeText(requireContext(), "Please select a reason before capturing.", Toast.LENGTH_SHORT).show()
            //  }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }


    private fun setupSpinner() {
        val issues = arrayOf(
            "Click here to select a reason", // hint item
            "Vehicle breakdown", "Robbery / Hijacking", "Missed cut-off",
            "Appointment requested", "Company closed", "Wrong product", "Industrial Action",
            "Client refused delivery", "Consignee not available", "Parcel damaged",
            "Insufficient documents", "Bad address"
        )

        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            issues
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(
                    if (position == 0) Color.GRAY else Color.BLACK
                )
                return view
            }
        }

        issueSpinner.adapter = adapter

        issueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    selectedIssue = issues[position]
                    btnCapture.isEnabled = true
                    recipientName.visibility = View.VISIBLE
                } else {
                    selectedIssue = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedIssue = ""
            }
        }

        issueSpinner.setSelection(0)
    }


    private fun checkPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e(
                    "DeliveryIssueDialog",
                    "+++++++++++++++++++++++++++++++++++Camera binding failed",
                    e
                )
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun capturePhoto() {
        val capture = imageCapture ?: return

        val photoFile = File.createTempFile("issue_photo_", ".jpg", requireContext().cacheDir)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    base64 = encodeFileToBase64(photoFile)
                    //callback?.invoke(selectedIssue, base64, recipientName.text.toString())
                    previewLayout.visibility = View.GONE
                    imagePreview.visibility = View.VISIBLE
                    displayBase64Image(base64, imagePreview)
                    notesText.visibility = View.VISIBLE
                    btnDone.visibility = View.VISIBLE

                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("DeliveryIssueDialog", "Capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    private fun encodeFileToBase64(file: File): String {
        val bytes = file.readBytes()
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
    }

    fun displayBase64Image(base64String: String, imageView: ImageView) {
        val cleanBase64 = base64String.substringAfter(",")
        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

        val exif = ExifInterface(ByteArrayInputStream(decodedBytes))
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        var bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }

        imageView.setImageBitmap(rotatedBitmap)
    }

    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun setOnResult(callback: (issue: String, image: String, name: String, notes : String) -> Unit) {
        this.callback = callback
    }
}