package co.za.kasi.fragments.bottomnav

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import co.za.kasi.DeliveriesList
import co.za.kasi.FailedDeliveriesList
import co.za.kasi.R
import co.za.kasi.SkynetHome
import co.za.kasi.SuccessfulDeliveriesList
import co.za.kasi.databinding.ActivityFinancialDashboardHomeBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.db.sharedPreferance.PendingWaybillsStorage
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.DailyStatistics
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.AppCache
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.NetworkMonitor
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.utils.SharedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SkyHomeFragment : Fragment() {

    private lateinit var binding: ActivityFinancialDashboardHomeBinding
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private var service: SuperAppHttpService? = null
    private var loader: Loader? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityFinancialDashboardHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        safeContext = requireContext()
        safeActivity = requireActivity()
        instance = this

        init()

        // Start observing persistent state
        SharedState.observeDataStore(viewLifecycleOwner, safeContext)

        // UI Binding
        setupSyncObservers()

        // Click Listeners
        binding.crdViewDeliveries.setOnClickListener {
            startActivity(Intent(context, DeliveriesList::class.java).putExtra("position", 1))
        }

        binding.crdfailedDeliveries.setOnClickListener {
            startActivity(Intent(context, FailedDeliveriesList::class.java).putExtra("position", 1))


        }

        binding.crdSuccessfulDeliveries.setOnClickListener {
            startActivity(
                Intent(context, SuccessfulDeliveriesList::class.java).putExtra(
                    "position",
                    1
                )
            )
        }

        // Network Monitor
        NetworkMonitor(requireContext()).observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                binding.syncArea.visibility = View.GONE
            }else{
                binding.syncArea.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSyncObservers() {
        val homeActivity = activity as SkynetHome
        lifecycleScope.launch {
            SharedState.percentage.collect { percentage ->
                binding.progressBar.progress = percentage
                binding.progressIndicatorText.text =
                    getString(
                        R.string.mark_total,
                        SharedState.syncedWaybills.value.toString(),
                        SharedState.awaitingSync.value.toString()
                    )

                when (percentage) {
                    0 -> {
                        if (SharedState.awaitingSync.value == 0) {
                            binding.syncArea.visibility = View.GONE
                        } else {
                            updateProgressBar(0)
                            SharedState.updateProgress(
                                SharedState.syncedWaybills.value,
                                SharedState.awaitingSync.value
                            )
                            binding.syncArea.visibility = View.VISIBLE
                            binding.progressTitle.text = getString(R.string.sync_needed)
                        }
                    }

                    100 -> {
                        lifecycleScope.launch {
                            // Show "sync complete"
                            withContext(Dispatchers.Main) {
                                binding.progressTitle.text = getString(R.string.sync_complete)
                                updateProgressBar(100)
                            }

                            delay(1500) // Still on main, OK because it's short and part of UI flow

                            // Perform data store updates off main thread
                            withContext(Dispatchers.IO) {
                                SharedState.updateSyncedWaybills(safeContext, 0)
                                SharedState.updateAwaitingSync(safeContext, 0)
                            }

                            // Update UI after reset
                            withContext(Dispatchers.Main) {
                                binding.syncArea.visibility = View.GONE
                            }
                        }
                    }

                    else -> {
                        if (SharedState.awaitingSync.value < SharedState.syncedWaybills.value) {
                            lifecycleScope.launch {
                                SharedState.updateSyncedWaybills(requireContext(), 0)
                                SharedState.updateAwaitingSync(requireContext(), 0)
                            }
                        } else {
                            updateProgressBar(percentage)
                            binding.progressTitle.text = getString(R.string.syncing)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            SharedState.syncRequired.collect {
                if (it) {
                    homeActivity.binding.syncDataNeeded.visibility = View.VISIBLE
                } else {
                    homeActivity.binding.syncDataNeeded.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshSyncProgressIfVisible()
    }

    private fun updateProgressBar(progress: Int) {
        binding.progressBar.progress = progress

        val progressDrawable = binding.progressBar.progressDrawable.mutate()
        val newColor = if (progress == 100) {
            ContextCompat.getColor(requireContext(), R.color.green) // your success color
        } else if (progress == 0) {
            ContextCompat.getColor(requireContext(), R.color.background) // your 0/0 color
        } else {
            ContextCompat.getColor(requireContext(), R.color.skynet_red) // your in-progress color
        }

        if (progressDrawable is LayerDrawable) {
            val progressBarDrawable = progressDrawable.findDrawableByLayerId(android.R.id.progress)
            progressBarDrawable?.setTint(newColor)
        }
    }

    private fun init() {
        service = ReusableFunctions.initiateSuperAppRetrofit(requireContext())

        AppCache.scanParcelList = null
        AppCache.setCurrentWaybill(null)
        AppCache.setCurrentWaybillList(null)

        if (LocationService1.isNetworkAvailable(safeContext)) {
            getStatistics()
            getDeliveryWaybills()
        } else {
            fetchWaybillsFromDatabase()
        }
    }

    private fun getFormattedDate(): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }

    private fun getStatistics() {
        loader = ReusableFunctions.showLoader(requireContext())

        service?.getDailyStatistics(
            LocalStorage.getSuperAppToken(requireContext()),
            LocalStorage.getSkynetDriverAccount().driver.identityNo
        )?.enqueue(object : Callback<DailyStatistics> {
            override fun onResponse(
                call: Call<DailyStatistics>,
                response: Response<DailyStatistics>
            ) {
                ReusableFunctions.hideLoader(loader)
                if (response.isSuccessful) {
                    response.body()?.let { stats ->
                        binding.tvSuccessfulCollections.text =
                            stats.successfulCollections.toString()
                        binding.tvFailedCollections.text = stats.failedCollections.toString()
                        binding.tvFailedDeliveries.text = stats.failedDeliveries.toString()
                        binding.tvSuccessfulDeliveries.text = stats.completedDeliveries.toString()
                        binding.tvNumDeliveries.text = stats.totalDeliveries.toString()
                        binding.tvReservedCollection.text = stats.totalCollections.toString()
                    }
                }
            }

            override fun onFailure(call: Call<DailyStatistics>, t: Throwable) {
                ReusableFunctions.hideLoader(loader)
                Log.e("Stats", "Failed: ${t.localizedMessage}")
            }
        })
    }

    private fun getDeliveryWaybills() {
        if (!LocationService1.isNetworkAvailable(requireContext())) {
            fetchWaybillsFromDatabase()
            return
        }

        service?.getDriverDeliveryWaybills(
            LocalStorage.getSuperAppToken(requireContext()),
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            getFormattedDate()
        )?.enqueue(object : Callback<List<Waybills>> {
            override fun onResponse(
                call: Call<List<Waybills>>,
                response: Response<List<Waybills>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { trip ->
                        val db = AppDatabase.getDatabase(safeContext).waybillDao()
                        CoroutineScope(Dispatchers.IO).launch {
                            db.deleteAllWaybills()
                            db.insertWaybills(trip.map { it.copy(number = it.number ?: "") })
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Waybills>>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.localizedMessage}")
            }
        })
    }

    private fun fetchWaybillsFromDatabase() {
        val db = AppDatabase.getDatabase(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val waybills = db.waybillDao().getWaybills()
                withContext(Dispatchers.Main) {
                    if (waybills.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "No waybills available offline",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.tvNumDeliveries.text = "0"
                    } else {
                        binding.tvNumDeliveries.text = waybills.size.toString()
                        binding.tvFailedDeliveries.text =
                            PendingWaybillsStorage.getFailedWaybills().size.toString()
                        binding.tvSuccessfulDeliveries.text =
                            PendingWaybillsStorage.getCompletedWaybills().size.toString()
                    }
                }
            } catch (e: Exception) {
                Log.e("Database Error", "Failed to fetch from DB: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        instance = null
    }

    companion object {
        var instance: SkyHomeFragment? = null

        fun refreshSyncProgressIfVisible() {
            instance?.binding?.let {
                it.progressBar.progress = SharedState.percentage.value
                it.progressIndicatorText.text = SharedState.outOfText.value
            }
        }
    }
}