package co.za.kasi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.za.kasi.databinding.ItemParcelBinding

class ScannedWaybillsAdapter(private val waybills: List<String>) :
    RecyclerView.Adapter<ScannedWaybillsAdapter.WaybillViewHolder>() {

    inner class WaybillViewHolder(private val binding: ItemParcelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parcel: String) {
            binding.parcelNumber.text = parcel
            binding.unscanIcon.visibility = View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaybillViewHolder {
        val binding = ItemParcelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WaybillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WaybillViewHolder, position: Int) {
        holder.bind(waybills[position])
    }

    override fun getItemCount(): Int = waybills.size
}