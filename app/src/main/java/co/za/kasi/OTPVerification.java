package co.za.kasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import co.za.kasi.dialogs.Loader;
import co.za.kasi.model.OtpVerificationDTO;
import co.za.kasi.model.ResendOtpDTO;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.utils.ReusableFunctions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.za.kasi.R;

public class OTPVerification extends AppCompatActivity {

//    111111111111111111111111

    Loader loader;
    private TextInputEditText otpBlock1, otpBlock2, otpBlock3, otpBlock4;
    private MaterialTextView txtResendOTP , errorMessage;
    private AppCompatButton verifyBtn;
    private MaterialTextView heading, subtitle;

    private TextView userNumber;

    private String otp;

    private String mobile;

    private Boolean confirmed;

    private DriverHttpService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        init();

        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        userNumber.setText(mobile);

        otpBlockSetUp();
        txtResendOTP.setOnClickListener(v -> {
            resendOtp(mobile);
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otp = otpBlock1.getText().toString().trim() + otpBlock2.getText().toString().trim() + otpBlock3.getText().toString().trim() + otpBlock4.getText().toString().trim();
                if(otp.length() != 4){
                    highlightBox(otpBlock1);
                    highlightBox(otpBlock2);
                    highlightBox(otpBlock3);
                    highlightBox(otpBlock4);
                    return;
                }
                loader = ReusableFunctions.showLoader(OTPVerification.this);
                otpVerification(mobile, otp);

                //startActivity(new Intent(OTPVerification.this, LicenseInformationRegistration.class));

            }
        });

    }

    private void highlightBox(TextInputEditText text) {

        text.setBackground(getDrawable(R.drawable.otp_block_error));
        text.startAnimation(ReusableFunctions.shakeError());

    }

    private void removehighlightBox() {

        errorMessage.setVisibility(View.GONE);
        otpBlock1.setBackground(getDrawable(R.drawable.otp_block_background));
        otpBlock2.setBackground(getDrawable(R.drawable.otp_block_background));
        otpBlock3.setBackground(getDrawable(R.drawable.otp_block_background));
        otpBlock4.setBackground(getDrawable(R.drawable.otp_block_background));

    }

    private void init() {

        otpBlock1 = findViewById(R.id.otpBlock1);
        otpBlock2 = findViewById(R.id.otpBlock2);
        otpBlock3 = findViewById(R.id.otpBlock3);
        otpBlock4 = findViewById(R.id.otpBlock4);
        verifyBtn = findViewById(R.id.btnVerifyAccount);
        txtResendOTP = findViewById(R.id.txtResendOTP);
        errorMessage = findViewById(R.id.txtErrorMessage);

        userNumber = findViewById(R.id.mobileNumber_txt);

        heading = findViewById(R.id.unauthorized_heading);
        heading.setText(R.string.otp_heading);
        subtitle = findViewById(R.id.unauthorized_subText);
        subtitle.setText(R.string.otp_subtitle);

        service = ReusableFunctions.initiateRetrofit(OTPVerification.this);

    }

//    @NonNull
//    @Override
//    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
//
//        alert = ReusableFunctions.showAlert(OTPVerification.this, "Title", "Message", DialogTypes.SECURITY.name());
//        alert.show(getSupportFragmentManager(),DialogTypes.SECURITY.name());
//        return super.getOnBackInvokedDispatcher();
//
//    }

    private void otpBlockSetUp() {

        otpBlock1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                removehighlightBox();
                if (count == 1) {

                    otpBlock2.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        otpBlock2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                removehighlightBox();
                if (count == 1) {
                    otpBlock3.requestFocus();
                }

                otpBlock2.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (count == 0 && keyCode == KeyEvent.KEYCODE_DEL) {
                            otpBlock1.requestFocus();

                        } else if (count == 1 && !(keyCode == KeyEvent.KEYCODE_DEL)) {
                            otpBlock3.requestFocus();
                        }
                        return false;
                    }
                });


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otpBlock3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removehighlightBox();
                if (count == 1) {
                    otpBlock4.requestFocus();
                }

                otpBlock3.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (count == 0 && keyCode == KeyEvent.KEYCODE_DEL) {
                            otpBlock2.requestFocus();

                        } else if (count == 1 && !(keyCode == KeyEvent.KEYCODE_DEL)) {
                            otpBlock4.requestFocus();

                        }
                        return false;
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otpBlock4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                removehighlightBox();
                otpBlock4.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (count == 0 && keyCode == KeyEvent.KEYCODE_DEL) {
                            otpBlock3.requestFocus();

                        }

                        return false;
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void otpVerification(String mobile, String code) {


        Call<OtpVerificationDTO> verificationCall = service.otpVerification(mobile, code);

        boolean confirm = false;

        verificationCall.enqueue(new Callback<OtpVerificationDTO>() {
            @Override
            public void onResponse(Call<OtpVerificationDTO> call, Response<OtpVerificationDTO> response) {
                ReusableFunctions.hideLoader(loader);
                if (response.code() == 200) {

                    if (response.body().isConfirmed()) {


                    } else {

                        highlightBox(otpBlock1);
                        highlightBox(otpBlock2);
                        highlightBox(otpBlock3);
                        highlightBox(otpBlock4);

                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText("Invalid Pin Code provided. Please verify the code.");

                    }

                } else {
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText("Technical error occurred. please try again in a few minutes.");


                }

            }

            @Override
            public void onFailure(Call<OtpVerificationDTO> call, Throwable t) {

                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText(t.getMessage());

            }
        });


    }

    private void resendOtp(String mobile) {

        Call<ResendOtpDTO> resendCall = service.resendOtp(mobile);

        resendCall.enqueue(new Callback<ResendOtpDTO>() {
            @Override
            public void onResponse(Call<ResendOtpDTO> call, Response<ResendOtpDTO> response) {

                if (response.code() == 200) {

                    Toast.makeText(OTPVerification.this, "A new OTP has been sent.", Toast.LENGTH_SHORT).show();

                } else if (response.code() == 400) {

                    Toast.makeText(OTPVerification.this, "Number has not been used to register", Toast.LENGTH_LONG).show();

                } else {
                    Log.e("ERROR", "FAILED ---  ELSE IS CALLED*************************************" + response.code());
                    Log.e("code", response.code() + response.errorBody().toString());
                    System.out.println("ELSE IS TRIGGERED");
                    System.out.println("CODE RETURNED: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<ResendOtpDTO> call, Throwable t) {

                Log.e("ERROR", "ONFAILURE IS CALLED");
                Log.e("code", t.toString());
                System.out.println("/////////ON FAILURE METHOD IS CALLED////////");
                System.out.println(t.getCause());

            }
        });

    }
}