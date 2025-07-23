package co.za.kasi

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import co.za.kasi.databinding.ActivitySigninBinding
import co.za.kasi.dialogs.Loader
import co.za.kasi.model.accountDTO.AutoLoginDTO
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriverAppLoginBody
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriverAppLoginBodyResponse
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.SuperAppHttpService
import co.za.kasi.services.location.LocationService1
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SignIn : AppCompatActivity() {
    private var forgotPasswordText: MaterialTextView? = null
    private var signUp: MaterialTextView? = null
    private var signUpLayout: LinearLayout? = null
    var email: TextInputEditText? = null
    var password: TextInputEditText? = null
    private var btnSignIn: AppCompatButton? = null
    private var loader: Loader? = null
    private var snackbar: Snackbar? = null
    private var gestureDetector: GestureDetector? = null
    private var services: SuperAppHttpService? = null
    private var autoLoginDTO: AutoLoginDTO? = null
    var intentFallBack: Intent? = null
    private val binding: ActivitySigninBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        init()

        if (LocalStorage.isLoginToday()) {

            if (LocalStorage.getSkynetDriverAccount() != null) {
                val serviceIntent = Intent(applicationContext, LocationService1::class.java)
                serviceIntent.setAction(LocationService1.ACTION_START)
                startService(serviceIntent)
            }
            if (LocalStorage.getSkynetDriverAccount().vehicle == null) {
                startActivity(Intent(this@SignIn, VehicleRegistrationSelection::class.java))
            } else {
                Log.e(
                    "", "===================121response body ==" + LocalStorage.getSkynetDriverAccount()!!
                        .activeDriver.toString()
                )
                startActivity(Intent(this@SignIn, SkynetHome::class.java))
            }

        }

        email!!.addTextChangedListener(
            ReusableFunctions.errorRemovingWatcher(
                email,
                applicationContext
            )
        )

        btnSignIn!!.setOnClickListener { //   startActivity(new Intent(SignIn.this, VehicleRegistrationSelection.class));
            if (validateInputs()) {
                loader = ReusableFunctions.showLoader(this@SignIn)
                gainAccess(email!!.getText().toString(), password!!.getText().toString())
            } else {
                highlightInputs()
            }
        }
        gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                ReusableFunctions.hideORShowPassword(password, applicationContext)
                return true
            }
        })
    }

    private fun highlightInputs() {
        if (TextUtils.isEmpty(email!!.getText().toString())) {
            email!!.background = getDrawable(R.drawable.text_background_error)
            email!!.startAnimation(ReusableFunctions.shakeError())
            email!!.error = "Invalid username"
        }
        if (TextUtils.isEmpty(password!!.getText().toString())) {
            password!!.background = getDrawable(R.drawable.text_background_error)
            password!!.startAnimation(ReusableFunctions.shakeError())
            password!!.error = "Invalid password"
        }
    }

    private fun init() {
        autoLoginDTO = AutoLoginDTO()
        intentFallBack = Intent(applicationContext, FallBackPage::class.java)
        intentFallBack!!.putExtra("class", "SignIn")
        email = findViewById(R.id.edtEmail)
        password = findViewById(R.id.edtPassword)
        forgotPasswordText = findViewById(R.id.forgot_passwordText)
        signUp = findViewById(R.id.tvSignUp)
        signUpLayout = findViewById(R.id.signUpLayout)
        btnSignIn = findViewById(R.id.btnSignInt)
        services = ReusableFunctions.initiateSuperAppRetrofit(applicationContext)
        password?.setCompoundDrawablesRelativeWithIntrinsicBounds(
            getDrawable(R.drawable.key_blue_password_icon_login),
            null,
            getDrawable(R.drawable.baseline_visibility_24),
            null
        )
        password?.setOnTouchListener(drawableRightClickListener)
        forgotPasswordText?.setOnClickListener(View.OnClickListener { v: View? ->
           // startActivity(Intent(applicationContext, ForgotPassword::class.java))
        })
        signUpLayout?.setOnClickListener(
            View.OnClickListener { v: View? ->
                Toast.makeText(
                    this,
                    "Visit www.skynet.co.za ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

    }

    private fun validateInputs(): Boolean {
        if (TextUtils.isEmpty(email!!.getText().toString())) {
            return false
        }
        return if (TextUtils.isEmpty(password!!.getText().toString())) {
            false
        } else true
    }

    private val drawableRightClickListener = OnTouchListener { v, event ->
        val rightDrawableWidth = password!!.compoundDrawablesRelative[2].bounds.width()
        if (event.rawX >= password!!.right - rightDrawableWidth) {
            gestureDetector!!.onTouchEvent(event)
        } else false
    }

    override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    private fun gainAccess(idNumber: String, pwd: String) {
        Log.e("", "===================clicked login ==")
        val logInDTO = SkynetDriverAppLoginBody()
        logInDTO.driverIdNumber = idNumber
        logInDTO.password = pwd
        val loginCreateCall = services!!.driverLogin(logInDTO)
        loginCreateCall.enqueue(object : Callback<SkynetDriverAppLoginBodyResponse?> {

            override fun onResponse(
                call: Call<SkynetDriverAppLoginBodyResponse?>,
                response: Response<SkynetDriverAppLoginBodyResponse?>
            ) {
                ReusableFunctions.hideLoader(loader)
                Log.e("", "===================response login ==" + response.code())

                if (response.code() == 200) {

                    Toast.makeText(this@SignIn, "successful login ", Toast.LENGTH_SHORT).show()
                    assert(response.body() != null)
                    Log.e("", "===================response body ==" + response.body().toString())
                    LocalStorage.storeSkynetDriverAccount(response.body())
                    LocalStorage.saveLoginDate()


                    if (LocalStorage.getSkynetDriverAccount() != null) {
                        val serviceIntent = Intent(applicationContext, LocationService1::class.java)
                        serviceIntent.setAction(LocationService1.ACTION_START)
                        startService(serviceIntent)
                    }
                    if (response.body()!!.activeDriver == null) {
                        startActivity(Intent(this@SignIn, VehicleRegistrationSelection::class.java))
                    } else {
                        Log.e(
                            "", "===================response body ==" + response.body()!!
                                .activeDriver.toString()
                        )
                        startActivity(Intent(this@SignIn, SkynetHome::class.java))
                    }
                } else {
                    if (response.errorBody() != null) {
                        ReusableFunctions.hideLoader(loader)
                        try {
                            if (response.code() == 400) {
                                val errorResponse = ReusableFunctions.convertErrorResponse(
                                    response.errorBody()!!.string()
                                )
                                snackbar = ReusableFunctions.snackbarInstance(
                                    btnSignIn,
                                    errorResponse.message,
                                    Snackbar.LENGTH_INDEFINITE,
                                    getColor(R.color.snackbar_red),
                                    getColor(R.color.white),
                                    Gravity.BOTTOM
                                )
                                snackbar?.show()
                                ReusableFunctions.dismisSnackBar(snackbar)
                            } else {
                                intentFallBack!!.putExtra(
                                    "error",
                                    "Technical Error occurred. Please try in few minutes."
                                )
                                startActivity(intentFallBack)
                                finish()
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
            }


        })
    }


}