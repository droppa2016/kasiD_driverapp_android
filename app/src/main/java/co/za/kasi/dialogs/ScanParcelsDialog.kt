package co.za.kasi.dialogs

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import co.za.kasi.R
import co.za.kasi.fragments.ActionConfirmationDialogFragment
import co.za.kasi.model.BucketBooking
import co.za.kasi.model.accountDTO.DriverDTO
import co.za.kasi.model.accountDTO.UserDTO
import co.za.kasi.services.DriverHttpService
import co.za.kasi.services.LocalStorage
import co.za.kasi.utils.CameraSurfaceView
import co.za.kasi.utils.ReusableFunctions
import com.google.android.material.textview.MaterialTextView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class ScanParcelsDialog : DialogFragment() {
    private var btnBackButton: AppCompatImageButton? = null
    private var btnWhatsappButton: AppCompatImageButton? = null
    private var title: MaterialTextView? = null
    var toolbar: Toolbar? = null
    private var user: UserDTO? = null
    private var driver: DriverDTO? = null
    private var token: String? = null
    private var crdCaptureParcel: CardView? = null
    private var surfaceView: SurfaceView? = null

    private var onPreview: LinearLayout? = null
    private var retry: AppCompatButton? = null
    private var upload: AppCompatButton? = null
    private var path: String? = null
    private var scanned = false
    private var services: DriverHttpService? = null
    private var cameraSurfaceView: CameraSurfaceView? = null
    private var loader: Loader? = null
    var returnStatus: ReturnStatus? = null

    var contactList: ArrayList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      //  requireView() = requireActivity().layoutInflater.inflate(R.layout.scanning_parcels, container, false)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        )
        val window = dialog!!.window
        dialog!!.setCancelable(false)
        window!!.setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_transparent_backgroudn))
        init()



        dialog!!.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // Handle the back button press here
                dismiss()
                return@OnKeyListener true // Consume the event
            }
            false // Pass the event to the next listener
        })
        return requireActivity().layoutInflater.inflate(R.layout.scanning_parcels, container, false)
    }

    override fun onStart() {
        super.onStart()
        // Close the camera
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onPause() {
        super.onPause()
        // Open the camera
        cameraSurfaceView!!.closeCamera()
        cameraSurfaceView!!.closeCamera()
    }

    private fun init() {
        contactList = ArrayList()
        contactList = requireArguments().getStringArrayList("contactList")
        services = ReusableFunctions.initiateRetrofit(context)
        toolbar = requireView().findViewById(R.id.custom_toolbar)
        // Set a custom close icon
        returnStatus = requireView().context as ReturnStatus
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar.setCustomView(R.layout.custon_action_bar)
        actionBar.show()
        title = requireView().findViewById(R.id.txtTitle)
        btnBackButton = requireView().findViewById(R.id.btnBack)
        btnWhatsappButton = requireView().findViewById(R.id.btnWhatsapp)
        surfaceView = requireView().findViewById(R.id.surface_view)
        retry = requireView().findViewById(R.id.btnRetry)
        upload = requireView().findViewById(R.id.btnUpload)
        onPreview = requireView().findViewById(R.id.lytonPreview)
        crdCaptureParcel = requireView().findViewById(R.id.crdCaptureParcel)
        localData
        cameraSurfaceView = requireView().findViewById(R.id.cameraSurfaceView)
        cameraSurfaceView?.setWaybill(contactList.toString())

        title?.setText("Scan Parcels")
        btnBackButton?.setOnClickListener(View.OnClickListener { v: View? ->
            returnStatus?.getStatus(false)
            dismiss()
        })
        cameraSurfaceView?.setOnImageCapturedListener(CameraSurfaceView.OnImageCapturedListener { imageUri ->
            path = imageUri.encodedPath
            activity?.runOnUiThread {
                onPreview?.setVisibility(View.VISIBLE)
                crdCaptureParcel?.setVisibility(View.GONE)
            }
        })
        crdCaptureParcel?.setOnClickListener(View.OnClickListener { v: View? -> cameraSurfaceView?.captureImage() })
        retry?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                return@setOnClickListener
            }

            onPreview?.visibility = View.GONE
            crdCaptureParcel?.visibility = View.VISIBLE

            cameraSurfaceView?.closeCamera()
            cameraSurfaceView?.openCamera()
        }

        upload?.setOnClickListener(View.OnClickListener { v: View? ->
            loader = ReusableFunctions.showLoader(context)
            uploadImage(path)
        })
    }

    private fun uploadImage(uri: String?) {
        try {
            val file = File(uri)
            val bitmap = MediaStore.Images.Media
                .getBitmap(requireContext().contentResolver, Uri.fromFile(file))
            if (bitmap == null) {
                Log.d(
                    "SELECTED_RETURN",
                    "============= /  // " + "An unknown problem occurred while taking an image."
                )
                ReusableFunctions.hideLoader(loader)
                return
            }
            val imageFile = File(uri)
            Log.e(
                "SELECTED_RETURN",
                "********************* This is the path ****************" + imageFile.canRead()
            )
            val fileReqBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val part: MultipartBody.Part =
                MultipartBody.Part.createFormData("invoiceImage", "delivery-note.jpg", fileReqBody)
            val bookingOid =
                RequestBody.create("text/plain".toMediaTypeOrNull(), "currentBooking.getOid()")
            val bucketOid = RequestBody.create("text/plain".toMediaTypeOrNull(), "bucket.getOid()")
            val returnComment = RequestBody.create("text/plain".toMediaTypeOrNull(), "returnCommentValue")

            // RequestBody returnItems;
            Log.e("SELECTED_RETURN", "BEFORE SENDING Return item value: " + "isReturnItems")
            Log.e(
                "SELECTED_RETURN",
                "BEFORE SENDING Return item value: isReturnItems   =    $token"
            )
            Log.e("SELECTED_RETURN", "RETURN_COMMENT FROM DRIVER: $returnComment")
            //Sending the image here
            val returnItems =
                RequestBody.create("text/plain".toMediaTypeOrNull(), "isReturnItems".toString())
            val call = services!!.uploadImage(
                token,
                part,
                bookingOid,
                bucketOid,
                returnItems,
                returnComment
            )
            Log.e("SELECTED_RETURN", "AFTER SENDING Return item value: " + "isReturnItems")
            call.enqueue(object : Callback<BucketBooking?> {
                override fun onResponse(
                    call: Call<BucketBooking?>,
                    response: Response<BucketBooking?>
                ) {
                    ReusableFunctions.hideLoader(loader)
                    if (response.code() == 200) {
                        showPopupDialog()
                        scanned = true
                        Log.e(
                            "SELECTED_RETURN",
                            "=======Ask if theres another POD then=========Open camera again=========="
                        )
                        cameraSurfaceView!!.openCamera()
                    } else {
                        showPopupDialog()
                        scanned = false
                        Log.e(
                            "SELECTED_RETURN_",
                            "================  " + response.code() + " An unknown problem occurred while completing booking " + response.message()
                        )
                        try {
                            Log.e(
                                "SELECTED_RETURN_", "================  " + response.errorBody()!!
                                    .string() + " An unknown problem occurred while completing booking " + response.message()
                            )
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }
                    }
                }

                override fun onFailure(call: Call<BucketBooking?>, t: Throwable) {
                    ReusableFunctions.hideLoader(loader)
                    Log.e(
                        "SELECTED_RETURN",
                        "========= An unexpected problem occurred while completing booking =======" + t.localizedMessage,
                        t
                    )
                }
            })
        } catch (e: Exception) {
            ReusableFunctions.hideLoader(loader)
            e.stackTrace
            Log.e("SELECTED_RETURN", "Hello -> " + e.message, e)
        }
    }

    private val localData: Unit
        get() {
            user = LocalStorage.getUserAccount()
            driver = LocalStorage.getDriverAccount()
            token = LocalStorage.getToken(context)
            Log.e(
                "SELECTED_RETURN",
                "BEFORE SENDING Return item value: isReturnItems   =    $token"
            )
        }

    private fun showPopupDialog() {
        ActionConfirmationDialogFragment(
            isError = true,
            buttonNegativeText = "No",
            buttonPositiveText ="Yes",
            title = "Upload was successful",
            message = "Your Proof of delivery was uploaded successfully, Would you like to capture another one?",
            onPositive = {
                cameraSurfaceView!!.openCamera()
                onPreview!!.visibility = View.GONE
                crdCaptureParcel!!.visibility = View.VISIBLE
            },
            onNegative = {
                returnStatus!!.getStatus(true)
                dismiss()
                dialog?.dismiss()
            }
        ).show(
            requireActivity().supportFragmentManager,
            "upload_success_dialog"
        )
    }

    interface ReturnStatus {
        fun getStatus(status: Boolean)
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 201
    }
}
