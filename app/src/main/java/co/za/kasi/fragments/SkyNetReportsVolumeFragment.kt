package co.za.kasi.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import co.za.kasi.FinancialDashBoardActivity
import co.za.kasi.R
import co.za.kasi.databinding.FragmentSkyReportsVolumeBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.WaybillStats
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.utils.ReusableFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SkyNetReportsVolumeFragment : Fragment() {
    private lateinit var binding: FragmentSkyReportsVolumeBinding
    private var loader: Loader? = null
    private var service: SuperAppHttpService? = null
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private lateinit var waybillStats: WaybillStats

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkyReportsVolumeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        safeContext = context ?: return
        safeActivity = activity ?: return
        service = ReusableFunctions.initiateSuperAppRetrofit(safeContext)

        (activity as FinancialDashBoardActivity).binding.let {
            it.volumeArea.setBackgroundResource(R.color.app_background)
            it.expensesArea.setBackgroundResource(R.color.card_background)
            it.revenueArea.setBackgroundResource(R.color.card_background)
        }

        getReportsVolume()

    }

    fun getReportsVolume() {
        loader = ReusableFunctions.showLoader(safeContext)

        val driverFinancials = service?.getWaybillStats(
            LocalStorage.getSuperAppToken(requireContext()),
            LocalStorage.getSkynetDriverAccount().driver.identityNo
        )

        driverFinancials?.enqueue(object : Callback<WaybillStats> {
            override fun onResponse(
                call: Call<WaybillStats?>,
                response: Response<WaybillStats?>
            ) {

                if (response.isSuccessful) {
                    ReusableFunctions.hideLoader(loader)
                    waybillStats = response.body()!!
                    binding.totalMonthlyDeliveriesValue.text =
                        getString(
                            R.string.total,
                            waybillStats.thisMonthTotalDeliveredWaybills.toString()
                        )
                    binding.totalMonthlyWaybillsValue.text =
                        waybillStats.thisMonthTotalWaybills.toString()
                    binding.revenueValue.text =
                        getString(R.string.r, waybillStats.monthlyRevenue.toString())
                    binding.expensesValue.text =
                        getString(R.string.r, waybillStats.monthlyExpenses.toString())
                    binding.profitValue.text =
                        getString(R.string.r, waybillStats.monthlyTotalProfit.toString())
                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }
                    Toast.makeText(safeContext, "Failed to get data", Toast.LENGTH_SHORT).show()
                    ReusableFunctions.hideLoader(loader)
                }
            }

            override fun onFailure(
                call: Call<WaybillStats?>,
                t: Throwable
            ) {
                ReusableFunctions.hideLoader(loader)
            }
        })
    }
}