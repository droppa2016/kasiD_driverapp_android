package co.za.kasi.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import co.za.kasi.databinding.ReceiverNameDialogFragmentBinding
import co.za.kasi.model.superApp.a.waybillData.ScanParcel

class ReceiverNameDialogFragment(
    private val parcels: List<ScanParcel> = emptyList(),
    private var isParcelSectionExpanded: Boolean = false,
    private val onBackClicked: (() -> Unit)? = null,
    private val onNameEntered: (String) -> Unit
) : DialogFragment() {
    private lateinit var binding: ReceiverNameDialogFragmentBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        binding = ReceiverNameDialogFragmentBinding.inflate(layoutInflater)

        dialog?.setOnKeyListener { _, keyCode, _ ->
            keyCode == android.view.KeyEvent.KEYCODE_BACK
        }

        binding.btnBackDialog.setOnClickListener {
            dismiss()
            onBackClicked?.invoke()
        }

        binding.btnSubmitName.setOnClickListener {
            val enteredName = binding.nameInput.text.toString().trim()
            if (enteredName.isEmpty()) {
                binding.nameInput.error = "Name cannot be empty"
            } else {
                dismiss()
                onNameEntered(enteredName)
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        // 🔥 Intercept hardware back press
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                onBackClicked?.invoke() // Your custom back logic
                dismiss()
                true // consume the event
            } else {
                false
            }
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        binding.nameInput.requestFocus()
        binding.nameInput.postDelayed({
            val imm = (context?.getSystemService(Context.INPUT_METHOD_SERVICE)
                ?: "") as InputMethodManager
            imm.showSoftInput(binding.nameInput, InputMethodManager.SHOW_IMPLICIT)
        }, 100)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }
}