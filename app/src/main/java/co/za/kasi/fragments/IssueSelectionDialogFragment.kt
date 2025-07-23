package co.za.kasi.dialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import co.za.kasi.databinding.IssueSelectionDialogFragmentBinding

class IssueSelectionDialogFragment(
    private val title: String,
    private val positiveButtonText: String,
    private val negativeButtonText: String,
    private val onPositiveButtonClick: (String) -> Unit = {},
    private val onNegativeButtonClick: () -> Unit = {},
    private val issues: Array<String>,
) : DialogFragment() {

    private var selectedIndex = 0
    private lateinit var binding: IssueSelectionDialogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IssueSelectionDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_single_choice, issues)
        binding.titleText.text = title
        binding.btnPositive.text = positiveButtonText
        binding.btnNegative.text = negativeButtonText

        binding.issueListView.adapter = adapter
        binding.issueListView.setItemChecked(selectedIndex, true)

        binding.issueListView.setOnItemClickListener { _, _, position, _ ->
            selectedIndex = position
        }

        binding.btnPositive.setOnClickListener {
            onPositiveButtonClick(issues[selectedIndex])
            dismiss()
        }

        binding.btnNegative.setOnClickListener {
            onNegativeButtonClick()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}