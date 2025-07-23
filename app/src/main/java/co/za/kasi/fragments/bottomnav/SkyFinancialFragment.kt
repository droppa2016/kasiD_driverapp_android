package co.za.kasi.fragments.bottomnav

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import co.za.kasi.FinancialDashBoardActivity
import co.za.kasi.databinding.FragmentSkyFinancialBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.enums.Financial
import co.za.kasi.model.FinancialInformation
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.utils.ReusableFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SkyFinancialFragment : Fragment() {
    private lateinit var binding: FragmentSkyFinancialBinding
    private var service: SuperAppHttpService? = null
    private var loader: Loader? = null
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private lateinit var financialInformation: FinancialInformation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkyFinancialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = context ?: return
        safeActivity = activity ?: return
        service = ReusableFunctions.initiateSuperAppRetrofit(safeContext)
        getDriverFinancial()

        binding.crdRateCard.setOnClickListener {
            FinancialDashBoardActivity.launchFinancialDashBoard(safeContext, Financial.RATES.name)
        }

        binding.crdReportCard.setOnClickListener {
            FinancialDashBoardActivity.launchFinancialDashBoard(safeContext, Financial.REPORTS.name)
        }

    }

    fun getDriverFinancial() {
        loader = ReusableFunctions.showLoader(safeContext)

        val driverFinancials = service?.getFinancialInformation(
            LocalStorage.getSuperAppToken(requireContext()),
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            getFormattedDate()
        )

        driverFinancials?.enqueue(object : Callback<FinancialInformation> {
            override fun onResponse(
                call: Call<FinancialInformation?>,
                response: Response<FinancialInformation?>
            ) {

                if (response.isSuccessful) {
                    ReusableFunctions.hideLoader(loader)
                    financialInformation = response.body()!!
                    binding.totalWaybillsText.text = financialInformation.totalWaybills.toString()
                    binding.todaysCollectionsText.text = "0"
                    binding.todaysDeliveriesText.text =
                        financialInformation.totalDeliveredWaybills.toString()
                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }

                    ReusableFunctions.hideLoader(loader)
                    Toast.makeText(safeContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<FinancialInformation?>,
                t: Throwable
            ) {
                ReusableFunctions.hideLoader(loader)
            }
        })
    }

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}