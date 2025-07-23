package co.za.kasi.dialogs

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class FailDeliveryDialogFragment(
    private val onResult: (issue: String, base64Image: String) -> Unit
) : DialogFragment() {

    private var selectedIssue: String? = null

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.e("CameraResult", "!!!!!!!!!!!!!!!!!!!!!! - Result code: ${result.resultCode}")
        Log.e("CameraResult", "!!!!!!!!!!!!!!!!!!!!!!- Intent Data: ${result.data}")

        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            Log.e("CameraResult", "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ - - Image Bitmap: $imageBitmap")

            if (imageBitmap != null && selectedIssue != null) {
                val bitmapWithText = drawTextOnBitmap(imageBitmap,
                    "Waybill Number : 23",
                    "Issue : $selectedIssue",
                    "TIMESTAMP: " + LocalDateTime.now().toString())
                Log.e("CameraResult", "111111111111 - Image with text drawn")

                onResult(selectedIssue!!, encodeImageToBase64(bitmapWithText))
               dismiss()
            } else {
                Log.e("CameraResult", "Bitmap or Issue is null!")
            }
        } else {
            Log.e("CameraResult", "Camera result not OK!")
        }
    }


    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Log.e("Permission", "Camera permission denied")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val issues = arrayOf(
            "Vehicle breakdown", "Robbery / Hijacking", "Missed cut-off",
            "Appointment requested", "Company closed", "Wrong product", "Industrial Action",
            "Client refused delivery", "Consignee not available", "Parcel damaged",
            "Insufficient documents", "Bad address"
        )

        var tempSelection = issues[0]

        return AlertDialog.Builder(requireContext())
            .setTitle("Select Delivery Issue")
            .setSingleChoiceItems(issues, -1) { _, which ->
                tempSelection = issues[which]
            }
            .setPositiveButton("OK") { _, _ ->
                selectedIssue = tempSelection
                checkCameraPermissionAndLaunch()

            }
            .setNegativeButton("Cancel") { _, _ ->
                dismiss()
            }
            .create()
    }

    private fun encodeImageToBase64(bitmap: android.graphics.Bitmap): String {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }



    private fun drawTextOnBitmap(
        bitmap: Bitmap,
        line1: String,
        line2: String,
        line3: String
    ): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        paint.textSize = 25f
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        val x = 20
        val y = 60
        val lineHeight = 50
        canvas.drawText(line1, x.toFloat(), y.toFloat(), paint)
        canvas.drawText(line2, x.toFloat(), (y + lineHeight).toFloat(), paint)
        canvas.drawText(line3, x.toFloat(), (y + lineHeight * 2).toFloat(), paint)
        return mutableBitmap
    }

    private fun showImagePreviewDialog(bitmap: Bitmap, issue: String) {
        val context = requireContext()
        val imageView = android.widget.ImageView(context).apply {
            setImageBitmap(bitmap)
            adjustViewBounds = true
            setPadding(16, 16, 16, 16)
        }

        Log.d("ImagePreview", "Displaying image preview dialog")

        AlertDialog.Builder(context)
            .setTitle("Confirm Image")
            .setView(imageView)
            .setPositiveButton("OK") { _, _ ->
                val base64 = encodeImageToBase64(bitmap)
                onResult(issue, base64)
                dismiss()
            }
            .setNegativeButton("Retry") { _, _ ->
                openCamera()
            }
            .setOnDismissListener {
                Log.d("ImagePreview", "Image preview dialog dismissed")
            }
            .show()
    }
}
