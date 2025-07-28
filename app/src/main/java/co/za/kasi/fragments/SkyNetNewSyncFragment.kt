package co.za.kasi.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.za.kasi.R
import co.za.kasi.SkynetHome
import co.za.kasi.SkynetHome.Companion.launchHomeActivity
import co.za.kasi.VehicleRegistrationSelection.Companion.launchVehicleRegistrationSelection
import co.za.kasi.adapters.ManifestAdapter
import co.za.kasi.databinding.FragmentSkyNetNewSyncBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.fragments.bottomnav.SkyHomeFragment
import co.za.kasi.model.ManifestDTO
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SyncDataService
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.utils.SharedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SkyNetNewSyncFragment : Fragment() {
    private lateinit var binding: FragmentSkyNetNewSyncBinding
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private var tripList: List<ManifestDTO> = emptyList<ManifestDTO>()
    private lateinit var adapter: ManifestAdapter
    private lateinit var service: SyncDataService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkyNetNewSyncBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeActivity = activity ?: return
        safeContext = context ?: return

        service = ReusableFunctions.initiateSyncDataRetrofit(safeContext)
        adapter = ManifestAdapter(tripList)

        val syncType = arguments?.getString("sync_type")

        setUpInitialState(syncType.toString())
        binding.btnPositive.setOnClickListener {
            syncData()
        }
        binding.btnNegative.setOnClickListener {
            if (syncType == "LOGGED_IN") {
                handleSyncCompleteLoggedIn()
            }
            if (syncType == "FIRST_LOGIN") {
                launchHomeActivity(safeContext)
            }
        }

        binding.btnRefresh.setOnClickListener {
            val homeActivity = activity as SkynetHome
            homeActivity.binding.networkLayout.visibility = View.GONE
            syncData()
        }

        binding.btnRetry.setOnClickListener {
            syncData()
        }
        binding.manifestsList.apply {
            layoutManager = LinearLayoutManager(safeContext)
            adapter = this@SkyNetNewSyncFragment.adapter
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackNavigation(syncType.toString())
                }
            }
        )
    }

    fun setUpInitialState(syncType: String) {
        if (syncType == "LOGGED_IN") {
            binding.syncLoadingState.visibility = View.GONE
            binding.syncCompleteState.visibility = View.GONE
            binding.syncFailedState.visibility = View.GONE
            binding.awaitingSyncState.visibility = View.VISIBLE

            (activity as SkynetHome).binding.networkLayout.visibility = View.VISIBLE
        }

        if (syncType == "FIRST_LOGIN") {
            binding.syncLoadingState.visibility = View.VISIBLE
            binding.syncCompleteState.visibility = View.GONE
            binding.syncFailedState.visibility = View.GONE
            binding.awaitingSyncState.visibility = View.GONE
            lifecycleScope.launch {
                delay(10_000)
                syncData()
            }
        }
    }

    companion object {
        fun newInstance(syncType: String): SkyNetNewSyncFragment {
            val fragment = SkyNetNewSyncFragment()
            val args = Bundle()
            args.putString("sync_type", syncType)
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun syncData() {
        binding.syncLoadingState.visibility = View.VISIBLE
        binding.syncCompleteState.visibility = View.GONE
        binding.syncFailedState.visibility = View.GONE
        binding.awaitingSyncState.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val token = LocalStorage.getSuperAppToken(safeContext)
                val driverId = LocalStorage.getSkynetDriverAccount().driver.identityNo
                val date = getTodayDate()

                // STEP 1: Trip Summary
                val tripSummaryRes = service.getTripSummary(token, driverId, date)
                if (!tripSummaryRes.isSuccessful) {
                    showSyncFailed("Trip Summary failed")
                    return@launch
                } else {
                    tripSummaryRes.body()?.let { trip ->
                        adapter.setItems(trip)
                    }
                }

                // STEP 2: Waybills
                val waybillsRes = service.getDriverDeliveryWaybills(token, driverId, date)
                if (!waybillsRes.isSuccessful) {
                    showSyncFailed("Waybills fetch failed")
                    return@launch
                } else {
                    waybillsRes.body()?.let { trip ->
                        val db = AppDatabase.getDatabase(safeContext).waybillDao()
                        CoroutineScope(Dispatchers.IO).launch {
                            db.deleteAllWaybills()
                            db.insertWaybills(trip.map { it.copy(number = it.number) })
                        }
                    }
                }

                // STEP 3: Statistics
                val statsRes = service.getWaybillStats(token, driverId)
                if (!statsRes.isSuccessful) {
                    showSyncFailed("Statistics fetch failed")
                    return@launch
                }

                setDriverSynced()
                binding.syncLoadingState.visibility = View.GONE
                binding.syncCompleteState.visibility = View.VISIBLE
                binding.syncFailedState.visibility = View.GONE
                if (tripSummaryRes.body()?.isEmpty() == true) {
                    binding.manifestsList.visibility = View.GONE
                    binding.descriptionText.visibility = View.GONE
                    binding.noManifestText.visibility = View.VISIBLE
                } else {
                    val numberOfManifests = tripSummaryRes.body()?.size ?: 0
                    val numberOfParcels = tripSummaryRes.body()?.sumOf { it.totalParcels} ?: 0

                    binding.numberOfManifests.text =
                        getString(R.string.number_of_manifests, numberOfManifests.toString())
                    binding.numberOfParcels.text   =
                        getString(R.string.number_of_parcels, numberOfParcels.toString())

                    binding.manifestsList.visibility = View.VISIBLE
                    binding.descriptionText.visibility = View.VISIBLE
                    binding.noManifestText.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showSyncFailed("Sync failed: ${e.localizedMessage}")
            }
        }
    }

    private fun showSyncFailed(reason: String) {
        binding.syncLoadingState.visibility = View.GONE
        binding.syncCompleteState.visibility = View.GONE
        binding.syncFailedState.visibility = View.VISIBLE
        Toast.makeText(safeContext, reason, Toast.LENGTH_SHORT).show()
    }

    fun getTodayDate(): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return LocalDate.now().format(formatter)
    }

    fun handleSyncCompleteLoggedIn() {
        val homeActivity = activity as SkynetHome
        homeActivity.replaceFragment(SkyHomeFragment())
        homeActivity.resetTabs()
        homeActivity.binding.homeTabIcon.setImageDrawable(
            AppCompatResources.getDrawable(
                safeContext,
                R.drawable.home_icon_red
            )
        )
        homeActivity.binding.homeTabText.setTextColor(
            getColor(
                safeContext,
                R.color.skynet_color
            )
        )

        homeActivity.binding.syncDataText.setTextColor(
            getColor(
                safeContext,
                R.color.black
            )
        )
        homeActivity.binding.syncDataIcon.setImageDrawable(
            AppCompatResources.getDrawable(
                safeContext,
                R.drawable.refresh_icon
            )
        )
    }

    private fun setDriverSynced() {
        lifecycleScope.launch {
            try {
                val response = service.setDriverSynced(
                    LocalStorage.getSuperAppToken(safeContext),
                    LocalStorage.getSkynetDriverAccount().driver.identityNo
                )

                if (response.isSuccessful) {
                    SharedState.updateSyncRequired(safeContext, false)
                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return@launch
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleBackNavigation(syncType: String) {
        val homeActivity = activity as SkynetHome
        if (syncType == "LOGGED_IN") {
            homeActivity.replaceFragment(SkyHomeFragment())
            homeActivity.resetTabs()
            homeActivity.binding.homeTabIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    safeContext,
                    R.drawable.home_icon_red
                )
            )
            homeActivity.binding.homeTabText.setTextColor(
                getColor(
                    safeContext,
                    R.color.skynet_color
                )
            )

            homeActivity.binding.syncDataText.setTextColor(
                getColor(
                    safeContext,
                    R.color.black
                )
            )
            homeActivity.binding.syncDataIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    safeContext,
                    R.drawable.refresh_icon
                )
            )
        }
        if (syncType == "FIRST_LOGIN") {
            launchVehicleRegistrationSelection(safeContext)
        }
    }

}