package co.za.kasi.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.za.kasi.databinding.SkyReportsRevenueItemBinding
import co.za.kasi.model.Trip

class SkyReportsRevenueAdapter(
    private var trips: List<Trip> = emptyList()
) : RecyclerView.Adapter<SkyReportsRevenueAdapter.TripViewHolder>() {

    inner class TripViewHolder(private val binding: SkyReportsRevenueItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: Trip) {
            binding.trip = trip
            binding.executePendingBindings()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTrips(trips: List<Trip>) {
        this.trips = trips
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SkyReportsRevenueItemBinding.inflate(inflater, parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(trips[position])
    }

    override fun getItemCount(): Int = trips.size
}