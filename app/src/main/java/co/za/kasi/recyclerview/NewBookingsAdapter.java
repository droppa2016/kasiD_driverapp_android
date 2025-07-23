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
import co.za.kasi.model.Booking;

public class NewBookingsAdapter extends RecyclerView.Adapter<NewBookingsAdapter.ViewHolder> {

    private ArrayList<Booking> bookings;
    private Context context;

    private OnClickListener onClickListener;
    public NewBookingsAdapter(Context context, ArrayList<Booking> bookings) {
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
    public NewBookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_booking_widget, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull NewBookingsAdapter.ViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        holder.type.setText(booking.getType());
        holder.time.setText(booking.getPickUpTime());
        holder.date.setText(booking.getPickUpDate());
        holder.pickupAddress.setText(booking.getPickUpAddress());
        holder.droppOffAddress.setText(booking.getDropOffAddress());

        holder.viewBooking.setOnClickListener(v -> { onClickListener.onViewClick(v, bookings.get(position));});
        holder.declineBooking.setOnClickListener(v -> { onClickListener.onCancelClick(v, bookings.get(position));});
    }

    @Override
    public int getItemCount() {
        return 2;
    }



    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener{
        void onViewClick(View button, Booking booking);
        void onCancelClick(View button, Booking booking);
    }
}
