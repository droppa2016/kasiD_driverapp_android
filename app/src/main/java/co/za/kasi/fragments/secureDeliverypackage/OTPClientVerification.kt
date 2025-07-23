package co.za.kasi.fragments.secureDeliverypackage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import co.za.kasi.DeliveriesList
import co.za.kasi.R
import co.za.kasi.SkynetHome
import co.za.kasi.databinding.FragmentOTPClientVerificationBinding
import co.za.kasi.db.AppDatabase
import co.za.kasi.db.sharedPreferance.PendingWaybillsStorage
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.superApp.a.waybillData.Coordinate
import co.za.kasi.model.superApp.a.waybillData.WaybillRequest
import co.za.kasi.model.superApp.a.waybillData.Waybills
import co.za.kasi.services.AppCache
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.CoordinateHelper
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.ReusableFunctions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class OTPClientVerification : Fragment() {

    private var _binding: FragmentOTPClientVerificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private var layoutSource: String? = null
    private var attempt: Int = 1
    private lateinit var location: CoordinateHelper
    private lateinit var coordinate: Coordinate
    private lateinit var service: SuperAppHttpService
    private var loader: Loader? = null

    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOTPClientVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        layoutSource = arguments?.getString("fragmentSource")
        safeContext = context ?: return
        safeActivity = activity ?: return

        Log.d("", "********Current waybill Data : ${AppCache.getCurrentWaybill()}")

        binding.topAuthLayout.unauthorizedHeading.text = "One Time Pin"
        binding.topAuthLayout.unauthorizedSubText.text = "Enter otp from the consignee"

        otpBlockSetUp()

        binding.btnVerifyAccount.setOnClickListener {

            val otp = binding.otpBlock1.getText().toString()
                .trim { it <= ' ' } + binding.otpBlock2.getText().toString()
                .trim { it <= ' ' } + binding.otpBlock3.getText().toString()
                .trim { it <= ' ' } + binding.otpBlock4.getText().toString().trim { it <= ' ' } +
                    binding.otpBlock5.getText().toString().trim { it <= ' ' } +
                    binding.otpBlock6.getText().toString().trim { it <= ' ' }

            val otpCondition = AppCache.getCurrentWaybillList()[0]
                .deliveryConditions
                ?.find { it.code == "OTP" }

            val otpValue = otpCondition?.value?.replace("-", "")

            Log.e("", "==============OTP VALUE == $otpValue")
            Log.e("", "============== given OTP  == $otp")

            if (otp.length != 6) {
                highlightBox(binding.otpBlock1)
                highlightBox(binding.otpBlock2)
                highlightBox(binding.otpBlock3)
                highlightBox(binding.otpBlock4)
                highlightBox(binding.otpBlock5)
                highlightBox(binding.otpBlock6)

            } else {

                if (otp == otpValue) {
                    sendResultBack(true, otp)
                    attempt = 1
                } else {
                    if (attempt != 3) {
                        attempt += 1
                    } else {
                        Toast.makeText(
                            context,
                            "You have exceeded all your OTP attempts",
                            Toast.LENGTH_SHORT
                        ).show()
                        submitFailedWaybill()
                        return@setOnClickListener
                    }
                    Toast.makeText(
                        context,
                        "Invalid OTP. Attempt no. - $attempt",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.resendOtpLayout.setOnClickListener {
            resendOtp()
        }

    }

    private fun init() {

        location = CoordinateHelper(requireContext())

        location.getCurrentCoordinate { coordinate ->
            if (coordinate != null) {
                Log.d("Coordinate", "Lat: ${coordinate.latitude}, Lng: ${coordinate.longitude}")
                this.coordinate = Coordinate(coordinate.latitude, coordinate.longitude)
            } else {
                Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        service = ReusableFunctions.initiateSuperAppRetrofit(requireContext())
    }

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun resendOtp() {

        val call = service.resendOtp(
            LocalStorage.getSuperAppToken(requireContext()),
            AppCache.getCurrentWaybillList()[0].number,
            getFormattedDate()
        )

        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Log.e("", "============= OTP RESEND code = ${response.code()}")
                Log.e("", "============= OTP RESEND message = ${response.message()}")

                if (response.code() == 200) {
                    Toast.makeText(requireContext(), "OTP Sent", Toast.LENGTH_SHORT).show()
                } else {

                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(safeActivity)
                        return
                    }

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    snackbar = ReusableFunctions.snackbarInstance(
                        binding.btnVerifyAccount,
                        errorResponse.message,
                        Snackbar.LENGTH_SHORT,
                        getColor(requireContext(), R.color.snackbar_red),
                        getColor(requireContext(), R.color.white),
                        Gravity.BOTTOM
                    )

                    snackbar?.show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun highlightBox(text: TextInputEditText) {
        text.background = context?.let { getDrawable(it, R.drawable.otp_block_error) }
        text.startAnimation(ReusableFunctions.shakeError())
    }

    private fun removehighlightBox() {
        //errorMessage.setVisibility(View.GONE)
        binding.otpBlock1.setBackground(context?.let {
            getDrawable(
                it,
                R.drawable.otp_block_background
            )
        })
        binding.otpBlock2.setBackground(context?.let {
            getDrawable(
                it,
                R.drawable.otp_block_background
            )
        })
        binding.otpBlock3.setBackground(context?.let {
            getDrawable(
                it,
                R.drawable.otp_block_background
            )
        })
        binding.otpBlock4.setBackground(context?.let {
            getDrawable(
                it,
                R.drawable.otp_block_background
            )
        })
        binding.otpBlock5.setBackground(context?.let {
            getDrawable(
                it,
                R.drawable.otp_block_background
            )
        })
        binding.otpBlock6.setBackground(context?.let {
            getDrawable(
                it,
                R.drawable.otp_block_background
            )
        })
    }

    private fun otpBlockSetUp() {
        binding.otpBlock1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                removehighlightBox()
                if (count == 1) {
                    binding.otpBlock2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.otpBlock2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                removehighlightBox()
                if (count == 1) {
                    binding.otpBlock3.requestFocus()
                }
                binding.otpBlock2.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (count == 0 && keyCode == KeyEvent.KEYCODE_DEL) {
                        binding.otpBlock1.requestFocus()
                    } else if (count == 1 && keyCode != KeyEvent.KEYCODE_DEL) {
                        binding.otpBlock2.requestFocus()
                    }
                    false
                })
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.otpBlock3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                removehighlightBox()
                if (count == 1) {
                    binding.otpBlock4.requestFocus()
                }
                binding.otpBlock3.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (count == 0 && keyCode == KeyEvent.KEYCODE_DEL) {
                        binding.otpBlock2.requestFocus()
                    } else if (count == 1 && keyCode != KeyEvent.KEYCODE_DEL) {
                        binding.otpBlock3.requestFocus()
                    }
                    false
                })
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.otpBlock4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                removehighlightBox()
                if (count == 1) {
                    binding.otpBlock5.requestFocus()
                }
                binding.otpBlock4.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (count == 0 && keyCode == KeyEvent.KEYCODE_DEL) {
                        binding.otpBlock3.requestFocus()
                    } else if (count == 1 && keyCode != KeyEvent.KEYCODE_DEL) {
                        binding.otpBlock4.requestFocus()
                    }
                    false
                })
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.otpBlock5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                removehighlightBox()
                if (count == 1) {
                    binding.otpBlock6.requestFocus()
                }
                binding.otpBlock5.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (count == 0 && keyCode == KeyEvent.KEYCODE_DEL) {
                        binding.otpBlock4.requestFocus()
                    } else if (count == 1 && keyCode != KeyEvent.KEYCODE_DEL) {
                        binding.otpBlock5.requestFocus()
                    }
                    false
                })
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.otpBlock6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                removehighlightBox()
                binding.otpBlock6.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (count == 0 && keyCode == KeyEvent.KEYCODE_DEL) {
                        binding.otpBlock5.requestFocus()
                    }
                    false
                })
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun sendResultBack(result: Boolean, value: String) {
        val resultBundle = Bundle().apply {
            putString("layoutSource", layoutSource)
            putBoolean("result", result)
        }

        if (result && layoutSource != null) {
            // this might be one of my best ideas
            AppCache.deliveryConditions.find { it.code == layoutSource }?.let { condition ->
                condition.value = value
            }
        }

        layoutSource?.let {
            parentFragmentManager.setFragmentResult(it, resultBundle)
        }
        parentFragmentManager.popBackStack()
    }

    private fun submitFailedWaybill() {

        loader = ReusableFunctions.showLoader(requireContext())
        var waybill = AppCache.getCurrentWaybillList()[0]


        val body = WaybillRequest(
            LocalStorage.getSkynetDriverAccount().driver.identityNo,
            waybill?.number.toString(),
            "DELIVERY_FAILURE",
            "Wrong OTP Provided",
            emptyList(),
            emptyList(),
            coordinate, "", LocalStorage.getOrCacheAndroidId(requireContext()),""
        )

        if (!LocationService1.isNetworkAvailable(requireContext())) {
            PendingWaybillsStorage.saveWaybill(body)
            PendingWaybillsStorage.saveFailedWaybill(AppCache.getCurrentWaybillList()[0])

            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(requireContext())
                    .waybillDao()
                    .deleteWaybillByNumber(body.waybillNumber)
            }

            Toast.makeText(
                requireContext(),
                "No Internet. Delivery failure saved offline.",
                Toast.LENGTH_SHORT
            )
                .show()
            startActivity(Intent(requireContext(), DeliveriesList::class.java))
            return
        }

        val submitWaybillCall =
            service.submitWaybill(LocalStorage.getSuperAppToken(requireContext()), body)

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonBody = gson.toJson(body)

        Log.e("", "================= submitWaybillCall== ${submitWaybillCall.toString()}")

        submitWaybillCall.enqueue(object : Callback<Waybills> {
            override fun onResponse(call: Call<Waybills>, response: Response<Waybills>) {


                ReusableFunctions.hideLoader(loader)
                Log.e("", "================= waybill submit == ${response.code()}")

                if (response.code() == 200) {
                    Log.e("", "================= waybill submit == ${response.code()}")

                    startActivity(Intent(requireContext(), SkynetHome::class.java))

                } else {
                    if (response.code() == 403) {
                        ReusableFunctions.handleBlockedUsers(requireActivity())
                        return
                    }

                    val errorResponse = ReusableFunctions.convertErrorResponse(
                        response.errorBody()!!.string()
                    )

                    snackbar = ReusableFunctions.snackbarInstance(
                        binding?.root, errorResponse.message, Snackbar.LENGTH_INDEFINITE,
                        ContextCompat.getColor(requireContext(), R.color.snackbar_red),
                        ContextCompat.getColor(requireContext(), R.color.white),
                        Gravity.TOP
                    )
                    snackbar?.show()
                }

            }

            override fun onFailure(call: Call<Waybills>, t: Throwable) {

            }
        })
    }
}