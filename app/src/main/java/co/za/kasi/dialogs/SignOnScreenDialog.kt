package co.za.kasi.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import co.za.kasi.R
import co.za.kasi.databinding.SignningOnScreenBinding
import co.za.kasi.model.superApp.a.waybillData.ScanParcel
import co.za.kasi.services.AppCache
import co.za.kasi.utils.ReusableFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

class SignOnScreenDialog : DialogFragment() {
    private lateinit var binding: SignningOnScreenBinding
    private lateinit var adapter: ArrayAdapter<ScanParcel>
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private lateinit var parcels: List<ScanParcel>
    private var isParcelSectionExpanded: Boolean = false
    private var loader: Loader? = null
    private var callback: ReturnStatus? = null
    private var receiverName: String? = null
    private var hasPromptedName = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = context ?: return
        safeActivity = activity ?: return
        parcels = AppCache.getScanParcelList()

        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        dialog?.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK || event.action == KeyEvent.ACTION_UP) {
                getDialog()?.dismiss()
                return@OnKeyListener true
            }
            false
        })

        adapter = object : ArrayAdapter<ScanParcel>(
            requireContext(),
            R.layout.item_parcel_scanned,
            R.id.tvParcelId,
            parcels
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = super.getView(position, convertView, parent)
                val item = getItem(position)
                val tv = row.findViewById<TextView>(R.id.tvParcelId)
                tv.text = item?.parcel_number ?: "Unknown Parcel"
                return row
            }
        }

        binding.parcelListView.adapter = adapter

        binding.totalNumberOfItemsText.text = parcels.size.toString()

