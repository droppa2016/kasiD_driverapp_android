package co.za.kasi.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AutoCompleteAdapter extends RecyclerView.Adapter<AutoCompleteAdapter.PlacesLocationHolder> implements Filterable {


    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public PlacesLocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesLocationHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }



    public class PlacesLocationHolder extends RecyclerView.ViewHolder implements View

            .OnClickListener {

        private TextView suburb;
        private LinearLayout mRow;

        public PlacesLocationHolder(@NonNull View itemView) {
            super(itemView);
            // suburb = itemView.findViewById(R.id.)
        }

        @Override
        public void onClick(View v) {

        }
    }
}
