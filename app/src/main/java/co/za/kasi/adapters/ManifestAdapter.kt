package co.za.kasi.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.za.kasi.R
import co.za.kasi.databinding.ManifestItemBinding
import co.za.kasi.model.ManifestDTO

class ManifestAdapter(
    private var items: List<ManifestDTO>
) : RecyclerView.Adapter<ManifestAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<ManifestDTO>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ManifestItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ManifestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tripNoText.text =
            holder.binding.root.context.getString(R.string.trip_number, item.tripNo.toString())
        holder.binding.waybillText.text =
            holder.binding.root.context.getString(
                R.string.manifest_waybills,
                item.totalWaybills.toString()
            )
        holder.binding.parcelsText.text =
            holder.binding.root.context.getString(
                R.string.manifest_parcels,
                item.totalParcels.toString()
            )
    }

    override fun getItemCount(): Int = items.size
}