package co.za.kasi.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import co.za.kasi.R
import co.za.kasi.databinding.FragmentSkyNetPasswordResetBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.NewPasswordBody
import co.za.kasi.model.NewPasswordResponse
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.utils.ReusableFunctions
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SkyNetPasswordResetFragment : Fragment() {
    private lateinit var binding: FragmentSkyNetPasswordResetBinding
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private var loader: Loader? = null
    private var service: SuperAppHttpService? = null
    private var snackBar: Snackbar? = null
    private var newPassWordGestureDetector: GestureDetector? = null
    private var confirmPasswordGestureDetector: GestureDetector? = null
    var intentFallBack: Intent? = null
    private lateinit var userAccountId: String
    private lateinit var otp: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkyNetPasswordResetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = context ?: return
        safeActivity = activity ?: return
        service = ReusableFunctions.initiateSuperAppRetrofit(safeContext)
        userAccountId = arguments?.getString(getString(R.string.useraccountid)) ?: ""
        otp = arguments?.getString(getString(R.string.otp)) ?: ""

        int()

        binding.submitNewPasswordButton.setOnClickListener {
            if (!validateInputs()) {
                highlightInputs()
                return@setOnClickListener
            }

            if (!passwordsMatch()) {
                binding.errorMessageText.visibility = View.VISIBLE
                return@setOnClickListener
            }

            changePassword()
        }
        newPassWordGestureDetector =
            GestureDetector(safeContext, object : SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    ReusableFunctions.hideORShowPassword(binding.newPasswordText, safeContext)
                    return true
                }
            })

        confirmPasswordGestureDetector =
            GestureDetector(safeContext, object : SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    ReusableFunctions.hideORShowPassword(binding.confirmPasswordText, safeContext)
                    return true
                }
            })

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    ActionConfirmationDialogFragment(
                        isIconNeeded = true,
                        isError = true,
                        title = getString(R.string.confirm_exit),
                        message = getString(R.string.you_are_almost_there_are_you_sure_you_want_to_cancel),
                        buttonPositiveText = getString(R.string.yes),
                        buttonNegativeText = getString(R.string.no),
                        onPositive = {
                            findNavController().navigate(R.id.action_skyNetPasswordResetFragment_to_skyNetSignInFragment)
                        }
                    ).show(parentFragmentManager, getString(R.string.confirm_dialog))
                }
            })

        binding.newPasswordText.addTextChangedListener(object : TextWatcher {
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

        binding.confirmPasswordText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (passwordsMatch()) {
                    removeHighLightBox()
                    binding.errorMessageText.visibility = View.INVISIBLE
                } else {
                    binding.errorMessageText.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun removeHighLightBox() {
        if (!binding.newPasswordText.text.toString().isEmpty()) {
            binding.newPasswordText.background =
                AppCompatResources.getDrawable(safeContext, R.drawable.text_background)
        }
        if (!binding.newPasswordText.text.toString().isEmpty()) {
            binding.confirmPasswordText.background =
                AppCompatResources.getDrawable(safeContext, R.drawable.text_background)
        }
    }

    private fun changePassword() {
        loader = ReusableFunctions.showLoader(safeContext)

        val changePasswordCall = service?.changePassword(
            NewPasswordBody(
                otp = otp,
                userAccountId = userAccountId,
                password = binding.newPasswordText.text.toString()
            )
        )

        changePasswordCall?.enqueue(object : Callback<NewPasswordResponse> {
            override fun onResponse(
                call: Call<NewPasswordResponse?>,
                response: Response<NewPasswordResponse?>
            ) {
                if (response.isSuccessful) {
                    ReusableFunctions.hideLoader(loader)
                    ActionConfirmationDialogFragment(
                        isIconNeeded = true,
                        isError = false,
                        hasASingleButton = true,
                        isCancelable = false,
                        buttonConfirmText = getString(R.string.proceed),
                        title = getString(R.string.password_reset_success),
                        message = getString(R.string.you_have_successfully_created_a_new_password_you_can_use_the_new_password_to_log_in_to_your_account),
                    ).show(parentFragmentManager, getString(R.string.acknowledge_dialog))
                    findNavController().navigate(R.id.action_skyNetPasswordResetFragment_to_skyNetSignInFragment)
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
                                    Log.e("TAG_error", "onFailure: " + e.message, e)
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
                call: Call<NewPasswordResponse?>,
                t: Throwable
            ) {
                ReusableFunctions.hideLoader(loader)
                Toast.makeText(
                    safeContext,
                    getString(R.string.failed_to_create_new_password), Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun passwordsMatch(): Boolean =
        binding.newPasswordText.text.toString() == binding.confirmPasswordText.text.toString()

    private fun highlightInputs() {
        if (TextUtils.isEmpty(binding.newPasswordText.getText().toString())) {
            binding.newPasswordText.background =
                getDrawable(safeContext, R.drawable.text_background_error)
            binding.newPasswordText.startAnimation(ReusableFunctions.shakeError())
            binding.newPasswordText.error = "Password required"
        }
        if (TextUtils.isEmpty(binding.confirmPasswordText.getText().toString())) {
            binding.confirmPasswordText.background =
                getDrawable(safeContext, R.drawable.text_background_error)
            binding.confirmPasswordText.startAnimation(ReusableFunctions.shakeError())
            binding.confirmPasswordText.error = "Confirm password"
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun int() {
        binding.newPasswordText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            getDrawable(safeContext, R.drawable.key_blue_password_icon_login),
            null,
            getDrawable(safeContext, R.drawable.baseline_visibility_24),
            null
        )

        binding.newPasswordText.setOnTouchListener(newPasswordDrawableRightClickListener)

        binding.confirmPasswordText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            getDrawable(safeContext, R.drawable.key_blue_password_icon_login),
            null,
            getDrawable(safeContext, R.drawable.baseline_visibility_24),
            null
        )

        binding.confirmPasswordText.setOnTouchListener(confirmPasswordDrawableRightClickListener)
    }

    @SuppressLint("ClickableViewAccessibility")
    private val newPasswordDrawableRightClickListener = OnTouchListener { v, event ->
        val rightDrawableWidth = binding.newPasswordText.compoundDrawablesRelative[2].bounds.width()
        if (event.rawX >= binding.newPasswordText.right - rightDrawableWidth) {
            newPassWordGestureDetector!!.onTouchEvent(event)
        } else false
    }

    @SuppressLint("ClickableViewAccessibility")
    private val confirmPasswordDrawableRightClickListener = OnTouchListener { v, event ->
        val rightDrawableWidth =
            binding.confirmPasswordText.compoundDrawablesRelative[2].bounds.width()
        if (event.rawX >= binding.confirmPasswordText.right - rightDrawableWidth) {
            confirmPasswordGestureDetector!!.onTouchEvent(event)
        } else false
    }

    private fun validateInputs(): Boolean {
        if (TextUtils.isEmpty(binding.newPasswordText.text.toString())) {
            return false
        }
        return !TextUtils.isEmpty(binding.confirmPasswordText.text.toString())
    }

}