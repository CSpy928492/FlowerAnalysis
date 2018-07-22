package com.example.cspy.floweranalysis;

import android.app.Application;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.cspy.floweranalysis.pojo.Dongtai;
import com.example.cspy.floweranalysis.pojo.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MyApplication extends Application {


    private List<Dongtai> allDongtaiList;
    private User user;

    private JSONObject location;
    private boolean locationValid;

    private boolean permission_camera;
    private boolean permission_write_external;
    private boolean permission_fine_location;
    private boolean permission_coarse_location;

    LocationClient mLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        location = new JSONObject();
        try {
            location.put("x", "116.38");
            location.put("y", "39.9");
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
                try {
                    location = new JSONObject();
                    location.put("x", bdLocation.getLatitude() + "");
                    location.put("y", bdLocation.getLongitude() + "");
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


    public boolean isPermission_camera() {
        return permission_camera;
    }

    public void setPermission_camera(boolean permission_camera) {
        this.permission_camera = permission_camera;
    }

    public boolean isPermission_write_external() {
        return permission_write_external;
    }

    public void setPermission_write_external(boolean permission_write_external) {
        this.permission_write_external = permission_write_external;
    }

    public boolean isPermission_fine_location() {
        return permission_fine_location;
    }

    public void setPermission_fine_location(boolean permission_fine_location) {
        this.permission_fine_location = permission_fine_location;
    }

    public boolean isPermission_coarse_location() {
        return permission_coarse_location;
    }

    public void setPermission_coarse_location(boolean permission_coarse_location) {
        this.permission_coarse_location = permission_coarse_location;
    }

    public boolean isLocationValid() {
        return locationValid;
    }
}
