package co.za.kasi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.za.kasi.model.superApp.a.waybillData.ScanParcel

class DeliveredParcelAdapter(private val items: List<ScanParcel>) :
    RecyclerView.Adapter<DeliveredParcelAdapter.DeliveredParcelAdapter>() {

    inner class DeliveredParcelAdapter(private val view: TextView) : RecyclerView.ViewHolder(view) {
        fun bind(parcel: ScanParcel) {
            view.text = parcel.parcel_number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveredParcelAdapter {
        val textView = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        return DeliveredParcelAdapter(textView)
    }

    override fun onBindViewHolder(
        holder: DeliveredParcelAdapter,
        position: Int
    ) {
        holder.bind(items[position])
    }


    override fun getItemCount() = items.size
}