//        binding.expandCollapseButton.setImageResource(
//            if (isParcelSectionExpanded) R.drawable.angle_down_icon
//            else R.drawable.angle_up_icon
//        )
//
//        binding.expandCollapseSection.setOnClickListener {
//            toggleParcelSection()
//        }

        init()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dismiss()
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SignningOnScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        AppCache.setSignatureBase64(null)
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    override fun onResume() {
        super.onResume()

        if (!hasPromptedName) {
            hasPromptedName = true

            binding.signaturePad.isEnabled = false

            view?.post {
                ReceiverNameDialogFragment(
                    parcels = AppCache.getScanParcelList(),
                    onBackClicked = {
                        this@SignOnScreenDialog.dismiss()
                    },
                    onNameEntered = { name ->
                        receiverName = name
                        AppCache.setReceiverName(name)
                    }
                ).show(parentFragmentManager, "receiver_name_dialog")
            }
        }
    }

    private fun init() {

        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar.setCustomView(R.layout.custon_action_bar)
        actionBar.show()

        binding.btnBack.setOnClickListener(View.OnClickListener { v: View? -> dismiss() })

        binding.crdClear.setOnClickListener(View.OnClickListener { v: View? ->
            binding.previewImage.setVisibility(View.INVISIBLE)
            binding.signaturePad.clear()
            AppCache.setSignatureBase64(null)
            binding.crdSaveSignature.text = getString(R.string.apply_signature)
        })

        binding.crdSaveSignature.setOnClickListener(View.OnClickListener { v: View? ->
            if (binding.crdSaveSignature.getText() == getString(R.string.apply_signature)) {

                if (receiverName.isNullOrEmpty()) {
                    Toast.makeText(context, "Enter Receiver Name", Toast.LENGTH_SHORT).show()
                    ReceiverNameDialogFragment(
                        parcels = AppCache.getScanParcelList(),
                        onBackClicked = {
                            this@SignOnScreenDialog.dismiss()
                        },
                        onNameEntered = { name ->
                            receiverName = name
                        }
                    ).show(parentFragmentManager, "receiver_name_dialog")
                } else if (binding.signaturePad.isEmpty) {
                    Toast.makeText(context, "Pad is empty", Toast.LENGTH_SHORT).show()
                } else {
                    showShortLoader()
                    val path = binding.signaturePad.saveSignature()
                    val signature = getBitmapFromFilePath(path!!)

                    uploadImage(path)
                }
            } else if (binding.crdSaveSignature.getText() == "DONE") {
                dismiss()
                if (callback != null) {
                    callback!!.getSignatureStatus(true)
                }
            }
        })
    }

    private fun uploadImage(signaturePath: String) {
        try {
            val baseSignature = getBitmapFromFilePath(signaturePath)
            for ((number) in AppCache.getCurrentWaybillList()) {
                var watermarkedBitmap: Bitmap? = null
                watermarkedBitmap = drawTextOnBitmap(
                    baseSignature,
                    "WAYBILL : $number",
                    "RECEIVED: $receiverName",
                    "TIMESTAMP: " + LocalDateTime.now().toString()
                )
                val baos = ByteArrayOutputStream()
                watermarkedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val base64Image = "data:image/jpeg;base64," +
                        Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT).trim { it <= ' ' }
                AppCache.setSignatureForWaybill(number, base64Image)
            }

            val numbers = AppCache.getCurrentWaybillList()?.map { it.number } ?: emptyList()
            val formattedWaybills = numbers.chunked(2)
                .joinToString("\n") { pair -> pair.joinToString(", ") }

            binding.previewImage.setImageBitmap(
                drawTextOnBitmap(
                    baseSignature,
                    "WAYBILL: $formattedWaybills ",
                    "RECEIVED: $receiverName",
                    "TIMESTAMP: " + LocalDateTime.now().toString()
                )
            )

            binding.previewImage.visibility = View.VISIBLE

            binding.crdSaveSignature.text = "DONE"

        } catch (e: Exception) {
            Log.e("UPLOAD_ERROR", "uploadImagesForAllWaybills: " + e.message)
            Toast.makeText(context, "Failed to save signature images", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadSignature(image: String) {
        AppCache.setSignatureBase64(image)
        //dismiss();
    }

    private fun getBitmapFromFilePath(filePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun drawTextOnBitmap(bitmap: Bitmap, vararg lines: String): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 25f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val x = 20f
        var y = 60f
        val lineHeight = 40f

        for (line in lines) {
            // If the line has newlines, draw each line separately
            line.split("\n").forEach { subLine ->
                canvas.drawText(subLine, x, y, paint)
                y += lineHeight
            }
        }

        return mutableBitmap
    }

    fun setCallback(callback: ReturnStatus?) {
        this.callback = callback
        this.callback = callback
    }

    private fun showShortLoader() {
        loader = ReusableFunctions.showLoader(requireContext())

        CoroutineScope(Dispatchers.Main).launch {
            kotlinx.coroutines.delay(2000) // 2 seconds
            ReusableFunctions.hideLoader(loader)
        }
    }

    interface ReturnStatus {
        fun getSignatureStatus(status: Boolean)
    }

//    private fun toggleParcelSection() {
//        val view = binding.parcelsArea
//
//        if (isParcelSectionExpanded) {
//            // Collapse
//            val initialHeight = view.measuredHeight
//            val animator = ValueAnimator.ofInt(initialHeight, 0)
//            animator.addUpdateListener {
//                val value = it.animatedValue as Int
//                view.layoutParams.height = value
//                view.requestLayout()
//            }
//            animator.duration = 300
//            animator.start()
//
//            view.postDelayed({ view.visibility = View.GONE }, 300)
//            binding.expandCollapseButton.setImageResource(R.drawable.angle_up_icon)
//
//        } else {
//            // Expand
//            view.visibility = View.VISIBLE
//            view.measure(
//                View.MeasureSpec.makeMeasureSpec(binding.root.width, View.MeasureSpec.EXACTLY),
//                View.MeasureSpec.UNSPECIFIED
//            )
//            val targetHeight = view.measuredHeight
//            view.layoutParams.height = 0
//
//            val animator = ValueAnimator.ofInt(0, targetHeight)
//            animator.addUpdateListener {
//                val value = it.animatedValue as Int
//                view.layoutParams.height = value
//                view.requestLayout()
//            }
//            animator.duration = 300
//            animator.start()
//
//            binding.expandCollapseButton.setImageResource(R.drawable.angle_down_icon)
//        }
//
//        isParcelSectionExpanded = !isParcelSectionExpanded
//    }
}