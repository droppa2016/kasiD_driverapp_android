package co.za.kasi.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import co.za.kasi.FallBackPage
import co.za.kasi.R
import co.za.kasi.SkynetHome
import co.za.kasi.VehicleRegistrationSelection
import co.za.kasi.databinding.FragmentSkyNetSignInBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.accountDTO.AutoLoginDTO
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriverAppLoginBody
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriverAppLoginBodyResponse
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.ReusableFunctions
import com.google.android.material.snackbar.Snackbar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException

class SkyNetSignInFragment : Fragment() {
    private lateinit var binding: FragmentSkyNetSignInBinding
    private lateinit var safeContext: Context
    private lateinit var safeActivity: Activity
    private var loader: Loader? = null
    private var snackBar: Snackbar? = null
    private var gestureDetector: GestureDetector? = null
    private var services: SuperAppHttpService? = null
    private var autoLoginDTO: AutoLoginDTO? = null
    var intentFallBack: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkyNetSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safeContext = context ?: return
        safeActivity = activity ?: return

        checkPermission()

        init()

        if (LocalStorage.isLoginToday()) {

            if (LocalStorage.getSkynetDriverAccount() != null) {
                val serviceIntent = Intent(safeContext, LocationService1::class.java)
                serviceIntent.setAction(LocationService1.ACTION_START)
                safeContext.startService(serviceIntent)
            }
            if (LocalStorage.getSkynetDriverAccount().vehicle == null) {
                startActivity(Intent(safeContext, VehicleRegistrationSelection::class.java))
            } else {
                Log.e(
                    "",
                    "===================121response body ==" + LocalStorage.getSkynetDriverAccount()!!
                        .activeDriver.toString()
                )
                startActivity(Intent(safeContext, SkynetHome::class.java))
            }
        }

        binding.emailText.addTextChangedListener(
            ReusableFunctions.errorRemovingWatcher(
                binding.emailText,
                safeContext
            )
        )

        binding.signInButton.setOnClickListener {
            if (validateInputs()) {
                loader = ReusableFunctions.showLoader(safeContext)

                if (LocationService1.isNetworkAvailable(safeContext)) {

                    gainAccess(
                        binding.emailText.getText().toString(),
                        binding.passwordText.getText().toString()
                    )

                } else {

                    validateOfflineLogin(
                        binding.emailText.getText().toString(),
                        binding.passwordText.getText().toString()
                    )
                }

            } else {
                highlightInputs()
            }
        }

