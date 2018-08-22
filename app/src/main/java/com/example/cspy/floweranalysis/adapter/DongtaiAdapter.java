package com.example.cspy.floweranalysis.adapter;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cspy.floweranalysis.DongtaiActivity;
import com.example.cspy.floweranalysis.MyApplication;
import com.example.cspy.floweranalysis.R;
import com.example.cspy.floweranalysis.pojo.Dongtai;
import com.example.cspy.floweranalysis.pojo.DongtaiList;
import com.example.cspy.floweranalysis.pojo.User;
import com.example.cspy.floweranalysis.util.HttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DongtaiAdapter extends RecyclerView.Adapter<DongtaiAdapter.ViewHolder> {

    DongtaiList dongtaiList;
    User user;
    MyApplication myApplication;
    DongtaiActivity dongtaiActivity;

    public DongtaiAdapter(User user, MyApplication myApplication, DongtaiActivity dongtaiActivity) {
        dongtaiList = myApplication.getAllDongtai().clone();
        dongtaiList.reverse();

        this.user = user;
        this.myApplication = myApplication;
        this.dongtaiActivity = dongtaiActivity;
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
        Dongtai dongtai = dongtaiList.getDongtaiList().get(position);
        String content = TextUtils.isEmpty(dongtai.getContent()) ? "请欣赏图片" : dongtai.getContent();
        holder.dtContent.setText(dongtai.getUserName() + " 说：“" + content + "”");
        holder.dtImageView.setImageBitmap(dongtai.getImage());
        holder.dtDongtaiTitle.setText("鉴定结果：" + dongtai.getZhiwuName());
        holder.dtTime.setText(getTime(dongtai.getTime()));
        holder.dtLocation.setText(dongtai.gethLocation());
        holder.delBtn.setVisibility(dongtai.getUserId().equals(user.getUserid()) ? View.VISIBLE : View.INVISIBLE);
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Dongtai deleteDongtai = dongtaiList.getDongtaiList().get(position);
                new DeleteTask().execute(deleteDongtai);
            }
        });
    }

    private String getTime(String time) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateformat.format(Double.parseDouble(time));
    }




    @Override
    public int getItemCount() {
        return dongtaiList.getDongtaiList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dtContent;
        ImageView dtImageView;
        TextView dtDongtaiTitle;
        TextView dtTime;
        TextView dtLocation;
        ImageButton delBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            dtContent = (TextView) itemView.findViewById(R.id.dongtai_content);
            dtImageView = (ImageView) itemView.findViewById(R.id.dongtai_image);
            dtDongtaiTitle = (TextView) itemView.findViewById(R.id.dongtai_title);
            dtTime = (TextView) itemView.findViewById(R.id.dongtai_time);
            dtLocation = (TextView) itemView.findViewById(R.id.dongtai_location);
            delBtn = (ImageButton) itemView.findViewById(R.id.dongtai_del_btn);
        }
    }

    public void refreshItem() {
        myApplication.refreshDongtai();
        dongtaiList.reverse();
        dongtaiList.addNewAndRemoveDeleted(myApplication.getAllDongtai());
        dongtaiList.reverse();
    }

    public void refreshMyItem() {
        myApplication.refreshDongtai();
        DongtaiList myDongtaiList = new DongtaiList(myApplication.getAllDongtai().getDongtaiListByUID(user.getUserid()));
        dongtaiList.reverse();
        dongtaiList.addNewAndRemoveDeleted(myDongtaiList);
        dongtaiList.reverse();
    }

    private void deleteConfirm(Dongtai deleted) {
        dongtaiList.getDongtaiList().remove(deleted);
        myApplication.getAllDongtai().getDongtaiList().remove(deleted);
        notifyDataSetChanged();
    }


//    private Boolean containsDongtai(List<Dongtai> dongtais, Dongtai dongtai) {
//        for (Dongtai d : dongtais) {
//            if (d.getDongtaiId().equals(dongtai.getDongtaiId())) {
//                return true;
//            }
//        }
//        return false;
//    }

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
                dongtaiActivity.refreshLayout();
            }
        }
    }


}
