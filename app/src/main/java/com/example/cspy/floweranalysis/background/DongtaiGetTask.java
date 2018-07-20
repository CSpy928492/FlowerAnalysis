package com.example.cspy.floweranalysis.background;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cspy.floweranalysis.MainActivity;
import com.example.cspy.floweranalysis.R;
import com.example.cspy.floweranalysis.pojo.Dongtai;
import com.example.cspy.floweranalysis.util.HttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DongtaiGetTask extends AsyncTask<View, Void, Boolean> {

    View view = null;

    @Override
    protected Boolean doInBackground(View... views) {
        if (views.length == 1) {
            view = views[0];
        }

        MainActivity.dongtaiList.clear();

        HttpConnect connect = new HttpConnect();
        try {
            JSONObject resultJSON = connect.getRequest(HttpConnect.chaxunAllDongtai);
            if (resultJSON != null) {
                Log.e("GetDongtaiTask:", "resultJSON:" + resultJSON.toString());
                JSONArray dongtaiArray = resultJSON.getJSONArray("data");
                for (int i = 0; i < dongtaiArray.length(); i++) {
                    JSONObject json = dongtaiArray.getJSONObject(i);
                    Dongtai dongtai = new Dongtai();
                    dongtai.setContent(json.getString("content"));
                    dongtai.setLocation(json.getString("location"));
                    dongtai.setTime(json.getString("time"));
                    dongtai.setZhiwuName(json.getString("zhiwuname"));
                    dongtai.setUserId(json.getString("userid"));
                    dongtai.setUserName(json.getString("username"));
                    dongtai.setDongtaiId(json.getString("dongtaiid"));


                    String filepath = json.getString("img");
                    String[] tempstrs = filepath.split("\\\\");

                    String filename = HttpConnect.getImageUri + tempstrs[tempstrs.length - 1];
                    HttpConnect connect1 = new HttpConnect();
                    byte[] bytes = connect1.getImage(filename);
                    Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes));
                    dongtai.setImage(bitmap);
                    MainActivity.dongtaiList.add(dongtai);
                }
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean && view != null) {
            Toast.makeText(view.getContext(), "动态刷新成功", Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout layout = (SwipeRefreshLayout) view;
            layout.setRefreshing(false);
        }
    }
}