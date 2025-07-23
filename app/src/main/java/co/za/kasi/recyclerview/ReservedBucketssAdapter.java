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
import co.za.kasi.model.BucketBooking;
import co.za.kasi.utils.ReusableFunctions;

public class ReservedBucketssAdapter extends RecyclerView.Adapter<ReservedBucketssAdapter.ViewHolder> {

    private ArrayList<BucketBooking> bookings;
    private Context context;

    private int todaysBookings = 0;

    private OnClickListener onClickListener;

    public ReservedBucketssAdapter(Context context, ArrayList<BucketBooking> bookings) {
        this.bookings = bookings;
        this.context = context;

        Collections.sort(bookings, Comparator.comparing(BucketBooking::getDate).reversed());
        todaysBookings = getTodaysBookings(bookings);
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
    public ReservedBucketssAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_bucket_widget, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReservedBucketssAdapter.ViewHolder holder, int position) {
        BucketBooking booking = bookings.get(position);

        holder.type.setText(booking.getType());
        holder.time.setText(booking.getTime());
        holder.date.setText(booking.getDate().substring(0,booking.getDate().indexOf("T")));
        holder.status.setText(booking.getStatus());
        holder.price.setText("R " + ReusableFunctions.getNetPrice(booking.getPrice()));

        holder.pickupAddress.setText(booking.getBookings().get(0).getPickUpAddress());

        holder.card.setOnClickListener(v -> {
            onClickListener.onViewClick(v, bookings.get(position));
        });

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
        void onViewClick(View button, BucketBooking booking);

        void onCancelClick(View button, BucketBooking booking);
    }
}
