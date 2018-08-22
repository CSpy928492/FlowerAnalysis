package com.example.cspy.floweranalysis;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.cspy.floweranalysis.pojo.Dongtai;
import com.example.cspy.floweranalysis.pojo.DongtaiList;
import com.example.cspy.floweranalysis.pojo.User;
import com.example.cspy.floweranalysis.util.HttpConnect;
import com.example.cspy.floweranalysis.util.LocationControl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MyApplication extends Application {

    private static final String TAG = "MyApplication";


    private DongtaiList allDongtai;
    private User user;
    private LocationControl locationControl;



    @Override
    public void onCreate() {
        super.onCreate();

        allDongtai = new DongtaiList();
        locationControl = new LocationControl(this);

    }


    public DongtaiList getAllDongtai() {
        return allDongtai;
    }

    public void setAllDongtai(DongtaiList allDongtai) {
        this.allDongtai = allDongtai;
    }

    public LocationControl getLocationControl() {
        return locationControl;
    }

    public void setLocationControl(LocationControl locationControl) {
        this.locationControl = locationControl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void initDongtai() throws IOException, JSONException {
        //初始化动态列表并初始化所有动态（设置位置并且获取网络图片），只应使用一次
        HttpConnect connect = new HttpConnect();
        JSONObject resultJSON = connect.getRequest(HttpConnect.chaxunAllDongtai);
        if (resultJSON != null) {
            //返回成功
            if (resultJSON.get("msg").equals("1")) {
                JSONArray dongtaiArray = resultJSON.getJSONArray("data");
                ArrayList<Dongtai> dongtais = new ArrayList<>();
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
                    //提取图片
                    String filepath = json.getString("img");
                    String[] tempstrs = filepath.split("\\\\");
                    //合成图片Url
                    String filename = HttpConnect.getImageUri + tempstrs[tempstrs.length - 1];
                    dongtai.setBitmapUri(filename);
                    dongtais.add(dongtai);
                }
                initDongtaiList(dongtais);
                allDongtai.setDongtaiList(dongtais);
            }
        }

    }

    //根据List设置位置和图片
    private void initDongtaiList(List<Dongtai> dongtaiList) throws IOException, JSONException {
        for (Dongtai dongtai : dongtaiList) {
            //转换成可读的形式
            dongtai.sethLocation(locationControl.getAddressByLocation(new JSONObject(dongtai.getLocation())));

            HttpConnect connect1 = new HttpConnect();
            byte[] bytes = connect1.getImage(dongtai.getBitmapUri());
            Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes));
            dongtai.setImage(bitmap);
        }
    }

    //刷新动态列表，并且只初始化新动态
    public void refreshDongtai() {
        HttpConnect connect = new HttpConnect();
        try {
            JSONObject resultJSON = connect.getRequest(HttpConnect.chaxunAllDongtai);
            if (resultJSON != null) {
                //返回成功
                if (resultJSON.get("msg").equals("1")) {
                    JSONArray dongtaiArray = resultJSON.getJSONArray("data");
                    ArrayList<Dongtai> dongtais = new ArrayList<>();
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
                        //提取图片
                        String filepath = json.getString("img");
                        String[] tempstrs = filepath.split("\\\\");
                        //合成图片Url
                        String filename = HttpConnect.getImageUri + tempstrs[tempstrs.length - 1];
                        dongtai.setBitmapUri(filename);
                        dongtais.add(dongtai);
                    }
                    //只初始化新加入的动态
                    initDongtaiList(allDongtai.addNewAndRemoveDeleted(new DongtaiList(dongtais)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
