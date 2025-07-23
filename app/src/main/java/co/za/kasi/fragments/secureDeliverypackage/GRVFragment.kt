package co.za.kasi.fragments.secureDeliverypackage

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.za.kasi.R
import co.za.kasi.SecureDelivery
import co.za.kasi.adapters.DeliveredParcelAdapter
import co.za.kasi.adapters.ParcelAdapter
import co.za.kasi.databinding.FragmentGRVBinding
import co.za.kasi.dialogs.ScanBarcodeDialog
import co.za.kasi.services.AppCache

class GRVFragment : Fragment() {
    private lateinit var binding: FragmentGRVBinding
    private lateinit var adapter: ParcelAdapter
    private var layoutSource: String? = null
    private var isParcelSectionExpanded: Boolean = false
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGRVBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = requireContext()
        safeActivity = requireActivity()
        layoutSource = arguments?.getString("fragmentSource")

        val activity = requireActivity()
        val actionBar = (activity as? SecureDelivery)?.supportActionBar
        if (actionBar?.customView != null) {
            val backButton = actionBar.customView.findViewById<View>(R.id.btnBack)
            backButton?.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        val adapter = DeliveredParcelAdapter(AppCache.scanParcelList)
        binding.parcelRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.parcelRecyclerView.adapter = adapter

        // Now assign adapter and then calculate height
        binding.parcelRecyclerView.adapter = adapter
        binding.topAuthLayout.unauthorizedHeading.text = "${layoutSource} Number"
        binding.topAuthLayout.unauthorizedSubText.text =
            "Enter ${layoutSource} or scan Barcode/QR code"
        binding.grvNumber.hint = "${layoutSource} Number"

        binding.btnSubmitGrvNum.setOnClickListener {
            if (binding.grvNumber.text?.isNotEmpty() == true) {
                sendResultBack(true, binding.grvNumber.text.toString())
            } else {
                Toast.makeText(requireContext(), "Enter ${layoutSource} or scan Barcode/QR code", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.scanSearchLayout.setOnClickListener {
            val dialog = ScanBarcodeDialog()
            dialog.setOnBarcodeScannedListener { scannedValue ->
                Toast.makeText(requireContext(), "Scanned: $scannedValue", Toast.LENGTH_SHORT)
                    .show()
                binding.grvNumber.setText(scannedValue)

            }
            dialog.show(parentFragmentManager, "BarcodeScanDialog")

        }

        binding.totalNumberOfItemsText.text = AppCache.scanParcelList.size.toString()

        binding.expandCollapseButton.setImageResource(
            if (isParcelSectionExpanded) R.drawable.angle_down_icon
            else R.drawable.angle_up_icon
        )

        binding.expandCollapseSection.setOnClickListener {
            isParcelSectionExpanded = !isParcelSectionExpanded

            if (isParcelSectionExpanded) {
                binding.expandCollapseButton.setImageResource(R.drawable.angle_down_icon)
                binding.parcelsArea.visibility = View.VISIBLE
            } else {
                binding.expandCollapseButton.setImageResource(R.drawable.angle_up_icon)
                binding.parcelsArea.visibility = View.GONE
            }
        }
    }

    private fun sendResultBack(result: Boolean, value: String) {
        val resultBundle = Bundle().apply {
            putString("layoutSource", layoutSource)
            putBoolean("result", result)
        }

        if (result && layoutSource != null) {
            AppCache.deliveryConditions.find { it.code == layoutSource }?.let { condition ->
                condition.value = value
            }
        }

        layoutSource?.let {
            parentFragmentManager.setFragmentResult(it, resultBundle)
        }
        parentFragmentManager.popBackStack()
    }
}