package com.example.cspy.floweranalysis.util;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LocationControl {

    String ak = "ONDUSCNY8laiUakuMFSt3p92bv4bqLck";//&callback=renderReverse
    String sha1 = "A9:63:90:CB:9F:7F:BE:7D:E6:6B:95:70:D2:F1:9F:A4:41:08:03:6C";

    JSONObject location;
    LocationClient mLocationClient;
    Context context;
    private volatile boolean locationValid;


    public LocationControl(Context context) {
        this.context = context;
        location = new JSONObject();
        try {
            location.put("x", "39.9");
            location.put("y", "116.38");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initLocation();
    }


    private void initLocation() {
        mLocationClient = new LocationClient(context);
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

    public String getAddressByLocation(JSONObject locationJSON) throws IOException, JSONException {
        String uri = "http://api.map.baidu.com/geocoder/v2/?ak=" + ak + "&location=" + locationJSON.get("x") + "," + locationJSON.get("y")
                + "&output=json&pois=0&mcode=" + sha1 + ";com.example.cspy.floweranalysis";
        HttpConnect locationConnect = new HttpConnect();
        JSONObject hLocationJSON = locationConnect.getRequest(uri);
        if (hLocationJSON != null && hLocationJSON.get("status").equals(0)) {
            return hLocationJSON.getJSONObject("result").get("formatted_address").toString();
        } else {
            return "获取位置错误";
        }
    }

    public boolean isLocationValid() {
        return locationValid;
    }

    public JSONObject getLocation() {
        return location;
    }
}
