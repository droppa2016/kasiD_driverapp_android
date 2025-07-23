package co.za.kasi.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import co.za.kasi.R;
import co.za.kasi.model.Booking;
import co.za.kasi.model.BucketBooking;
import co.za.kasi.recyclerview.ViewAllBookingsAdapter;

public class ViewAllBookingsDialog extends DialogFragment {

    private RecyclerView recyclerView;
    ViewAllBookingsAdapter adapter;

    private BucketBooking bucketBooking;
    String point_type;
    View view;

    public interface MyClickListener {
        void onGoToBookingListener();
        void onFinencialListener();
    }


    private MyClickListener listener;

    //private CardDetailsListener cardDetailsListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.view_all_bookings_layout, null);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_transparent_backgroudn));
        }

        init();


        return view;
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

    private void init() {

        recyclerView = view.findViewById(R.id.rcvBookings);
        bucketBooking = (BucketBooking) getArguments().getSerializable("booking");
        point_type = getArguments().getString("point_type");
//        listener = (MyClickListener) view.getContext();
        if(bucketBooking != null){
            setUpRecycler();
        } else {
           // Toast.makeText(getContext(), "No bookings found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpRecycler(){
        adapter = new ViewAllBookingsAdapter(getContext(), (ArrayList<Booking>) bucketBooking.getBookings(), point_type);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       // Toast.makeText(getContext(), "No bookings found", Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(adapter);
    }
   // @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            listener = (MyClickListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement MyDialogListener");
//        }
//    }
}
