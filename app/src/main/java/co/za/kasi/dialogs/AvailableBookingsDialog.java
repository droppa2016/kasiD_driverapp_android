package co.za.kasi.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;

import co.za.kasi.FallBackPage;

import co.za.kasi.R;
import co.za.kasi.model.Booking;
import co.za.kasi.model.ErrorResponse;
import co.za.kasi.model.accountDTO.DriverDTO;
import co.za.kasi.model.accountDTO.UserDTO;
import co.za.kasi.recyclerview.NewBookingsAdapter;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.utils.ReusableFunctions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailableBookingsDialog extends AppCompatDialogFragment {

    //private CardDetailsListener cardDetailsListener;

    View view;
    DriverHttpService services;
    private UserDTO user;
    private DriverDTO driver;
    private String token;

    private RecyclerView recyclerView;
    private AppCompatButton button;

    private NewBookingsAdapter adapter;
    private Loader loader;
    private Snackbar snackbar;

    private Intent intentFallBack;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view  = LayoutInflater.from(inflater.getContext()).inflate(R.layout.available_bookins_popup,null);
        init();
        Window window = getDialog().getWindow();
        if(window != null){
            window.setBackgroundDrawable(view.getContext().getDrawable(R.drawable.dialog_transparent_backgroudn));
        }

        return view;
    }

    private void init(){
        intentFallBack = new Intent(getContext(), FallBackPage.class);
        intentFallBack.putExtra("class", "Dashboard");
        services = ReusableFunctions.initiateRetrofit(getContext());
        recyclerView = view.findViewById(R.id.rcvAvvailableBooking);
        button = view.findViewById(R.id.btnView);
        loader = ReusableFunctions.showLoader(getContext());
        getLocalData();
        getAvailable();
    }

    private void setUpRecycler(ArrayList<Booking> bookings){
        adapter = new NewBookingsAdapter(getContext() ,bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void getAvailable(){

        if(LocalStorage.getNewBookings() != null) {
            ArrayList<String> bookings = new ArrayList<>();
            bookings.addAll(LocalStorage.getNewBookings());

            Log.d("TAG_available_bokings", "onResponse: " + bookings.toString());
            Call<ArrayList<Booking>> getAvailableBookingcall = services.getNewBookings(token, bookings);
            getAvailableBookingcall.enqueue(new Callback<ArrayList<Booking>>() {
                @Override
                public void onResponse(Call<ArrayList<Booking>> call, Response<ArrayList<Booking>> response) {
                    ReusableFunctions.hideLoader(loader);
                    if (response.code() == 200) {
                        Log.d("TAG_available_bokings", "onResponse: " + response.body());
                        setUpRecycler(response.body());
                    } else {

                        ReusableFunctions.hideLoader(loader);
                        if (response.errorBody() != null) {

                            try {
                                if (response.code() == 400) {
                                    ErrorResponse errorResponse = ReusableFunctions.convertErrorResponse(response.errorBody().string());
                                    snackbar = ReusableFunctions.snackbarInstance(recyclerView, errorResponse.getMessage(), Snackbar.LENGTH_INDEFINITE, getContext().getColor(R.color.snackbar_red), getContext().getColor(R.color.white), Gravity.BOTTOM);
                                    snackbar.show();
                                    ReusableFunctions.dismisSnackBar(snackbar);
                                } else {
                                    intentFallBack.putExtra("error", getString(R.string.techical_error_message));
                                    intentFallBack.putExtra("server", "server");
                                    startActivity(intentFallBack);
                                    getActivity().finish();
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Booking>> call, Throwable t) {

                    ReusableFunctions.hideLoader(loader);
                }
            });
        } else {
            snackbar = ReusableFunctions.snackbarInstance(getActivity().findViewById(android.R.id.content), "There are no new bookings.",Snackbar.LENGTH_LONG, getContext().getColor(R.color.snackbar_red), getContext().getColor(R.color.white), Gravity.BOTTOM);
            snackbar.show();
            ReusableFunctions.hideLoader(loader);
            dismiss();
        }
    }

    private void getLocalData(){
        user = LocalStorage.getUserAccount();
        driver = LocalStorage.getDriverAccount();
        token = LocalStorage.getToken(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

}
