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


import co.za.kasi.R;
import co.za.kasi.model.Booking;

public class ReservedBookingsAdapter extends RecyclerView.Adapter<ReservedBookingsAdapter.ViewHolder> {

    private ArrayList<Booking> bookings;
    private Context context;

    private static OnClickListener onClickListener;

    public ReservedBookingsAdapter(Context context, ArrayList<Booking> bookings) {
        this.bookings = bookings;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView date, time, pickupAddress, droppOffAddress, suburb, trackNo ;
        CardView  card;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.tvDateHistory);
            pickupAddress = itemView.findViewById(R.id.tvPickUpAddressHistory);
            droppOffAddress = itemView.findViewById(R.id.tvDropOffAddressHistory);
            trackNo = itemView.findViewById(R.id.tvTrackNumberHistory);
            suburb = itemView.findViewById(R.id.tvSuburbHistory);

            card = itemView.findViewById(R.id.crdCard);
           // onClickListener = (OnClickListener) itemView.getContext();

        }

    }
    @NonNull
    @Override
    public ReservedBookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_history_widget, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReservedBookingsAdapter.ViewHolder holder, int position) {
        Booking booking = bookings.get(position);

      //  holder.trackNo.setText(booking.getTrackNo());
        holder.suburb.setText(" " +booking.getProvince());
        holder.time.setText(booking.getPickUpTime());
        holder.date.setText(booking.getPickUpDate());
        holder.pickupAddress.setText(booking.getPickUpAddress());
        holder.droppOffAddress.setText(booking.getDropOffAddress());

        holder.card.setOnClickListener(v -> { onClickListener.onViewClick(v, bookings.get(position));});

    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener{
        void onViewClick(View button, Booking booking);
        void onCancelClick(View button, Booking booking);
    }
}
