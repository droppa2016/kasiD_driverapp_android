package co.za.kasi.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import co.za.kasi.databinding.ManualParcelEntryDialogFragmentBinding

class ManualParcelEntryDialogFragment(
    private val title: String? = "Manual Parcel Entry",
    private val isCancelable: Boolean = true,
    private val onPositive: ((String) -> Unit)? = null,
    private val onNegative: (() -> Unit)? = null,
    private val onDismiss: (() -> Unit)? = null,
    private val buttonPositiveText: String? = "Submit",
    private val buttonNegativeText: String? = "Cancel",
) : DialogFragment() {
    private lateinit var binding: ManualParcelEntryDialogFragmentBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = ManualParcelEntryDialogFragmentBinding.inflate(layoutInflater)

        binding.alertTitle.text = title
        binding.btnPositive.text = buttonPositiveText
        binding.btnNegative.text = buttonNegativeText

        binding.btnPositive.setOnClickListener {
            dismiss()
            onPositive?.invoke(binding.input.text.toString())
        }

        binding.btnNegative.setOnClickListener {
            dismiss()
            onNegative?.invoke()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.setCancelable(isCancelable)
        dialog.setCanceledOnTouchOutside(isCancelable)

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }
}