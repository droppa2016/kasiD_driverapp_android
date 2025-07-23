package co.za.kasi.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.net.toUri
import co.za.kasi.R
import co.za.kasi.databinding.UpdateBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdateBottomSheet(
    private val isForcedUpdate: Boolean = false,
    private val onNegative: (() -> Unit)? = null,

    ) : BottomSheetDialogFragment() {
    private lateinit var binding: UpdateBottomSheetBinding
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                finishAffinity(safeActivity)
                true
            } else {
                false
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UpdateBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = context ?: return
        safeActivity = activity ?: return

        binding.alertTitle.text =
            if (isForcedUpdate) getString(R.string.important_update_required)
            else getString(R.string.new_version_available)

        binding.displayIcon.setImageResource(R.drawable.play_store_icon)

        binding.alertMessage.text =
            if (isForcedUpdate) getString(R.string.this_version_of_the_app_is_no_longer_supported_please_update_to_continue)
            else getString(R.string.we_ve_added_exciting_new_features_and_improvements_update_now_for_the_best_experience)

        binding.alertMessage.setTextColor(resources.getColor(R.color.black, null))

        binding.btnPositive.text =
            if (isForcedUpdate) getString(R.string.update_now)
            else getString(R.string.update)

        binding.btnNegative.text =
            if (isForcedUpdate) getString(R.string.cancel)
            else getString(R.string.remind_me_later)

        binding.btnNegative.visibility =
            if (isForcedUpdate) View.GONE
            else View.VISIBLE

        binding.btnPositive.setOnClickListener {
            dismiss()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = getString(R.string.https_play_google_com_store).toUri()
            intent.setPackage("com.android.vending")
            startActivity(intent)
            finishAffinity(safeActivity)
        }
        binding.btnNegative.setOnClickListener {
            onNegative?.invoke()
            dismiss()
        }
    }
}