package co.za.kasi.fragments.bottomnav

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import co.za.kasi.R
import co.za.kasi.SkynetHome
import co.za.kasi.databinding.FragmentSkyAccountBinding
import co.za.kasi.services.LocalStorage
import co.za.kasi.utils.ReusableMethods

class SkyAccountFragment : Fragment() {

    private var _binding: FragmentSkyAccountBinding? = null
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity

    private val binding get() = _binding!!

    //private Loader loader;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSkyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = context ?: return
        safeActivity = activity ?: return
        init()
        binding.signOutbtn.setOnClickListener { view1: View? ->
            ReusableMethods.performLightSignOut(safeContext, safeActivity)
            requireActivity().finish()
        }

        val homeActivity = activity as SkynetHome
        homeActivity.binding.networkLayout.visibility = View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
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
            }
        )
    }

    private fun init() {
        val driver = LocalStorage.getSkynetDriverAccount()
        binding.txtName.text = getString(
            R.string.name_information,
            driver.userAccount.firstName,
            driver.userAccount.lastName
        )
        binding.txtEmail.text = driver.userAccount.email
        binding.txtContactNumber.text = driver.userAccount.mobileNumber

        binding.apply {
            if (driver.vehicle != null) {
                val cachedVehicle = driver.vehicle

                vehicleRegNum.text = cachedVehicle.registration_number
                vehicleMake.text = cachedVehicle.make
                vehicleModel.text = cachedVehicle.model
                vehicleVinNum.text = cachedVehicle.vin_number
            }
        }

        binding.appVersion.text = safeContext
            .packageManager.getPackageInfo(
                safeContext.packageName,
                0
            ).versionName ?: "1.0.0"
    }
}