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

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.ViewHolder> {

    private ArrayList<Booking> bookings;
    private Context context;

    public static OnItemCLickListener onItemCLickListener;

    public AddressesAdapter(Context context, ArrayList<Booking> bookings) {
        this.bookings = bookings;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView desc, address;
        CardView card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            desc = itemView.findViewById(R.id.txtActivitype);
            address = itemView.findViewById(R.id.txtActivityAddress);
            card = itemView.findViewById(R.id.crdCurrentBooking);
            card.setVisibility(View.VISIBLE);

        }
    }
    @NonNull
    @Override
    public AddressesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_pick_ups_widget, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AddressesAdapter.ViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        int pos = position + 1;
        holder.desc.setText("Address " + pos);

        if(booking.isItemPicked()){
            holder.address.setText(booking.getDropOffAddress());
            holder.card.setCardBackgroundColor(context.getColor(R.color.skynet_red));
        }else {
            holder.address.setText(booking.getPickUpAddress());
            holder.card.setCardBackgroundColor(context.getColor(R.color.skynet_color));
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemCLickListener.bockingClick(holder.card, bookings.get(position));
            }
        });

    }

    public void setOnItemCLickListener(OnItemCLickListener onItemCLickListener) {
        this.onItemCLickListener = onItemCLickListener;
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public interface OnItemCLickListener{
        public View.OnClickListener bockingClick(CardView card, Booking booking);
    }
}
