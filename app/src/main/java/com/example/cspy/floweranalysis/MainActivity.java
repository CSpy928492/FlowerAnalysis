package com.example.cspy.floweranalysis;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity {

    private int selectedItemId = R.id.item_fujin;

    final int SDK_PERMISSION_REQUEST = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final FujinFragment fujinFragment = new FujinFragment();
        final ShibieFragment shibieFragment = new ShibieFragment();
        final ShezhiFragment shezhiFragment = new ShezhiFragment();


        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, i);
            }
        }



        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment,fujinFragment);
        transaction.commit();

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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
        MyApplication myApplication = (MyApplication) getApplication();

        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myApplication.setPermission_coarse_location(true);
                } else {
                    myApplication.setPermission_coarse_location(false);
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myApplication.setPermission_fine_location(true);
                } else {
                    myApplication.setPermission_fine_location(false);
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myApplication.setPermission_camera(true);
                } else {
                    myApplication.setPermission_camera(false);
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myApplication.setPermission_write_external(true);
                } else {
                    myApplication.setPermission_write_external(false);
                }
                break;

        }
    }

}