        gestureDetector = GestureDetector(safeContext, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                ReusableFunctions.hideORShowPassword(binding.passwordText, safeContext)
                return true
            }
        })

        binding.emailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeHighLightBox()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.passwordText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                removeHighLightBox()
            }

            override fun afterTextChanged(s: Editable?) {}
        })



        binding.forgotPasswordButton.setOnClickListener {
            findNavController().navigate(R.id.action_skyNetSignInFragment_to_skyNetForgotPasswordFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishAffinity(safeActivity)
                }
            }
        )
    }

    private fun highlightInputs() {
        if (TextUtils.isEmpty(binding.emailText.getText().toString())) {
            binding.emailText.background =
                getDrawable(safeContext, R.drawable.text_background_error)
            binding.emailText.startAnimation(ReusableFunctions.shakeError())
            binding.emailText.error = "No username entered"
        }
        if (TextUtils.isEmpty(binding.passwordText.getText().toString())) {
            binding.passwordText.background =
                getDrawable(safeContext, R.drawable.text_background_error)
            binding.passwordText.startAnimation(ReusableFunctions.shakeError())
            binding.passwordText.error = getString(R.string.no_password_entered)
        }
    }

    private fun validateInputs(): Boolean {
        if (TextUtils.isEmpty(binding.emailText.text.toString())) {
            return false
        }
        return !TextUtils.isEmpty(binding.passwordText.text.toString())
    }

    private fun validateOfflineLogin(username: String, password: String) {
        ReusableFunctions.hideLoader(loader)

        if (LocalStorage.getSkynetDriverAccount() == null || LocalStorage.getSelectedVehicle() == null) {
            Toast.makeText(
                safeContext,
                "Unable to validate offline login, please connect to a stable network",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (LocalStorage.getCachedUsername() == username && LocalStorage.getCachedPassword() == password) {
            LocalStorage.saveLoginDate()
            startActivity(Intent(safeContext, SkynetHome::class.java))
            Toast.makeText(safeContext, "successful login ", Toast.LENGTH_SHORT).show()
        } else {
            snackBar = ReusableFunctions.snackbarInstance(
                binding.root,
                "Invalid Credentials",
                Snackbar.LENGTH_SHORT,
                getColor(safeContext, R.color.snackbar_red),
                getColor(safeContext, R.color.white),
                Gravity.BOTTOM
            )
            snackBar?.show()
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                safeContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                safeActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

    private fun gainAccess(idNumber: String, pwd: String) {


        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://clocking-api.droppa.co.za/kasid/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val api = retrofit.create(SuperAppHttpService::class.java)


        val logInDTO = SkynetDriverAppLoginBody()
        logInDTO.driverIdNumber = idNumber
        logInDTO.password = pwd
        val loginCreateCall = api.driverLogin(logInDTO)
        Log.e("", "===================call login ==$loginCreateCall")
        loginCreateCall.enqueue(object : Callback<SkynetDriverAppLoginBodyResponse?> {



            override fun onResponse(
                call: Call<SkynetDriverAppLoginBodyResponse?>,
                response: Response<SkynetDriverAppLoginBodyResponse?>
            ) {
                ReusableFunctions.hideLoader(loader)
                Log.e("", "===================response login ==" + response.code())
                Log.e("", "===================response login ==" + response.message())

                if (response.code() == 200) {

                    if (response.body()?.driver?.hasLoggedInBefore != true) {
                        ActionConfirmationDialogFragment(
                            isIconNeeded = true,
                            isError = true,
                            hasASingleButton = true,
                            isCancelable = false,
                            buttonConfirmText = getString(R.string.reset_password),
                            title = getString(R.string.first_login),
                            message = getString(R.string.you_are_logging_in_for_the_first_time_please_reset_your_password_to_continue)
                        ) {
                            val action =
                                SkyNetSignInFragmentDirections.actionSkyNetSignInFragmentToSkyNetPasswordResetFragment(
                                    otp = "000000",
                                    userAccountId = response.body()?.userAccount?.id.toString(),
                                )
                            findNavController().navigate(action)
                        }.show(parentFragmentManager, "reset_password_dialog")
                        return
                    }

                    Toast.makeText(safeContext, "successful login ", Toast.LENGTH_SHORT).show()
                    assert(response.body() != null)
                    Log.e("", "===================response body ==" + response.body().toString())
                    LocalStorage.storeSkynetDriverAccount(response.body())
                    LocalStorage.saveLoginDate()
                    LocalStorage.storeAppVersionCode(response.body()!!.driver.appCode)
                    LocalStorage.storeCredentials(idNumber, pwd)

                    if (LocalStorage.getSkynetDriverAccount() != null) {
                        val serviceIntent = Intent(safeContext, LocationService1::class.java)
                        serviceIntent.setAction(LocationService1.ACTION_START)
                        safeContext.startService(serviceIntent)
                    }

                    if (response.body()!!.activeDriver == null) {
                        startActivity(Intent(safeContext, VehicleRegistrationSelection::class.java))
                    } else {
                        Log.e(
                            "",
                            "===================response body ==" + response.body()!!.activeDriver.toString()
                        )
                        startActivity(Intent(safeContext, SkynetHome::class.java))
                    }
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
                                    Snackbar.LENGTH_SHORT,
                                    getColor(safeContext, R.color.snackbar_red),
                                    getColor(safeContext, R.color.white),
                                    Gravity.BOTTOM
                                )
                                snackBar?.show()
                            } else {

                            }
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SkynetDriverAppLoginBodyResponse?>, t: Throwable) {
                ReusableFunctions.hideLoader(loader)
                Log.e("TAG_error", "onFailure: " + t.message, t)
                //intentFallBack.putExtra("error", t.getMessage());
//                startActivity(intentFallBack);
//                finish();
            }


        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        autoLoginDTO = AutoLoginDTO()
        intentFallBack = Intent(safeContext, FallBackPage::class.java)
        intentFallBack!!.putExtra("class", "SignIn")
        services = ReusableFunctions.initiateSuperAppRetrofit(safeContext)

        binding.passwordText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            getDrawable(safeContext, R.drawable.key_blue_password_icon_login),
            null,
            getDrawable(safeContext, R.drawable.baseline_visibility_24),
            null
        )

        binding.passwordText.setOnTouchListener(drawableRightClickListener)

    }

    @SuppressLint("ClickableViewAccessibility")
    private val drawableRightClickListener = OnTouchListener { v, event ->
        val rightDrawableWidth = binding.passwordText.compoundDrawablesRelative[2].bounds.width()
        if (event.rawX >= binding.passwordText.right - rightDrawableWidth) {
            gestureDetector!!.onTouchEvent(event)
        } else false
    }

    fun removeHighLightBox() {
        if (!binding.emailText.text.toString().isEmpty()) {
            binding.emailText.background =
                getDrawable(safeContext, R.drawable.text_background)
        }
        if (!binding.passwordText.text.toString().isEmpty()) {
            binding.passwordText.background =
                getDrawable(safeContext, R.drawable.text_background)
        }
    }

}