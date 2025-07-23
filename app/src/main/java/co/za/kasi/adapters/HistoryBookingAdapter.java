package co.za.kasi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import co.za.kasi.R;

import co.za.kasi.model.Booking;
import co.za.kasi.services.RecyclerViewInterface;

public class HistoryBookingAdapter extends RecyclerView.Adapter<HistoryBookingAdapter.BookingViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    private List<Booking> bookings;
    private Context context;

    public HistoryBookingAdapter(RecyclerViewInterface recyclerViewInterface, List<Booking> bookings, Context context) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.bookings = bookings;
        this.context = context;
    }


    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        v = layoutInflater.inflate(R.layout.booking_history_widget,parent,false);

        return new BookingViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {

        holder.trackNo.setText(bookings.get(position).getTrackNo());
        holder.suburb.setText(bookings.get(position).getProvince());
        holder.bookingTime.setText(bookings.get(position).getPickUpTime());
        holder.bookingDate.setText(bookings.get(position).getPickUpDate());
        holder.pickUpAddress.setText(bookings.get(position).getPickUpAddress());
        holder.dropOffAddress.setText(bookings.get(position).getDropOffAddress());


    }

    @Override
    public int getItemCount() {

        return bookings.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder{

        public MaterialTextView trackNo,pickUpAddress,dropOffAddress,bookingTime,bookingDate,suburb;

        public BookingViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            this.trackNo = itemView.findViewById(R.id.tvTrackNumberHistory);
            this.suburb = itemView.findViewById(R.id.tvSuburbHistory);
            this.bookingDate = itemView.findViewById(R.id.tvDateHistory);
            this.pickUpAddress = itemView.findViewById(R.id.tvPickUpAddressHistory);
            this.dropOffAddress = itemView.findViewById(R.id.tvDropOffAddressHistory);


        }
    }



}
