package co.za.kasi.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import co.za.kasi.R;
import co.za.kasi.enums.PointType;
import co.za.kasi.model.Booking;
import co.za.kasi.model.BucketBooking;
import co.za.kasi.utils.ReusableFunctions;

public class ViewAllBookingsAdapter extends RecyclerView.Adapter<ViewAllBookingsAdapter.ViewHolder> {

    private ArrayList<Booking> bookings;
    private Context context;
    String point_type;

    private int todaysBookings = 0;

    private OnClickListener onClickListener;

    public ViewAllBookingsAdapter(Context context, ArrayList<Booking> bookings, String point_type) {
        this.bookings = bookings;
        this.context = context;
        this.point_type = point_type;

        Collections.sort(bookings, Comparator.comparing(Booking::getPickUpDate).reversed());
       // todaysBookings = getTodaysBookings(bookings);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView date, time, pickupAddress, price, type, status;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.tvBucketDate);
            time = itemView.findViewById(R.id.tvBucketTime);
            pickupAddress = itemView.findViewById(R.id.tvPickUpAddress);
            price = itemView.findViewById(R.id.tvBucketPrice);
            status = itemView.findViewById(R.id.tvBucketStatus);
            type = itemView.findViewById(R.id.tvBucketType);

            card = itemView.findViewById(R.id.card);
        }
    }

    @NonNull
    @Override
    public ViewAllBookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_bucket_widget, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewAllBookingsAdapter.ViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        holder.type.setText(booking.getType());
        holder.time.setText(booking.getPickUpTime());
        holder.date.setText(booking.getPickUpDate());
        holder.status.setText(booking.getStatus());
        holder.price.setText("R" + ReusableFunctions.getNetPrice(booking.getPrice()));

        if(point_type.equals(PointType.PICK_UP.name())) {
            holder.pickupAddress.setText(booking.getPickUpAddress());
        } else {
            holder.pickupAddress.setText(booking.getDropOffAddress());
        }


    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    private int getTodaysBookings(ArrayList<BucketBooking> bucketBookings) {
        int today = 0;

        for (BucketBooking booking : bucketBookings) {
            if (ReusableFunctions.compareDates(ReusableFunctions.convertStringToDate(booking.getDate()))) {
                today++;
            }
        }

        return today;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener {
        void onViewClick(View button, Booking booking);

        void onCancelClick(View button, Booking booking);
    }
}
