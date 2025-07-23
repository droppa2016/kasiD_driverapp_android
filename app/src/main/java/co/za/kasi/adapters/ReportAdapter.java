package co.za.kasi.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import co.za.kasi.R;
import co.za.kasi.model.driverOwner.ParcelBillingDTO;

/**
 * created by {Andries Sebola} on {1/23/2023}.
 */
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private View view;
    private ArrayList<ParcelBillingDTO> record;
    boolean collapsed = false;
    private CollapseList collapse;
    int listSize = 8;
    Context context;

    public ReportAdapter(ArrayList<ParcelBillingDTO> record) {
        this.record = record;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.skynet_report_item, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView cal1, cal2, cal3;
        private TableRow row;

        public ViewHolder(@NonNull View view) {
            super(view);

            cal1 = view.findViewById(R.id.trCal1);
            cal2 = view.findViewById(R.id.trCal2);
            cal3 = view.findViewById(R.id.trCal3);
            row = view.findViewById(R.id.trParent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParcelBillingDTO reportDTO = record.get(position);

            /*String caps = reportDTO.getCal1().substring(0,1).toUpperCase();
            String initCapitalize = caps + reportDTO.getCal1().substring(1).toLowerCase();*/
        holder.cal1.setText("reportDTO.");
        holder.cal2.setText("");
        holder.cal3.setText("R " + reportDTO.getAmountDue());
        holder.row.setPadding(140, 0, 0, 0);
        if (position == 0) {
            //holder.row.setBackgroundColor(context.getColor(R.color.skynet_color_light));
            //holder.row.setBackground(context.getDrawable(R.drawable.border_dashed_style));
            holder.cal1.setTextColor(context.getColor(R.color.quantum_black_100));
            holder.cal2.setTextColor(context.getColor(R.color.quantum_black_100));
            holder.cal3.setTextColor(context.getColor(R.color.quantum_black_100));
            holder.cal1.setTextSize(13);
            holder.cal2.setTextSize(13);
            holder.cal3.setTextSize(13);

            holder.cal1.setTypeface(null, Typeface.BOLD);
            holder.cal3.setTypeface(null, Typeface.BOLD);

        }


        if (position == record.size() - 1) {
            //holder.row.setBackgroundColor(context.getColor(R.color.skynet_color_light));
            //holder.row.setBackground(context.getDrawable(R.drawable.border_dashed_style));
            holder.cal1.setTextColor(context.getColor(R.color.quantum_white_100));
            holder.cal3.setTextColor(context.getColor(R.color.quantum_white_100));
            holder.cal1.setTextSize(14);
            holder.cal3.setTextSize(14);
            holder.row.setBackgroundColor(ContextCompat.getColor(context, R.color.skynet_color));

            holder.row.setPadding(140, 25, 0, 25);
            holder.cal1.setTypeface(null, Typeface.BOLD);
            holder.cal3.setTypeface(null, Typeface.BOLD);
        }

    }

    @Override
    public int getItemCount() {
        return record.size();
    }

    public interface CollapseList {
        void onClick();
    }

}
