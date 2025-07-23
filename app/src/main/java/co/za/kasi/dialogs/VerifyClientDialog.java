package co.za.kasi.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;


import co.za.kasi.R;
import co.za.kasi.enums.PointType;
import co.za.kasi.model.Booking;
import co.za.kasi.model.accountDTO.DriverDTO;
import co.za.kasi.model.accountDTO.UserDTO;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.utils.ReusableFunctions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyClientDialog extends DialogFragment {


    private ReturnStatus returnStatus;
    private AppCompatImageButton btnBackButton, btnWhatsappButton;
    private TextInputEditText mobileNumber;
    private AppCompatButton btnGetCode;
    private MaterialTextView title, tvCurrentNumber;

    private TextInputEditText otpBlock1, otpBlock2, otpBlock3, otpBlock4;
    private MaterialTextView txtResendOTP;
    private AppCompatButton verifyBtn;
    View view;
    private UserDTO user;
    private DriverDTO driver;
    private String token;

    private DriverHttpService services;
    private Loader loader;
    private String cellPhoneNumber = null;
    Toolbar toolbar;
    private Booking currentBooking;
    private String currentPoint = PointType.PICK_UP.name();

    //private CardDetailsListener cardDetailsListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.verifying_with_mobile, null);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        init();
        Window window = getDialog().getWindow();
        getDialog().setCancelable(false);
        window.setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_transparent_backgroudn));

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    // Handle the back button press here
                    dismiss();
                    return true; // Consume the event
                }
                return false; // Pass the event to the next listener
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void init() {

        toolbar = view.findViewById(R.id.custom_toolbar);
        // Set a custom close icon
        services = ReusableFunctions.initiateRetrofit(getContext());
        getLocalData();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custon_action_bar);
        actionBar.show();
        returnStatus = (ReturnStatus) view.getContext();
        title = view.findViewById(R.id.txtTitle);
        btnBackButton = view.findViewById(R.id.btnBack);
        btnWhatsappButton = view.findViewById(R.id.btnWhatsapp);
        
        tvCurrentNumber = view.findViewById(R.id.tvcurrentNumber);
        title.setText("Verify Client");
        btnBackButton.setOnClickListener(v -> {
            returnStatus.returnStatus(false);
            dismiss();
        });

        btnGetCode = view.findViewById(R.id.btnGetOTP);
        mobileNumber = view.findViewById(R.id.edtClientNumber);

        otpBlock1 = view.findViewById(R.id.otpBlock1);
        otpBlock2 = view.findViewById(R.id.otpBlock2);
        otpBlock3 = view.findViewById(R.id.otpBlock3);
        otpBlock4 = view.findViewById(R.id.otpBlock4);
        verifyBtn = view.findViewById(R.id.btnVerifyAccount);
        txtResendOTP = view.findViewById(R.id.txtResendOTP);
        otpBlockSetUp();
        currentBooking = (Booking) getArguments().getSerializable("booking");

        if (currentBooking != null) {
            if (currentBooking.isItemPicked()) {
                cellPhoneNumber = currentBooking.getDropOff().getPhone();
                mobileNumber.setText("*** *** " + cellPhoneNumber.substring(6));
                currentPoint = PointType.DROP_OFFS.name();
            } else {
                cellPhoneNumber = currentBooking.getPickUp().getPhone();
                mobileNumber.setText("*** *** " + cellPhoneNumber.substring(6));
                currentPoint = PointType.PICK_UP.name();
            }
        }

        btnGetCode.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(cellPhoneNumber)) {
               loader = ReusableFunctions.showLoader(getContext());
                getCode();
            } else {

            }
        });

        verifyBtn.setOnClickListener(v -> {
           String otp = otpBlock1.getText().toString().trim() + otpBlock2.getText().toString().trim() + otpBlock3.getText().toString().trim() + otpBlock4.getText().toString().trim();

            if(otp.length() == 4){

                loader = ReusableFunctions.showLoader(getContext());
                verifyCode();
            } else {
                highlightBox(otpBlock1);
                highlightBox(otpBlock2);
                highlightBox(otpBlock3);
                highlightBox(otpBlock4);
            }
        });


/*        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnStatus.returnStatus(false);
                dismiss();
                Log.d("OnBackPressedDispatcher", "handleOnBackPressed: pressed");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);*/
    }

    private void getCode(){

        Call<ResponseBody> getCodeCall = services.requestVerificationCode(token,currentPoint, currentBooking.getOid(),mobileNumber.getText().toString());
        getCodeCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ReusableFunctions.hideLoader(loader);
                if(response.code() == 200){
                    try {
                        tvCurrentNumber.setText("OTP Sent to " + mobileNumber.getText());
                        Log.d("TAG___", "onResponse: " + response.body().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        Log.e("TAG___", "onResponse: " + response.errorBody().string());
                        Log.d("TAG___", "onResponse: " + mobileNumber.getText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ReusableFunctions.hideLoader(loader);

            }
        });
    }

    private void verifyCode(){

        Call<ResponseBody> getCodeCall = services.requestVerificationCode(token,currentPoint, currentBooking.getOid(),mobileNumber.getText().toString());
        getCodeCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ReusableFunctions.hideLoader(loader);
                if(response.code() == 200){
                    try {
                        Log.d("TAG___", "onResponse: " + response.body().string());
                        returnStatus.returnStatus(true);
                        dismiss();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    highlightBox(otpBlock1);
                    highlightBox(otpBlock2);
                    highlightBox(otpBlock3);
                    highlightBox(otpBlock4);
                    try {
                        Log.e("TAG___", "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ReusableFunctions.hideLoader(loader);
            }
        });
    }

    private void getLocalData() {
        user = LocalStorage.getUserAccount();
        driver = LocalStorage.getDriverAccount();
        token = LocalStorage.getToken(getContext());
    }

    private void highlightBox(TextInputEditText text) {

        text.setBackground(getContext().getDrawable(R.drawable.otp_block_error));
        text.startAnimation(ReusableFunctions.shakeError());

    }
    private void otpBlockSetUp() {

        otpBlock1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


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

    public interface ReturnStatus{
        void returnStatus(boolean status);
    }
}
