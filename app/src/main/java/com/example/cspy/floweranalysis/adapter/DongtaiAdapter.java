package com.example.cspy.floweranalysis.adapter;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cspy.floweranalysis.MyApplication;
import com.example.cspy.floweranalysis.R;
import com.example.cspy.floweranalysis.pojo.Dongtai;
import com.example.cspy.floweranalysis.pojo.User;
import com.example.cspy.floweranalysis.util.HttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DongtaiAdapter extends RecyclerView.Adapter<DongtaiAdapter.ViewHolder> {

    List<Dongtai> mDongtaiList;
    User user;
    MyApplication myApplication;

    public DongtaiAdapter(List<Dongtai> dongtaiList, User user, MyApplication myApplication) {
        this.mDongtaiList = new ArrayList<>();
        for (int i = dongtaiList.size() - 1; i >= 0; i--) {
            mDongtaiList.add(dongtaiList.get(i));
        }
        this.user = user;
        this.myApplication = myApplication;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dongtai_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Dongtai dongtai = mDongtaiList.get(position);
        holder.dtUserName.setText(dongtai.getUserName());
        holder.dtContent.setText(dongtai.getContent());
        holder.dtImageView.setImageBitmap(dongtai.getImage());
        holder.dtZhiwuName.setText(dongtai.getZhiwuName());
        holder.dtTime.setText(dongtai.getTime());
        holder.dtLocation.setText(dongtai.getLocation());
        holder.delBtn.setVisibility(dongtai.getUserId().equals(user.getUserid()) ? View.VISIBLE : View.INVISIBLE);
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Dongtai deleteDongtai = mDongtaiList.get(position);
                new DeleteTask().execute(deleteDongtai);

            }
        });
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
        Button delBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            dtUserName = itemView.findViewById(R.id.dongtai_username);
            dtContent = itemView.findViewById(R.id.dongtai_content);
            dtImageView = itemView.findViewById(R.id.dongtai_image);
            dtZhiwuName = itemView.findViewById(R.id.dongtai_zhiwuname);
            dtTime = itemView.findViewById(R.id.dontai_time);
            dtLocation = itemView.findViewById(R.id.dongtai_location);
            delBtn = itemView.findViewById(R.id.dongtai_del_btn);


        }
    }

    public void refreshItem() {
        for (Dongtai dongtai : myApplication.getAllDongtaiList()) {
            if (!containsDongtai(mDongtaiList, dongtai)) {
                mDongtaiList.add(0, dongtai);
            }
        }
    }

    public void refreshMyItem() {
        for (Dongtai dongtai : myApplication.getMyDongtaiList()) {
            if (!containsDongtai(mDongtaiList, dongtai)) {
                mDongtaiList.add(0, dongtai);
            }
        }
    }

    private void deleteConfirm(Dongtai deleted) {
        mDongtaiList.remove(deleted);
        myApplication.getAllDongtaiList().remove(deleted);
        notifyDataSetChanged();
    }

    private Boolean containsDongtai(List<Dongtai> dongtais, Dongtai dongtai) {
        for (Dongtai d : dongtais) {
            if (d.getDongtaiId().equals(dongtai.getDongtaiId())) {
                return true;
            }
        }
        return false;
    }

    class DeleteTask extends AsyncTask<Dongtai, Void, Boolean> {
        Dongtai delDongtai;


        @Override
        protected Boolean doInBackground(Dongtai... dongtais) {
            delDongtai = dongtais[0];

            HttpConnect connect = new HttpConnect();
            try {
                JSONObject resultJSON = connect.getRequest(HttpConnect.delDongtai + "?dongtaiid=" + delDongtai.getDongtaiId());
                if (resultJSON != null) {
                    if (resultJSON.get("msg").equals("1")) {
                        return true;
                    }
                }
                return false;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                deleteConfirm(delDongtai);
            }
        }
    }


}
