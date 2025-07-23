package co.za.kasi.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import co.za.kasi.R
import co.za.kasi.databinding.FragmentSkyNetForgotPasswordBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.PasswordResetBody
import co.za.kasi.model.PasswordResetDTO
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.utils.ReusableFunctions
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SkyNetForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentSkyNetForgotPasswordBinding
    private lateinit var safeContext: Context
    private var loader: Loader? = null
    private var service: SuperAppHttpService? = null
    private lateinit var safeActivity: Activity
    private var snackBar: Snackbar? = null
    var intentFallBack: Intent? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkyNetForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = context ?: return
        safeActivity = activity ?: return
        service = ReusableFunctions.initiateSuperAppRetrofit(safeContext)

        binding.getOTPButton.setOnClickListener {
            if (binding.idNumberEditText.text.toString().isEmpty()) {
                highlightInputs()
                return@setOnClickListener
            }
            resetPassword()
        }

        binding.idNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeHighLightBox()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun resetPassword() {
        loader = ReusableFunctions.showLoader(safeContext)

        val resetPasswordCall = service?.resetPassword(
            PasswordResetBody(
                binding.idNumberEditText.text.toString()
            )
        )

        resetPasswordCall?.enqueue(object : Callback<PasswordResetDTO> {
            override fun onResponse(
                call: Call<PasswordResetDTO?>,
                response: Response<PasswordResetDTO?>
            ) {
                if (response.isSuccessful) {
                    ReusableFunctions.hideLoader(loader)
                    val action =
                        SkyNetForgotPasswordFragmentDirections.actionSkyNetForgotPasswordFragmentToSkyNetOtpVerificationFragment(
                            userAccountId = response.body()?.userAccount?.id.toString(),
                            message = response.body()?.message.toString()
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
                call: Call<PasswordResetDTO?>,
                t: Throwable
            ) {
                ReusableFunctions.hideLoader(loader)
                Toast.makeText(
                    safeContext, "Failed to send", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun highlightInputs() {
        if (TextUtils.isEmpty(binding.idNumberEditText.getText().toString())) {
            binding.idNumberEditText.background =
                getDrawable(safeContext, R.drawable.text_background_error)
            binding.idNumberEditText.startAnimation(ReusableFunctions.shakeError())
            binding.idNumberEditText.error = "Id Number required"
        }
    }

    private fun removeHighLightBox() {
        binding.idNumberEditText.background =
            AppCompatResources.getDrawable(safeContext, R.drawable.text_background)
    }
}