package co.za.kasi.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.za.kasi.databinding.SkyRatesTiersItemBinding
import co.za.kasi.model.Tier


class SkyRatesTierAdapter(
    private var tiers: List<Tier>? = null
) : RecyclerView.Adapter<SkyRatesTierAdapter.TierViewHolder>() {

    inner class TierViewHolder(private val binding: SkyRatesTiersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tier: Tier) {
            binding.tier = tier
            binding.executePendingBindings()
        }
    }

    fun setTiers(tiers: List<Tier>) {
        this.tiers = tiers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TierViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SkyRatesTiersItemBinding.inflate(inflater, parent, false)
        return TierViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TierViewHolder, position: Int) {
        holder.bind(tiers?.get(position) ?: Tier(0, 0, 0.0))
    }

    override fun getItemCount(): Int = tiers?.size ?: 0
}