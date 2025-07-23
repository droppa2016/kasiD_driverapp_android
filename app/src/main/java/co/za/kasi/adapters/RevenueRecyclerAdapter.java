package co.za.kasi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


//import co.za.skynet.RecyclerViewAdapter.ReportAdapter;
import co.za.kasi.R;
import co.za.kasi.model.SkynetReportDTO;
import co.za.kasi.model.driverOwner.ParcelBillingDTO;
import co.za.kasi.model.driverOwner.RatePerRouteBilling;
//import co.za.skynet.util.Utils;

public class RevenueRecyclerAdapter extends RecyclerView.Adapter<RevenueRecyclerAdapter.ViewHolder> {

    ArrayList<RatePerRouteBilling> rates;
    Context context;
    ReportAdapter adapter;
    private ArrayList<RatePerRouteBilling> ratePerRouteBillings;
    private ArrayList<ParcelBillingDTO> parcelBillingDTOS;
    private ArrayList<SkynetReportDTO> records;

    private double mtdRouteTotal = 0.0;

    public RevenueRecyclerAdapter(ArrayList<RatePerRouteBilling> rates, Context context) {
        this.rates = rates;
        this.context = context;
        init();
        
    }
    
    private void init(){
        //utils = new Utils();
        ratePerRouteBillings = new ArrayList<>();
        parcelBillingDTOS = new ArrayList<>();
        records = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_revenue_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RatePerRouteBilling rate = rates.get(position);
        records.clear();
        parcelBillingDTOS = (ArrayList<ParcelBillingDTO>) rate.getParcels();
        holder.routeName.setText(rate.getRoute());

        setupRecyclerviewList(holder.report, (ArrayList<ParcelBillingDTO>) rate.getParcels());
        //calculateProfit(holder.report, rate);

    }

    @Override
    public int getItemCount() {
        return rates.size();
    }


   /* private void calculateProfit(RecyclerView recyclerView, RatePerRouteBilling rate){

        double mtdRouteTotal = 0.0;
        records.add(new SkynetReportDTO("Description", "", "Amount" ));
        if(rate.getFixedAmount() > 0){
            records.add(new SkynetReportDTO("Base amount", "", "R " + utils.decimalFormater(rate.getFixedAmount()) ));
        }
        if(rate.getFuelCharge() > 0){
            records.add(new SkynetReportDTO("Fuel Surcharge", "", "R " + utils.decimalFormater(rate.getFuelCharge()) ));
        }
        if(rate.getTotalTiers() > 0){
            records.add(new SkynetReportDTO("Tier amount", "", "R " + utils.decimalFormater(rate.getTotalTiers())));
        }
      *//*  if(rate.getFixedAmount() > 0){
            records.add(new SkynetReportDTO("Base amount", "", "R " + utils.decimalFormater(rate.getFixedAmount()) ));
        }*//*

        mtdRouteTotal = rate.getFixedAmount() + rate.getTotalTiers() + rate.getFuelCharge();
        records.add(new SkynetReportDTO("Sub Total ", "", "R " + utils.decimalFormater(mtdRouteTotal)));

        Log.e("TAG_parcel", "calculateProfit: ==========================================================");
        Log.e("TAG_parcel", parcelBillingDTOS.toString());
        Log.e("TAG_parcel", "calculateProfit: ==========================================================");

        adapter = new ReportAdapter(records);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

    }*/

    private void setupRecyclerviewList(RecyclerView recyclerView, ArrayList<ParcelBillingDTO> list) {

        adapter = new ReportAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private AppCompatTextView routeName;
        private RecyclerView report;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            routeName = itemView.findViewById(R.id.tvRouteName);
            report = itemView.findViewById(R.id.rcvRevenueRecords);
        }
    }
}
