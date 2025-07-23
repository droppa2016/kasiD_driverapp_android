package co.za.kasi.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.za.kasi.FinancialDashBoardActivity
import co.za.kasi.R
import co.za.kasi.databinding.FragmentSkyReportsRevenueBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.TripSummaryResponse
import co.za.kasi.recyclerview.SkyReportsRevenueAdapter
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.utils.ReusableFunctions.getFirstOfMonthFormatted
import co.za.kasi.utils.ReusableFunctions.getFormattedDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("NotifyDataSetChanged")
class SkyNetReportsRevenueFragment : Fragment() {

    private var _binding: FragmentSkyReportsRevenueBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SkyReportsRevenueAdapter
    private var loader: Loader? = null
    private var service: SuperAppHttpService? = null

    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity

    private val identityNumber = LocalStorage.getSkynetDriverAccount().driver.identityNo
    private val endDate = getFormattedDate()
    private val startDate = getFirstOfMonthFormatted(endDate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSkyReportsRevenueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        safeContext = requireContext()
        safeActivity = requireActivity()

        setupDashboardHighlight()
        setupRecyclerView()

        service = ReusableFunctions.initiateSuperAppRetrofit(safeContext)
        fetchTripSummary()
    }

    private fun setupDashboardHighlight() {
        (activity as? FinancialDashBoardActivity)?.binding?.apply {
            volumeArea.setBackgroundResource(R.color.card_background)
            expensesArea.setBackgroundResource(R.color.card_background)
            revenueArea.setBackgroundResource(R.color.app_background)
        }
    }

    private fun setupRecyclerView() {
        adapter = SkyReportsRevenueAdapter()
        binding.recyclerViewTrips.apply {
            adapter = this@SkyNetReportsRevenueFragment.adapter
            layoutManager = LinearLayoutManager(safeContext)
        }
    }

    private fun fetchTripSummary() {
        loader = ReusableFunctions.showLoader(safeContext)

        service?.getTripSummary(
            LocalStorage.getSuperAppToken(safeContext),
            identityNumber,
            startDate,
            endDate
        )?.enqueue(object : Callback<TripSummaryResponse> {
            override fun onResponse(
                call: Call<TripSummaryResponse>,
                response: Response<TripSummaryResponse>
            ) {
                loader?.let { ReusableFunctions.hideLoader(it) }

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    bindTripSummaryData(body)
                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }
                    showToast(getString(R.string.failed_to_get_trip_summary))
                }
            }

            override fun onFailure(call: Call<TripSummaryResponse>, t: Throwable) {
                loader?.let { ReusableFunctions.hideLoader(it) }
                showToast(getString(R.string.network_error_occurred))
            }
        })
    }

    private fun bindTripSummaryData(data: TripSummaryResponse) {
        binding.apply {
            revenueText.text = getString(R.string.r, data.revenue.toString())
            adminFeeText.text = getString(R.string.r, data.adminFee.toString())
            expensesText.text = getString(R.string.r, data.expenses.toString())
            totalProfitText.text = getString(R.string.r, data.totalProfit.toString())
        }
        adapter.setTrips(data.trips)
        adapter.notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(safeContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}