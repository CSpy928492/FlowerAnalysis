package com.example.cspy.floweranalysis;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.example.cspy.floweranalysis.pojo.Dongtai;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FujinFragment extends Fragment {

    public static final int CORRECT_LOCATION = 30;
    public static final int WRONG_LOCATION = 31;

    private static final String TAG = "FujinFragment";

    private TextureMapView mapView = null;
    private BaiduMap baiduMap;
    LocationClient mLocationClient;
    Context context;

    Boolean isFirstTime;

    public FujinFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fujin,container,false);

        mapView = (TextureMapView) view.findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();

        isFirstTime = true;


        mLocationClient = new LocationClient(getContext());
        mLocationClient.setLocOption(getDefaultLocationClientOption());
        mLocationClient.registerLocationListener(mListener);
        mLocationClient.start();

        return view;


    }

    public void refreshMarker() {
        List<OverlayOptions> options = new ArrayList<>();
        MyApplication myApplication = (MyApplication) getActivity().getApplication();

        for (Dongtai dongtai : myApplication.getAllDongtai().getDongtaiList()) {
            String location = dongtai.getLocation();
            try {
                JSONObject locationJSON = new JSONObject(location);

                Log.e(TAG, "onCreateView: locationJSON:" + locationJSON.toString());
                DecimalFormat format = new DecimalFormat("0.000000");

                Double x = Double.valueOf(format.format((Double) locationJSON.get("x") + Math.random() / 1000));

                Double y = Double.valueOf(format.format((Double) locationJSON.get("y") + Math.random() / 1000));

                LatLng latLng = new LatLng(x, y);

                View markerView = View.inflate(getActivity(), R.layout.marker_item, null);
                TextView textView = (TextView) markerView.findViewById(R.id.marker_text);
                textView.setBackgroundColor(0x7f888888);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                ImageView imageView = (ImageView) markerView.findViewById(R.id.marker_icon);
                textView.setText(dongtai.getZhiwuName());
                textView.setTextSize(60);
                textView.setTextColor(Color.WHITE);
                imageView.setImageBitmap(dongtai.getImage());

                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(markerView);
                OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(bitmapDescriptor).scaleX(0.3f).scaleY(0.3f); //
                options.add(overlayOptions);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        baiduMap.clear();
        baiduMap.addOverlays(options);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mLocationClient.unRegisterLocationListener(mListener);
        mLocationClient.stop();


    }


    @Override
    public void onResume() {
        mapView.onResume();
        isFirstTime = true;
        super.onResume();
        refreshMarker();

    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        isFirstTime = true;
        refreshMarker();


    }

    private void navigateTo(BDLocation location) {

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        JSONObject loc = new JSONObject();
        try {
            loc.put("x", location.getLatitude());
            loc.put("y", location.getLongitude());
//            MainActivity.setLocation(loc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(20f);
        baiduMap.animateMapStatus(update);
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                if (isFirstTime) {
                    navigateTo(bdLocation);
                    isFirstTime = false;
                }
            }
        }
    };

    public static LocationClientOption getDefaultLocationClientOption() {
        LocationClientOption mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mOption.setOpenGps(true);//可选，默认false，设置是否开启Gps定位
        mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

        return mOption;
    }


}
