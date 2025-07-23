package co.za.kasi.fragments

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
import co.za.kasi.FinancialDashBoardActivity
import co.za.kasi.R
import co.za.kasi.databinding.FragmentSkynetRatesBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.RateStructures
import co.za.kasi.recyclerview.SkyRatesTierAdapter
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.utils.ReusableFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SkyNetRatesFragment : Fragment() {
    private lateinit var binding: FragmentSkynetRatesBinding
    private lateinit var adapter: SkyRatesTierAdapter
    private var loader: Loader? = null
    private var service: SuperAppHttpService? = null
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private lateinit var rateStructures: RateStructures

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkynetRatesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeActivity = activity as FinancialDashBoardActivity
        safeContext = context ?: return
        safeActivity = activity ?: return
        service = ReusableFunctions.initiateSuperAppRetrofit(safeContext)
        adapter = SkyRatesTierAdapter()
        binding.tierList.adapter = adapter
        binding.tierList.layoutManager = LinearLayoutManager(safeContext)

        homeActivity.binding.backButton.setOnClickListener {
            navigateBack(homeActivity)
        }

        homeActivity.binding.topAppBar.visibility = View.VISIBLE

        homeActivity.binding.customTitle.text = getString(R.string.skynet_rates)

        homeActivity.binding.tabSelectionArea.visibility = View.GONE

        getRatesStructure()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack(homeActivity)
                }
            })
    }

    fun getRatesStructure() {
        loader = ReusableFunctions.showLoader(safeContext)

        val driverFinancials = service?.getRateStructures(
            LocalStorage.getSuperAppToken(requireContext()),
            LocalStorage.getSkynetDriverAccount().driver.permanentRoute
        )

        driverFinancials?.enqueue(object : Callback<RateStructures> {
            override fun onResponse(
                call: Call<RateStructures?>,
                response: Response<RateStructures?>
            ) {
                if (response.isSuccessful) {
                    ReusableFunctions.hideLoader(loader)
                    rateStructures = response.body()!!
                    binding.tvRouteName.text = rateStructures.name
                    binding.tvBranchName.text = rateStructures.branch.name
                    binding.tvFixedRateAmount.text = rateStructures.fixedAmount.toString()
                    binding.tvRateType01.text = rateStructures.name
                    binding.tvProvince01.text = rateStructures.branch.name
                    adapter.setTiers(rateStructures.tiers)
                } else {
                    ReusableFunctions.hideLoader(loader)
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }
                    Toast.makeText(
                        safeContext,
                        getString(R.string.failed_to_get_rates_structure), Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onFailure(
                call: Call<RateStructures?>,
                t: Throwable
            ) {
                ReusableFunctions.hideLoader(loader)
                Toast.makeText(
                    safeContext,
                    getString(R.string.failed_to_get_rates_structure), Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    fun navigateBack(activity: FinancialDashBoardActivity) {
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }
}