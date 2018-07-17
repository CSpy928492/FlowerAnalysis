package com.example.cspy.floweranalysis.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cspy.floweranalysis.R;
import com.example.cspy.floweranalysis.pojo.Dongtai;

import java.util.List;

public class DongtaiAdapter extends RecyclerView.Adapter<DongtaiAdapter.ViewHolder> {

    List<Dongtai> mDongtaiList;

    public DongtaiAdapter(List<Dongtai> dongtaiList) {
        this.mDongtaiList = dongtaiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dongtai_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dongtai dongtai = mDongtaiList.get(position);
        holder.dtUserName.setText(dongtai.getUserName());
        holder.dtContent.setText(dongtai.getContent());
        holder.dtImageView.setImageBitmap(dongtai.getImage());
        holder.dtZhiwuName.setText(dongtai.getZhiwuName());
        holder.dtTime.setText(dongtai.getTime());
        holder.dtLocation.setText(dongtai.getLocation());


    }

    @Override
    public int getItemCount() {
        return mDongtaiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dtUserName;
        TextView dtContent;
        ImageView dtImageView;
        TextView dtZhiwuName;
        TextView dtTime;
        TextView dtLocation;

        public ViewHolder(View itemView) {
            super(itemView);

            dtUserName = itemView.findViewById(R.id.dongtai_username);
            dtContent = itemView.findViewById(R.id.dongtai_content);
            dtImageView = itemView.findViewById(R.id.dongtai_image);
            dtZhiwuName = itemView.findViewById(R.id.dongtai_zhiwuname);
            dtTime = itemView.findViewById(R.id.dontai_time);
            dtLocation = itemView.findViewById(R.id.dongtai_location);

        }
    }
}
