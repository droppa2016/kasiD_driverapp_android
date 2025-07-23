package co.za.kasi.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


//import co.za.skynet.RecyclerViewAdapter.TiresAdapter;
import co.za.kasi.R;
import co.za.kasi.model.driverOwner.Rates;
//import co.za.skynet.util.Utils;

public class RatesPagerAdapter extends RecyclerView.Adapter<RatesPagerAdapter.ViewHolder> {

    //Utils utils;
    ArrayList<Rates> rates;
    Context context;
    //TiresAdapter adapter;

    public RatesPagerAdapter(ArrayList<Rates> rates, Context context) {
        this.rates = rates;
        this.context = context;
        //utils = new Utils();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rate_structure, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rates rate = rates.get(position);

//        holder.baseAmount.setText("R " + utils.decimalFormater( rate.getRates().getFixedAmount()) );
        holder.branchName.setText(rate.getBranch());
        holder.province.setText(rate.getProvince());
        holder.rateType.setText(rate.getRateType().replace("_"," "));

        //setUpRecyclerView(rate.getRates().getTiers(), holder.tires);
        Log.d("TAG_tier", "onBindViewHolder: " + "Tires sorted" + rate.getRates().getTiers());

    }

    @Override
    public int getItemCount() {
        return rates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        AppCompatTextView branchName, province, rateType, baseAmount;
        RecyclerView tires;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            branchName = itemView.findViewById(R.id.tvBranchName);
            province = itemView.findViewById(R.id.tvProvince);
            rateType = itemView.findViewById(R.id.tvRateType);
            baseAmount = itemView.findViewById(R.id.tvBaseAmount);
            tires = itemView.findViewById(R.id.rcvTiers);
        }
    }


}
