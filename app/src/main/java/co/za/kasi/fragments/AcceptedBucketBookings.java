package co.za.kasi.fragments;

import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import co.za.kasi.R;
import co.za.kasi.dialogs.Loader;
import co.za.kasi.enums.BookingStatus;
import co.za.kasi.model.BucketBooking;
import co.za.kasi.model.accountDTO.DriverDTO;
import co.za.kasi.model.accountDTO.UserDTO;
import co.za.kasi.recyclerview.ReservedBucketssAdapter;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.utils.ReusableFunctions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptedBucketBookings extends Fragment {

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

        view = inflater.inflate(R.layout.fragment_accepted_bucket_bookings, container, false);
        init();

        return view;
    }

    private void init() {
        recyclerView = view.findViewById(R.id.rcvReservedBuckets);
        services = ReusableFunctions.initiateRetrofit(getContext());
        loader = ReusableFunctions.showLoader(getContext());
        getLocalData();
        getBookings();
    }

    private void getLocalData() {
        user = LocalStorage.getUserAccount();
        driver = LocalStorage.getDriverAccount();
        token = LocalStorage.getToken(getContext());

    }


    private void getBookings() {

        Call<ArrayList<BucketBooking>> getBookinhCall = services.getReservedBuckets(token, BookingStatus.RESERVED.name(), driver.getOid());
        getBookinhCall.enqueue(new Callback<ArrayList<BucketBooking>>() {
            @Override
            public void onResponse(Call<ArrayList<BucketBooking>> call, Response<ArrayList<BucketBooking>> response) {
                ReusableFunctions.hideLoader(loader);
                if (response.code() == 200) {
                    Collections.sort(response.body(), Comparator.comparing(BucketBooking::getDate).reversed());
                    setUpRecycler(response.body());

                } else {
                    Log.d("TAG_available_bokings", "onResponse: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BucketBooking>> call, Throwable t) {

                ReusableFunctions.hideLoader(loader);
            }
        });
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

            }
        });
    }
}