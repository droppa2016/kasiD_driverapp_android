package co.za.kasi.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.za.kasi.R;
import co.za.kasi.enums.PointType;
import co.za.kasi.model.Booking;
import co.za.kasi.model.BookingFlightInfoDTO;
import co.za.kasi.model.accountDTO.DriverDTO;
import co.za.kasi.model.accountDTO.UserDTO;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.utils.ReusableFunctions;
import retrofit2.Call;
import retrofit2.Callback;

public class FlightBookingDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    private ReturnStatus returnStatus;
    private AppCompatImageButton btnBackButton, btnWhatsappButton;
    private TextInputEditText departureDate;
    private AppCompatButton btnGetCode;
    private MaterialTextView title, tvCurrentNumber;

    View view;
    private UserDTO user;
    private DriverDTO driver;
    private String token;

    private String date;
    private int mYear, mMonth, mDay;
    private DriverHttpService services;
    private Loader loader;
    private String cellPhoneNumber = null;
    Toolbar toolbar;
    private Booking currentBooking;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private RelativeLayout parent;

    private AppCompatButton btnDepartureDate, btnDepartureTime, btnArrivalDate, btnArrivalTime, btnDone;
    private TextInputEditText edtFlightNumber, edtDepartureDate, edtDepartureTime, edtArrivalDate, edtArrivalTime, tempTimeInput, edtExtraInfo;
    private String currentPoint = PointType.PICK_UP.name();

    //private CardDetailsListener cardDetailsListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.flight_leg_process, null);
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
//        returnStatus = (ReturnStatus) view.getContext();
        title = view.findViewById(R.id.txtTitle);
        btnBackButton = view.findViewById(R.id.btnBack);
        btnWhatsappButton = view.findViewById(R.id.btnWhatsapp);
        
        tvCurrentNumber = view.findViewById(R.id.tvcurrentNumber);
        title.setText("Verify Client");
        btnBackButton.setOnClickListener(v -> {
            returnStatus.returnStatus(false);
            dismiss();
        });

        otpBlockSetUp();
        currentBooking = (Booking) getArguments().getSerializable("booking");


        btnDepartureDate = view.findViewById(R.id.btnDDateP);
        btnDepartureTime = view.findViewById(R.id.btnDTime);
        btnArrivalDate = view.findViewById(R.id.btnADate);
        btnArrivalTime = view.findViewById(R.id.btnATime);
        btnDone = view.findViewById(R.id.btnDone);
        edtDepartureDate = view.findViewById(R.id.edtDepartureDate);
        edtDepartureTime = view.findViewById(R.id.edtDepartureTime);
        edtArrivalDate = view.findViewById(R.id.edtArrivalDate);
        edtArrivalTime = view.findViewById(R.id.edtArrivalTime);
        edtFlightNumber = view.findViewById(R.id.edtFlightNumber);
        edtExtraInfo = view.findViewById(R.id.edtExtraInfo);
        parent = view.findViewById(R.id.parent);

        pressEvent();
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

    private void pressEvent() {

        btnDepartureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePicker(edtDepartureDate, true);
            }
        });

        btnDepartureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePicker(edtDepartureTime, false);
                tempTimeInput = edtDepartureTime;
            }
        });


        btnArrivalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePicker(edtArrivalDate, true);
            }
        });

        btnArrivalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePicker(edtArrivalTime, false);
                tempTimeInput = edtArrivalTime;
            }
        });

        btnDone.setOnClickListener(v -> {
            if (validateInfo()) {

                BookingFlightInfoDTO flightInfoDTO = new BookingFlightInfoDTO();
                flightInfoDTO.setArrivalDate(edtArrivalDate.getText().toString());
                flightInfoDTO.setBookingOid(currentBooking.getOid());
                flightInfoDTO.setDepartureDate(edtDepartureDate.getText().toString());
                flightInfoDTO.setEwaybill(currentBooking.getTrackNo());
                flightInfoDTO.setFlightNo(edtFlightNumber.getText().toString());

                loader = ReusableFunctions.showLoader(getContext());
                endFlightElement(flightInfoDTO);
                Snackbar.make(btnDone, "Onboarding flight", Snackbar.LENGTH_LONG).show();
            }
        });

        parent.setOnClickListener(v -> {
            edtFlightNumber.clearFocus();
            edtExtraInfo.clearFocus();
        });
    }

    private void endFlightElement(@NonNull BookingFlightInfoDTO flightInfoDTO) {

        Call<Booking> bookingCreateCall = services.airLineElementCompleted(token, flightInfoDTO);

        bookingCreateCall.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull retrofit2.Response<Booking> response) {
                ReusableFunctions.hideLoader(loader);
                if (response.code() == 200) {
                    if (response.body() != null) {
                        Log.d("TAG__flight_e", "onResponse: " + response.body());
                    } else Toast.makeText(getContext(), "Bucket not sync.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unknown error occurred.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {
                ReusableFunctions.hideLoader(loader);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void openDateTimePicker(TextInputEditText editText, boolean isDatePicker) {
        if (isDatePicker) {
            datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog, (view, year, monthOfYear, dayOfMonth) -> {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM--dd", Locale.US);
                String day = dayOfMonth <= 9 ? "0".concat(String.valueOf(dayOfMonth)) : String.valueOf(dayOfMonth);
                String month = (monthOfYear + 1) <= 9 ? "0".concat(String.valueOf((monthOfYear + 1))) : String.valueOf(monthOfYear + 1);
                date = year + "-" + month + "-" + day;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if ((LocalDate.now().getDayOfMonth() - year) > 5) {
//                    Toast.makeText(getContext(), "License is expired", Toast.LENGTH_LONG).show();
//                } else {
                    editText.setText(date);
                    // }
                }

            }, mYear, mMonth, mDay);

            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            // Add 4 days to the current date
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            Date increasedDate = calendar.getTime();
            datePickerDialog.getDatePicker().setMaxDate(increasedDate.getTime());
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.getDatePicker().setBackgroundColor(getContext().getColor(R.color.card_background));
            datePickerDialog.show();
        } else {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog, this, hour, minute, false);
            timePickerDialog.show();
        }


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

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String hour = hourOfDay + "";
        String min = minute + "";
        if (String.valueOf(hourOfDay).length() == 1) {
            hour = "0" + hourOfDay;
        }

        if (String.valueOf(minute).length() == 1) {
            min = "0" + minute;
        }

        String amPm;
        if (hourOfDay < 12) {
            amPm = "AM";
        } else {
            amPm = "PM";
            // Convert 24-hour format to 12-hour format
            if (hourOfDay > 12) {
                hourOfDay -= 12;
            }
        }

        String time = hour + ":" + min + " " + amPm;
        tempTimeInput.setText(time);
    }

    private boolean validateInfo() {
        boolean isSuccessful = true;
        if (TextUtils.isEmpty(edtDepartureDate.getText())) {
            edtDepartureDate.setBackground(getContext().getDrawable(R.drawable.text_background_error));
            edtDepartureDate.startAnimation(ReusableFunctions.shakeError());
            edtDepartureDate.setError("Required");
            isSuccessful = false;
        } else {
            edtDepartureDate.setBackground(getContext().getDrawable(R.drawable.text_background));
            edtDepartureDate.setError(null);
        }

        if (TextUtils.isEmpty(edtDepartureTime.getText())) {

            edtDepartureTime.setBackground(getContext().getDrawable(R.drawable.text_background_error));
            edtDepartureTime.startAnimation(ReusableFunctions.shakeError());
            edtDepartureTime.setError("Required");
            isSuccessful = false;
        } else {
            edtDepartureTime.setBackground(getContext().getDrawable(R.drawable.text_background));
            edtDepartureTime.setError(null);
        }

        if (TextUtils.isEmpty(edtArrivalDate.getText())) {

            edtArrivalDate.setBackground(getContext().getDrawable(R.drawable.text_background_error));
            edtArrivalDate.startAnimation(ReusableFunctions.shakeError());
            edtArrivalDate.setError("Required");
            isSuccessful = false;
        } else {
            edtArrivalDate.setBackground(getContext().getDrawable(R.drawable.text_background));
            edtArrivalDate.setError(null);
        }

        if (TextUtils.isEmpty(edtArrivalTime.getText())) {

            edtArrivalTime.setBackground(getContext().getDrawable(R.drawable.text_background_error));
            edtArrivalTime.startAnimation(ReusableFunctions.shakeError());
            edtArrivalTime.setError("Required");
            isSuccessful = false;
        } else {
            edtArrivalTime.setBackground(getContext().getDrawable(R.drawable.text_background));
            edtArrivalTime.setError(null);
        }

        if (TextUtils.isEmpty(edtFlightNumber.getText())) {

            edtFlightNumber.setBackground(getContext().getDrawable(R.drawable.text_background_error));
            edtFlightNumber.startAnimation(ReusableFunctions.shakeError());
            edtFlightNumber.setError("Required");
            isSuccessful = false;
        } else {
            edtFlightNumber.setBackground(getContext().getDrawable(R.drawable.text_background));
            edtFlightNumber.setError(null);
        }

        return isSuccessful;
    }

    public interface ReturnStatus {
        void returnStatus(boolean status);
    }
}
