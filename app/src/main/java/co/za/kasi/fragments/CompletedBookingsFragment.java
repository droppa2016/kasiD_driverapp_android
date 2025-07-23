package co.za.kasi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import co.za.kasi.R;

import co.za.kasi.dialogs.Loader;
import co.za.kasi.model.Booking;
import co.za.kasi.model.CompleteBookingDTO;
import co.za.kasi.model.accountDTO.DriverDTO;
import co.za.kasi.model.accountDTO.UserDTO;
import co.za.kasi.recyclerview.AvailableBookingsAdapter;
import co.za.kasi.recyclerview.ReservedBookingsAdapter;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.services.RecyclerViewInterface;
import co.za.kasi.utils.ReusableFunctions;
import retrofit2.Call;
import retrofit2.Callback;


public class CompletedBookingsFragment extends Fragment implements RecyclerViewInterface {


    private DriverHttpService services;

    private RecyclerViewInterface recyclerViewInterface;

    private ArrayList<Booking> bookingHistory;
    private View view;
    private UserDTO user;
    private DriverDTO driver;
    private String token;
    private Snackbar snackbar;
    private Loader loader;
    private AvailableBookingsAdapter adapter;

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

    private void setUpRecycler(ArrayList<Booking> bookings) {
        adapter = new AvailableBookingsAdapter(getContext(), bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new ReservedBookingsAdapter.OnClickListener() {
            @Override
            public void onViewClick(View button, Booking booking) {


            }

            @Override
            public void onCancelClick(View button, Booking booking) {

            }

        });
    }

    private void getBookingsHistory() {

        Call<CompleteBookingDTO> bookingCreateCall = services.getBookings(token, driver.getOid());
        bookingCreateCall.enqueue(new Callback<CompleteBookingDTO>() {
            @Override
            public void onResponse(@NonNull Call<CompleteBookingDTO> call, @NonNull retrofit2.Response<CompleteBookingDTO> response) {
                ReusableFunctions.hideLoader(loader);
                if (response.code() == 200) {
                    if (response.body() != null) {
                        bookingHistory = new ArrayList<>();
                        for (Booking booking : (ArrayList<Booking>) response.body().getBookings()) {
                            if (!booking.isBucket()) {
                                bookingHistory.add(booking);
                                //   Log.d("tag","-------------------contact - "+bookingHistory.get(2).getDropOff().getLastName());

                            }
                        }
                        Log.d("bookings_size", " size all " + response.body().getBookings().size());
                        Log.d("bookings_size", " size filtered " + bookingHistory.size());

                        setUpRecycler(bookingHistory);

                        adapter.notifyDataSetChanged();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<CompleteBookingDTO> call, @NonNull Throwable t) {
                // Log.d("NICOLAS",  "Error "+call.toString());
                ReusableFunctions.hideLoader(loader);
            }
        });
    }


    @Override
    public void onItemClick(int position) {



    }
}