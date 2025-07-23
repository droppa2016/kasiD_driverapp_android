package co.za.kasi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.util.UUID;

import co.za.kasi.dialogs.Loader;
import co.za.kasi.model.DriverAccountDTO;
import co.za.kasi.model.DriverPersonalDetails;
import co.za.kasi.model.ErrorResponse;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.utils.ReusableFunctions;
import co.za.kasi.utils.Validations;
import co.za.kasi.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    private AppCompatButton nextBtn;

    private TextInputEditText nameText, surnameText, emailText, phoneNumber, passwordText, rePasswordText;

    private String deviceID;

    private Loader loader;

    private GestureDetector gestureDetector,gestureDetectorReType;

    private Snackbar snackbar;

    private MaterialTextView heading, subtitle, btnSIgnIn;

    private DriverHttpService services;

    private LinearLayout lytBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        nextBtn = findViewById(R.id.btnCreateAccount);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameText.getText().toString().trim();
                String surname = surnameText.getText().toString().trim();
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                String retypePassword = rePasswordText.getText().toString().trim();
                String mobile = phoneNumber.getText().toString().trim();

                //Validate all text fields before allowing user to continue

                if (validateInputs()) {

                    DriverPersonalDetails user = new DriverPersonalDetails(deviceID, mobile, name, surname, mobile + "123", email);

                    DriverAccountDTO account = new DriverAccountDTO(user, email, retypePassword.toCharArray());

                    loader = ReusableFunctions.showLoader(SignUp.this);

                    registerPersonalDetails(account);

                } else {
                    highlightInputs();
                }

                //startActivity(new Intent(SignUp.this, OTPVerification.class));

            }
        });

        gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed (MotionEvent e){
                // Handle the single tap event here
                ReusableFunctions.hideORShowPasswordSignUp(passwordText, SignUp.this);
                return true;
            }
        });

        gestureDetectorReType =  new GestureDetector(this,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed (MotionEvent e){
                // Handle the single tap event here
                ReusableFunctions.hideORShowPasswordSignUp(rePasswordText, SignUp.this);
                return true;
            }
        });

        passwordText.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.outline_lock_24),null,getDrawable(R.drawable.baseline_visibility_24),null);
        passwordText.setOnTouchListener(drawableRightClickListenerPassword);
        passwordText.append("");

        rePasswordText.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.outline_lock_24),null,getDrawable(R.drawable.baseline_visibility_24),null);
        rePasswordText.setOnTouchListener(drawableRightClickListenerReTypePassword);
        rePasswordText.append("");

        lytBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountAccessActivity.launchAccountAccessActivity(SignUp.this);
            }
        });
    }

    private void init() {

        services = ReusableFunctions.initiateRetrofit(SignUp.this);
        nameText = findViewById(R.id.nameText);
        surnameText = findViewById(R.id.edtSurnameText);
        emailText = findViewById(R.id.edtEmailText);
        phoneNumber = findViewById(R.id.edtPhoneNumberText);
        passwordText = findViewById(R.id.edtPassword);
        rePasswordText = findViewById(R.id.edtRePassword);
        nextBtn = findViewById(R.id.btnCreateAccount);
        btnSIgnIn = findViewById(R.id.btnSIgnIn);

        lytBottom = findViewById(R.id.lytBottomCard);

        deviceID = UUID.randomUUID().toString();

        heading = findViewById(R.id.unauthorized_heading);
        heading.setText("Create a Droppa Driver Account");
        subtitle = findViewById(R.id.unauthorized_subText);
        subtitle.setText("Create a free account or sign in if you have already registered.");

        // phoneNumber.addTextChangedListener(validateNumber(phoneNumber,getApplicationContext()));

        nameText.addTextChangedListener(ReusableFunctions.errorRemovingWatcher(nameText, SignUp.this));
        surnameText.addTextChangedListener(ReusableFunctions.errorRemovingWatcher(surnameText, SignUp.this));
        emailText.addTextChangedListener(ReusableFunctions.errorRemovingWatcher(emailText, SignUp.this));
        phoneNumber.addTextChangedListener(ReusableFunctions.errorRemovingWatcher(phoneNumber, SignUp.this));
        passwordText.addTextChangedListener(ReusableFunctions.errorRemovingWatcher(passwordText, SignUp.this));
        rePasswordText.addTextChangedListener(ReusableFunctions.errorRemovingWatcher(rePasswordText, SignUp.this));

    }


    private boolean validateInputs() {

        boolean check = true;

        if (!Validations.isValidEmail(emailText.getText().toString())) {
            check =  false;
        }
        if (nameText.toString().length() < 2) {
            check =  false;
        }

        if (surnameText.toString().length() < 2) {
            check =  false;
        }

        String number = phoneNumber.getText().toString();

        if (number.isEmpty()) {
            check =  false;

        }else if(number.charAt(0) != '0'){
            check =false;

        }else  if(number.length() !=10){
            check =false;
        }

        if (passwordText.getText().toString().length() < 6) {
            check =  false;
        }

        if(!rePasswordText.getText().toString().equalsIgnoreCase(passwordText.getText().toString())){
           check =  false;
        }

        return check;
    }

    private void highlightInputs() {

        if (nameText.getText().toString().length() < 2) {
            nameText.setBackground(getDrawable(R.drawable.text_background_error));
            nameText.startAnimation(ReusableFunctions.shakeError());
            nameText.setError("Invalid name" );
        }

        if (surnameText.getText().toString().length() < 2) {
            surnameText.setBackground(getDrawable(R.drawable.text_background_error));
            surnameText.startAnimation(ReusableFunctions.shakeError());
            surnameText.setError("Invalid surname" );
        }

        if (!Validations.isValidEmail(emailText.getText().toString())) {
            emailText.setBackground(getDrawable(R.drawable.text_background_error));
            emailText.startAnimation(ReusableFunctions.shakeError());
            emailText.setError("Invalid email" );
        }

        String number = phoneNumber.getText().toString().trim();


        if (TextUtils.isEmpty(phoneNumber.getText().toString())) {


            phoneNumber.setBackground(getDrawable(R.drawable.text_background_error));
            phoneNumber.startAnimation(ReusableFunctions.shakeError());
            phoneNumber.setError("Invalid phone number" );


        } else if (number.charAt(0) != '0') {

            phoneNumber.setBackground(getDrawable(R.drawable.text_background_error));
            phoneNumber.startAnimation(ReusableFunctions.shakeError());
            phoneNumber.setError("Invalid phone number" );

        } else if (number.length() != 10) {

            phoneNumber.setBackground(getDrawable(R.drawable.text_background_error));
            phoneNumber.startAnimation(ReusableFunctions.shakeError());
            phoneNumber.setError("Invalid phone number" );
        }

        if (passwordText.getText().toString().length() < 6) {
            passwordText.setBackground(getDrawable(R.drawable.text_background_error));
            passwordText.startAnimation(ReusableFunctions.shakeError());
            passwordText.setError("Password must contain minimum of 6 characters" );
        }

        if (!rePasswordText.getText().toString().equalsIgnoreCase(passwordText.getText().toString())) {
            rePasswordText.setBackground(getDrawable(R.drawable.text_background_error));
            rePasswordText.startAnimation(ReusableFunctions.shakeError());
            rePasswordText.setError("Passwords do not match." );
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    private View.OnTouchListener drawableRightClickListenerPassword = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int rightDrawableWidth = passwordText.getCompoundDrawablesRelative()[2].getBounds().width();

            if (event.getRawX() >= (passwordText.getRight()) - rightDrawableWidth) {

                passwordText.setSelection(passwordText.getText().toString().length());
                return gestureDetector.onTouchEvent(event);

            }
            return false;
        }
    };

    private View.OnTouchListener drawableRightClickListenerReTypePassword = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int rightDrawableWidth = rePasswordText.getCompoundDrawablesRelative()[2].getBounds().width();

            if (event.getRawX() >= (rePasswordText.getRight()) - rightDrawableWidth) {

                return gestureDetectorReType.onTouchEvent(event);
            }
            return false;
        }
    };

    private void registerPersonalDetails(DriverAccountDTO account) {

        Call<DriverAccountDTO> driver = services.registerMobileNumber(account);

        driver.enqueue(new Callback<DriverAccountDTO>() {
            @Override
            public void onResponse(Call<DriverAccountDTO> call, Response<DriverAccountDTO> response) {

                ReusableFunctions.hideLoader(loader);
                if (response.code() == 200) {

                    System.out.println(response.body().toString());



                    Log.d("TAG_message", "=================Successful-CREATED==================");
                    Log.d("TAG_message", "CODE: " + response.code());

                } else {

                    if (response.code() == 400) {



                        ErrorResponse errorResponse = null;
                        try {
                            errorResponse = ReusableFunctions.convertErrorResponse(response.errorBody().string());
                            if(errorResponse.getMessage().equalsIgnoreCase(getString(R.string.device_msg)) || errorResponse.getMessage().contains(getString(R.string.account_msg))
                            || errorResponse.getMessage().equalsIgnoreCase(getString(R.string.mobile_exist))){
                                Intent intent = new Intent(SignUp.this, OTPVerification.class);
                                intent.putExtra("mobile", account.getDriverPersonalDetails().mobile);
                                Log.d("TAG_message", "onResponse: " + errorResponse.getMessage());

                                startActivity(intent);
                                Toast.makeText(SignUp.this, "converting account", Toast.LENGTH_LONG).show();
                            } else {
                                Log.d("TAG_message", "onResponse: " + errorResponse.getMessage());
                                snackbar = ReusableFunctions.snackbarInstance(nextBtn,errorResponse.getMessage(),Snackbar.LENGTH_INDEFINITE, getColor(R.color.snackbar_red), getColor(R.color.white), Gravity.BOTTOM);
                                snackbar.show();
                                ReusableFunctions.dismisSnackBar(snackbar);
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }

            }

            @Override
            public void onFailure(Call<DriverAccountDTO> call, Throwable t) {

                ReusableFunctions.hideLoader(loader);
                Log.e("ERROR", "ONFAILURE IS CALLED");
                Log.e("code", t.toString());
                System.out.println("/////////ON FAILURE METHOD IS CALLED////////");
                System.out.println(t.getCause());

            }
        });


    }


}