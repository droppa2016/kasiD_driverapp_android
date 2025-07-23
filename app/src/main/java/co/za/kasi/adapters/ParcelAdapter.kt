package co.za.kasi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.za.kasi.R
import co.za.kasi.databinding.ItemHeaderBinding
import co.za.kasi.databinding.ItemParcelBinding
import co.za.kasi.model.superApp.a.waybillData.ScanParcel
import co.za.kasi.model.superApp.a.waybillData.WaybillListItem

class ParcelAdapter( private val items: MutableList<WaybillListItem>,   private val onUnscanClicked: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            TYPE_PARCEL -> {
                val binding = ItemParcelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ParcelViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is WaybillListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is WaybillListItem.ParcelItem -> (holder as ParcelViewHolder).bind(item.scanParcel)

        }
    }


    override fun getItemCount(): Int = items.size

    fun updateParcelScanStatus(parcelBarcode: String) {
        items.forEachIndexed { index, item ->
            if (item is WaybillListItem.ParcelItem && item.scanParcel.parcel_number == parcelBarcode) {
                item.scanParcel.scanned = true
                notifyItemChanged(index)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is WaybillListItem.Header -> TYPE_HEADER
            is WaybillListItem.ParcelItem -> TYPE_PARCEL
        }
    }

    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: WaybillListItem.Header) {
            binding.waybillNumber.text = header.waybillNumber
        }
    }

    inner class ParcelViewHolder(private val binding: ItemParcelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parcel: ScanParcel) {
            binding.parcelNumber.text = parcel.parcel_number

            binding.parcelNumber.setTextColor(
                if (parcel.scanned) ContextCompat.getColor(itemView.context, R.color.green) else ContextCompat.getColor(itemView.context, R.color.black)
            )

            binding.tickIcon.visibility = if (parcel.scanned) View.VISIBLE else View.GONE

          //  binding.unscanIcon.visibility = if (parcel.scanned) View.VISIBLE else View.GONE

            binding.unscanIcon.setOnClickListener {
                onUnscanClicked(parcel.parcel_number)
            }
        }
    }

    fun unscanParcel(parcelBarcode: String) {
        items.forEachIndexed { index, item ->
            if (item is WaybillListItem.ParcelItem && item.scanParcel.parcel_number == parcelBarcode) {
                item.scanParcel.scanned = false
                notifyItemChanged(index)
            }
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PARCEL = 1
    }
}
