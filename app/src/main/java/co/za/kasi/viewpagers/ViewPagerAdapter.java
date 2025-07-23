package co.za.kasi.viewpagers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;


import co.za.kasi.R;
import co.za.kasi.model.systemDTO.ViewPagerDTO;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>{

    private ArrayList<ViewPagerDTO> list;
    private Context context;
    private View view;

    public ViewPagerAdapter(ArrayList<ViewPagerDTO> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onboarding_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {

        ViewPagerDTO dto = list.get(position);

        holder.title.setText(dto.getTitle());
        holder.context.setText(dto.getContext());
        holder.imageview.setImageDrawable(dto.getIllustration());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView title, context;
        AppCompatImageView imageview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtTitle);
            context = itemView.findViewById(R.id.txtContext);
            imageview = itemView.findViewById(R.id.illustrationImage);
        }
    }
}
