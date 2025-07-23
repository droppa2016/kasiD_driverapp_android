package co.za.kasi.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import co.za.kasi.FinancialDashBoardActivity
import co.za.kasi.R
import co.za.kasi.databinding.FragmentSkynetReportsBinding

class SkyNetReportsFragment : Fragment() {
    private lateinit var binding: FragmentSkynetReportsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkynetReportsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeActivity = activity as FinancialDashBoardActivity

        homeActivity.binding.backButton.setOnClickListener {
            navigateBack(homeActivity)
        }

        homeActivity.binding.customTitle.text = getString(R.string.skynet_reports)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack(homeActivity)
                }
            })

    }

    fun navigateBack(activity: FinancialDashBoardActivity) {
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }

}