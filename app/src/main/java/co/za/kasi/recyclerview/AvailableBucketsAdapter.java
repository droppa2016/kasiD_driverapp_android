package co.za.kasi.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;


import co.za.kasi.R;
import co.za.kasi.model.BucketBooking;

public class AvailableBucketsAdapter extends RecyclerView.Adapter<AvailableBucketsAdapter.ViewHolder> {

    private ArrayList<BucketBooking> bookings;
    private Context context;

    private OnClickListener onClickListener;
    public AvailableBucketsAdapter(Context context, ArrayList<BucketBooking> bookings) {
        this.bookings = bookings;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView date, time, pickupAddress, droppOffAddress, type;
        AppCompatButton viewBooking, declineBooking;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.txtDate);
            time = itemView.findViewById(R.id.txtTime);
            pickupAddress = itemView.findViewById(R.id.txtPickUpAddress);
            droppOffAddress = itemView.findViewById(R.id.txtDroppOffAddress);
            type = itemView.findViewById(R.id.txtBookingType);

            viewBooking = itemView.findViewById(R.id.btnViewBooking);
            declineBooking = itemView.findViewById(R.id.btnDeclineBooking);
        }
    }
    @NonNull
    @Override
    public AvailableBucketsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_booking_widget, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AvailableBucketsAdapter.ViewHolder holder, int position) {
        BucketBooking booking = bookings.get(position);

        holder.declineBooking.setVisibility(View.GONE);
        holder.type.setText(booking.getType());
        holder.time.setText(booking.getTime());
        holder.date.setText(booking.getDate().substring(0, booking.getDate().indexOf("T")));

        if (booking.isInBound()) {
            holder.pickupAddress.setText(booking.getBookings().size() + " Pick Up points");
            holder.droppOffAddress.setText(booking.getBookings().get(0).getPickUpAddress());
        } else {
            holder.pickupAddress.setText(booking.getBookings().get(0).getPickUpAddress());
            holder.droppOffAddress.setText(booking.getBookings().size() + " Drop Off points");
        }

        holder.viewBooking.setOnClickListener(v -> { onClickListener.onViewClick(v, bookings.get(position));});
        holder.declineBooking.setOnClickListener(v -> { onClickListener.onCancelClick(v, bookings.get(position));});

    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }


    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener{
        void onViewClick(View button, BucketBooking booking);
        void onCancelClick(View button, BucketBooking booking);
    }
}
