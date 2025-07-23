package co.za.kasi.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import co.za.kasi.R
import co.za.kasi.databinding.FragmentSkyNetOtpVerificationBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.UserAccountDTO
import co.za.kasi.model.VerifyOTPBody
import co.za.kasi.model.VerifyOTPResponse
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.utils.ReusableFunctions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SkyNetOtpVerificationFragment : Fragment() {
    private lateinit var binding: FragmentSkyNetOtpVerificationBinding
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private var loader: Loader? = null
    private var service: SuperAppHttpService? = null
    private var snackBar: Snackbar? = null
    var intentFallBack: Intent? = null
    private lateinit var userAccountId: String
    private lateinit var message: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkyNetOtpVerificationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = context ?: return
        safeActivity = activity ?: return
        service = ReusableFunctions.initiateSuperAppRetrofit(safeContext)
        userAccountId = arguments?.getString(getString(R.string.useraccountid)) ?: ""
        message = arguments?.getString(getString(R.string.message)) ?: ""

        binding.responseMessageText.text = message

        binding.verifyOTP.setOnClickListener {
            verifyOTP()
        }

        binding.resendOTPText.setOnClickListener {
            resendOTP()
        }

        otpBlockSetUp()

    }

    private fun highlightBox(text: TextInputEditText) {
        text.background = getDrawable(safeContext, R.drawable.otp_block_error)
        text.startAnimation(ReusableFunctions.shakeError())
    }
    private lateinit var otpBlocks: List<TextInputEditText>

    private fun getOTPString(): String {
        var otpString = ""
        otpString += binding.otpBlock1.text.toString()
        otpString += binding.otpBlock2.text.toString()
        otpString += binding.otpBlock3.text.toString()
        otpString += binding.otpBlock4.text.toString()
        otpString += binding.otpBlock5.text.toString()
        otpString += binding.otpBlock6.text.toString()
        return otpString
    }

    private fun verifyOTP() {

        if (getOTPString().length != 6) {
            highlightBox(binding.otpBlock1)
            highlightBox(binding.otpBlock2)
            highlightBox(binding.otpBlock3)
            highlightBox(binding.otpBlock4)
            highlightBox(binding.otpBlock5)
            highlightBox(binding.otpBlock6)
            return
        }
        loader = ReusableFunctions.showLoader(safeContext)

        val verifyOtpCall = service?.verifyOTP(
            VerifyOTPBody(
                userAccountId = userAccountId,
                otp = getOTPString()
            )
        )

        verifyOtpCall?.enqueue(object : Callback<VerifyOTPResponse> {
            override fun onResponse(
                call: Call<VerifyOTPResponse?>,
                response: Response<VerifyOTPResponse?>
            ) {
                if (response.isSuccessful) {
                    ReusableFunctions.hideLoader(loader)
                    val action =
                        SkyNetOtpVerificationFragmentDirections.actionSkyNetOtpVerificationFragmentToSkyNetPasswordResetFragment(
                            userAccountId = userAccountId,
                            otp = getOTPString()
                        )
                    findNavController().navigate(action)

                } else {
                    if (response.errorBody() != null) {
                        ReusableFunctions.hideLoader(loader)
                        try {
                            if (response.code() == 400) {
                                val errorResponse = ReusableFunctions.convertErrorResponse(
                                    response.errorBody()!!.string()
                                )
                                snackBar = ReusableFunctions.snackbarInstance(
                                    binding.root,
                                    errorResponse.message,
                                    Snackbar.LENGTH_INDEFINITE,
                                    getColor(safeContext, R.color.snackbar_red),
                                    getColor(safeContext, R.color.white),
                                    Gravity.BOTTOM
                                )
                                snackBar?.show()
                                ReusableFunctions.dismisSnackBar(snackBar)
                            } else {
                                intentFallBack!!.putExtra(
                                    "error",
                                    "Technical Error occurred. Please try in few minutes."
                                )
                                try {
                                    startActivity(intentFallBack!!)
                                } catch (e: Exception) {
                                    return
                                }

                                safeActivity.finish()
                            }
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<VerifyOTPResponse?>,
                t: Throwable
            ) {
                ReusableFunctions.hideLoader(loader)
                Toast.makeText(
                    safeContext, getString(R.string.failed_to_verify_otp), Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun resendOTP() {
        loader = ReusableFunctions.showLoader(safeContext)

        val resendOTPCall = service?.resendOTP(
            userAccountId
        )

        resendOTPCall?.enqueue(object : Callback<UserAccountDTO> {
            override fun onResponse(
                call: Call<UserAccountDTO?>,
                response: Response<UserAccountDTO?>
            ) {
                if (response.isSuccessful) {
                    ReusableFunctions.hideLoader(loader)
                    Toast.makeText(
                        safeContext, "OTP Resent Successfully", Toast.LENGTH_SHORT
                    ).show()

                } else {
                    if (response.errorBody() != null) {
                        ReusableFunctions.hideLoader(loader)
                        try {
                            if (response.code() == 400) {
                                val errorResponse = ReusableFunctions.convertErrorResponse(
                                    response.errorBody()!!.string()
                                )
                                snackBar = ReusableFunctions.snackbarInstance(
                                    binding.root,
                                    errorResponse.message,
                                    Snackbar.LENGTH_INDEFINITE,
                                    getColor(safeContext, R.color.snackbar_red),
                                    getColor(safeContext, R.color.white),
                                    Gravity.BOTTOM
                                )
                                snackBar?.show()
                                ReusableFunctions.dismisSnackBar(snackBar)
                            } else {
                                intentFallBack!!.putExtra(
                                    "error",
                                    "Technical Error occurred. Please try in few minutes."
                                )
                                try {
                                    startActivity(intentFallBack!!)
                                } catch (e: Exception) {
                                    return
                                }

                                safeActivity.finish()
                            }
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<UserAccountDTO?>,
                t: Throwable
            ) {
                ReusableFunctions.hideLoader(loader)
                Toast.makeText(
                    safeContext, "Failed to resend OTP", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun removeHighLightBox() {
        binding.otpBlock1.background =
            getDrawable(safeContext, R.drawable.otp_block_background_grey)
        binding.otpBlock2.background =
            getDrawable(safeContext, R.drawable.otp_block_background_grey)
        binding.otpBlock3.background =
            getDrawable(safeContext, R.drawable.otp_block_background_grey)
        binding.otpBlock4.background =
            getDrawable(safeContext, R.drawable.otp_block_background_grey)
        binding.otpBlock5.background =
            getDrawable(safeContext, R.drawable.otp_block_background_grey)
        binding.otpBlock6.background =
            getDrawable(safeContext, R.drawable.otp_block_background_grey)

    }

    private fun otpBlockSetUp() {

        otpBlocks = listOf(
            binding.otpBlock1,
            binding.otpBlock2,
            binding.otpBlock3,
            binding.otpBlock4,
            binding.otpBlock5,
            binding.otpBlock6
        )

        otpBlocks.forEachIndexed { index, editText ->
            // Move focus to next on text input
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    removeHighLightBox()
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < otpBlocks.lastIndex) {
                        otpBlocks[index + 1].requestFocus()
                    }
                }
            })

            // Handle backspace
            editText.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isNullOrEmpty() && index > 0) {
                        otpBlocks[index - 1].apply {
                            requestFocus()
                            setText("") // Optional: clear previous if needed
                        }
                    }
                }
                false
            }
        }
    }
}