package co.za.kasi.adapters

import android.widget.Filter
import android.widget.Filterable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.za.kasi.databinding.BookingHistoryWidgetBinding
import co.za.kasi.model.superApp.a.waybillData.Waybills

class WaybillzAdapter(private var waybills: List<Waybills> = emptyList(),
                     private val onWaybillClick: (Waybills) -> Unit) :
    RecyclerView.Adapter<WaybillzAdapter.WaybillViewHolder>(), Filterable {

    private var filteredList: List<Waybills> = waybills
    private var fullList: List<Waybills> = waybills

    inner class WaybillViewHolder(private val binding: BookingHistoryWidgetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(waybill: Waybills) {
            binding.apply {
                tvTrackNumberHistory.text = waybill.number
                tvSuburbHistory.text = waybill.serviceType
                tvPickUpAddressHistory.text = waybill.sender?.addressLine1
                tvDropOffAddressHistory.text = "${waybill.consignee?.addressLine1}, ${waybill.consignee?.addressLine2}, ${waybill.consignee?.addressLine3}, ${waybill.consignee?.town}, ${waybill.consignee?.postalCode} "
                tvDateHistory.text = waybill.deliveryDate?.substring(0,10)
                tvTime.text = waybill.deliveryDate?.substring(11,16)
                if (waybill.serviceType?.equals("COU") == true){
                    storeNameLayout.visibility = View.VISIBLE
                    storeNametxt.text = "Store name : ${waybill.consignee?.addressLine1}"
                }else{
                    storeNameLayout.visibility = View.GONE
                }

                binding.root.setOnClickListener {
                    onWaybillClick(waybill)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaybillViewHolder {
        val binding = BookingHistoryWidgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WaybillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WaybillViewHolder, position: Int) {
        holder.bind(waybills[position]!!)
    }

    override fun getItemCount(): Int = waybills.size

    public fun setItemList(list : List<Waybills>){
        this.waybills = list
        notifyDataSetChanged()
    }

    fun updateWaybills(newWaybills: List<Waybills>) {
        waybills = newWaybills
        fullList = newWaybills
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }
}