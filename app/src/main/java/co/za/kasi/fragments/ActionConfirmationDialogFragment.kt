package co.za.kasi.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import co.za.kasi.R
import co.za.kasi.databinding.ActionConfirmationDialogFragmentBinding

class ActionConfirmationDialogFragment(
    private val isIconNeeded: Boolean = false,
    private val isError: Boolean = false,
    private val hasASingleButton: Boolean = false,
    private val buttonPositiveText: String? = null,
    private val buttonNegativeText: String? = null,
    private val buttonConfirmText: String = "Acknowledge",
    private val title: String? = null,
    private val message: String? = null,
    private val isCancelable: Boolean = true,
    private val onPositive: (() -> Unit)? = null,
    private val onNegative: (() -> Unit)? = null,
    private val onConfirm: (() -> Unit)? = null
) : DialogFragment() {
    private lateinit var binding: ActionConfirmationDialogFragmentBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = ActionConfirmationDialogFragmentBinding.inflate(layoutInflater)

        binding.alertTitle.text = title
        binding.alertMessage.text = message
        binding.btnPositive.text = buttonPositiveText
        binding.btnNegative.text = buttonNegativeText
        binding.btnConfirm.text = buttonConfirmText

        val displayIcon =
            if (isError) R.drawable.baseline_security_24 else R.drawable.baseline_check_circle_outline_24

        if(!isIconNeeded){
            binding.displayIcon.visibility = View.GONE
        }

        binding.displayIcon.setImageResource(displayIcon)

        if (hasASingleButton) {
            binding.optionButtonsSection.visibility = View.GONE
        } else {
            binding.btnConfirm.visibility = View.GONE
        }

        binding.btnPositive.setOnClickListener {
            dismiss()
            onPositive?.invoke()
        }

        binding.btnNegative.setOnClickListener {
            dismiss()
            onNegative?.invoke()
        }

        binding.btnConfirm.setOnClickListener {
            dismiss()
            onConfirm?.invoke()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.setCancelable(isCancelable)
        dialog.setCanceledOnTouchOutside(isCancelable)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }
}