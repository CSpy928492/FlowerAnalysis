package com.example.cspy.floweranalysis;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.baidu.mapapi.SDKInitializer;
import com.example.cspy.floweranalysis.background.DongtaiGetTask;
import com.example.cspy.floweranalysis.pojo.Dongtai;
import com.example.cspy.floweranalysis.pojo.User;
import com.example.cspy.floweranalysis.util.HttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int selectedItemId = R.id.item_fujin;

    final int SDK_PERMISSION_REQUEST = 0;

    public static User user = new User();
    public static JSONObject location;
    public static List<Dongtai> dongtaiList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //权限
        ArrayList<String> permissions = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }


        if (permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
        }




        final FujinFragment fujinFragment = new FujinFragment();
        final ShibieFragment shibieFragment = new ShibieFragment();
        final ShezhiFragment shezhiFragment = new ShezhiFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment,fujinFragment);
        transaction.commit();

        getDongtaiList();






        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                if(item.getItemId() != selectedItemId) {
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();

                    switch (item.getItemId()) {
                        case R.id.item_fujin:
                            transaction.replace(R.id.fragment, fujinFragment);
                            transaction.commit();
                            break;
                        case R.id.item_shibie:
                            transaction.replace(R.id.fragment, shibieFragment);
                            transaction.commit();
                            break;
                        case R.id.item_shezhi:
                            transaction.replace(R.id.fragment, shezhiFragment);
                            transaction.commit();
                            break;
                        default:


                    }
                    selectedItemId = item.getItemId();
                }
                return true;
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void setUser(JSONObject jsonObject) {
        try {
            user.setSex(jsonObject.getString("sex"));
            user.setUserid(jsonObject.getString("userid"));
            user.setUsername(jsonObject.getString("username"));
            user.setPassword(jsonObject.getString("password"));
            user.setUsertel(jsonObject.getString("usertel"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setLocation(JSONObject object) {
        location = object;
    }

    public static List<Dongtai> getMyDongtaiList() {
        List<Dongtai> myDongtaiList = new ArrayList<>();
        String myId = user.getUserid();
        if (dongtaiList != null) {
            for (Dongtai dongtai : dongtaiList) {
                if (dongtai.getUserId().equals(myId)) {
                    myDongtaiList.add(dongtai);
                }
            }
        }
        return myDongtaiList;
    }

    private void getDongtaiList() {
        DongtaiGetTask getTask = new DongtaiGetTask();
        getTask.execute();
    }


}
