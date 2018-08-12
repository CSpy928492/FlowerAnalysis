package com.example.cspy.floweranalysis;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.cspy.floweranalysis.pojo.Dongtai;
import com.example.cspy.floweranalysis.pojo.User;
import com.example.cspy.floweranalysis.util.HttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    String ak = "ONDUSCNY8laiUakuMFSt3p92bv4bqLck";//&callback=renderReverse
    String sha1 = "A9:63:90:CB:9F:7F:BE:7D:E6:6B:95:70:D2:F1:9F:A4:41:08:03:6C";


    volatile private List<Dongtai> allDongtaiList;
    private User user;

    private JSONObject location;
    private volatile boolean locationValid;

    LocationClient mLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        location = new JSONObject();
        try {
            location.put("x", "39.9");
            location.put("y", "116.38");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mLocationClient = new LocationClient(this);
        mLocationClient.setLocOption(getDefaultLocationClientOption());
        mLocationClient.registerLocationListener(mListener);
        mLocationClient.start();

    }

    private LocationClientOption getDefaultLocationClientOption() {
        LocationClientOption mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(true);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mOption.setOpenGps(true);//可选，默认false，设置是否开启Gps定位
        mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

        return mOption;
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                location = new JSONObject();
                try {
                    location.put("x", bdLocation.getLatitude());
                    location.put("y", bdLocation.getLongitude());
                    locationValid = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public List<Dongtai> getAllDongtaiList() {
        return allDongtaiList;
    }

    public void setAllDongtaiList(List<Dongtai> allDongtaiList) {
        this.allDongtaiList = allDongtaiList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JSONObject getLocation() {
        return location;
    }


    public synchronized boolean isLocationValid() {
        return locationValid;
    }

    public List<Dongtai> getMyDongtaiList() {
        List<Dongtai> myDongtaiList = new ArrayList<>();
        for (Dongtai dongtai : allDongtaiList) {
            if (dongtai.getUserId().equals(user.getUserid())) {
                myDongtaiList.add(dongtai);
            }
        }
        return myDongtaiList;
    }

    public Boolean isInList(Dongtai dongtai) {
        for (Dongtai dt : allDongtaiList) {
            if (dt.getDongtaiId().equals(dongtai.getDongtaiId())) {
                return true;
            }
        }
        return false;
    }

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

                        //转换成可读的形式
                        JSONObject jsonObject = new JSONObject(dongtai.getLocation());
                        String x = jsonObject.getString("x");
                        String y = jsonObject.getString("y");

                        String uri = "http://api.map.baidu.com/geocoder/v2/?ak=" + ak + "&location=" + x + "," + y + "&output=json&pois=0&mcode=" + sha1 + ";com.example.cspy.floweranalysis";
                        HttpConnect locationConnect = new HttpConnect();
                        JSONObject hLocationJSON = locationConnect.getRequest(uri);
                        if (hLocationJSON != null && hLocationJSON.get("status").equals(0)) {
                            dongtai.sethLocation(hLocationJSON.getJSONObject("result").get("formatted_address").toString());
                        } else {
                            dongtai.sethLocation("江苏省");
                        }


                        //提取图片
                        String filepath = json.getString("img");
                        String[] tempstrs = filepath.split("\\\\");
                        //合成图片Url
                        String filename = HttpConnect.getImageUri + tempstrs[tempstrs.length - 1];
                        HttpConnect connect1 = new HttpConnect();
                        byte[] bytes = connect1.getImage(filename);
                        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes));
                        dongtai.setImage(bitmap);
                        dongtais.add(dongtai);
                    }
                    setAllDongtaiList(dongtais);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
