package co.za.kasi.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import co.za.kasi.R;

import co.za.kasi.dialogs.Loader;
import co.za.kasi.model.BucketBooking;
import co.za.kasi.model.CompleteBookingDTO;
import co.za.kasi.model.accountDTO.DriverDTO;
import co.za.kasi.model.accountDTO.UserDTO;
import co.za.kasi.recyclerview.ReservedBucketssAdapter;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.utils.ReusableFunctions;
import retrofit2.Call;
import retrofit2.Callback;

public class CompletedBucketsFragment extends Fragment {

    private DriverHttpService services;
    private View view;
    private UserDTO user;
    private DriverDTO driver;
    private String token;
    private Snackbar snackbar;
    private Loader loader;
    private ReservedBucketssAdapter adapter;

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_completed_bookings, container, false);
        init();

        return view;
    }

    private void init() {
        recyclerView = view.findViewById(R.id.rcvCompletedBooking);
        services = ReusableFunctions.initiateRetrofit(getContext());
        loader = ReusableFunctions.showLoader(getContext());
        getLocalData();
        getBookingsHistory();
    }

    private void getLocalData() {
        user = LocalStorage.getUserAccount();
        driver = LocalStorage.getDriverAccount();
        token = LocalStorage.getToken(getContext());

    }

    private void setUpRecycler(ArrayList<BucketBooking> bookings) {
        adapter = new ReservedBucketssAdapter(getContext(), bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new ReservedBucketssAdapter.OnClickListener() {
            @Override
            public void onViewClick(View button, BucketBooking booking) {

            }

            @Override
            public void onCancelClick(View button, BucketBooking booking) {
                Toast.makeText(getContext(), " ====================|||||||||||==========", Toast.LENGTH_SHORT).show();
            }

        });

    }


    private void getBookingsHistory( ) {

        Call<CompleteBookingDTO> bookingCreateCall = services.getBookings(token, driver.getOid());
        bookingCreateCall.enqueue(new Callback<CompleteBookingDTO>() {
            @Override
            public void onResponse(@NonNull Call<CompleteBookingDTO> call, @NonNull retrofit2.Response<CompleteBookingDTO> response) {
                ReusableFunctions.hideLoader(loader);
                if(response.code() == 200) {
                    if(response.body() != null) {
                        setUpRecycler((ArrayList<BucketBooking>) response.body().getBucketBookings());
                        adapter.notifyDataSetChanged();
                    }
                }else{

                }
            }

            @Override
            public void onFailure(@NonNull Call<CompleteBookingDTO> call, @NonNull Throwable t) {
                // Log.d("NICOLAS",  "Error "+call.toString());
                ReusableFunctions.hideLoader(loader);
            }
        });
    }
}