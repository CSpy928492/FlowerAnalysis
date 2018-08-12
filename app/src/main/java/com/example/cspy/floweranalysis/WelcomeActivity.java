package com.example.cspy.floweranalysis;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
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

import static com.example.cspy.floweranalysis.LoginActivity.CORRECT_STATE;

public class WelcomeActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView task1, task2, task3;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Intent intent;
    int i = 0;

    final int mRequestCode = 1;
    String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    List<String> permissionList = new ArrayList<>();

    volatile Boolean already = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_welcome);

        Bitmap welcomeBackground = BitmapFactory.decodeResource(getResources(), R.drawable.welcome);
        ImageView imageView = (ImageView) findViewById(R.id.imageview_welcome);
        imageView.setImageBitmap(welcomeBackground);

        progressBar = (ProgressBar) findViewById(R.id.progressbar_welcome);
        task1 = (TextView) findViewById(R.id.task_1);
        task1.setBackgroundColor(getResources().getColor(R.color.normal_color, null));
        task2 = (TextView) findViewById(R.id.task_2);
        task2.setBackgroundColor(getResources().getColor(R.color.normal_color, null));
        task3 = (TextView) findViewById(R.id.task_3);
        task3.setBackgroundColor(getResources().getColor(R.color.normal_color, null));

        //获取权限
        permissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[i]);
            }
        }
        if (permissionList.size() > 0) {
            String[] permission = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permission, mRequestCode);
        } else {
            already = true;
        }
        new InitTask().execute();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        already = true;
    }




    class InitTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this);
            String usertel = sharedPreferences.getString("usertel", null);
            String password = sharedPreferences.getString("password", null);

            MyApplication myApplication = (MyApplication) getApplication();


            // 用户登陆相关
            // CORRECT_STATE    表示都存在且正常
            // WITHOUT_USERTEL  表示缺少用户名/无登陆信息（黄色）
            // WITHOUT_PASSWORD 表示缺少密码
            // WRONG_PASSWORD   表示密码错误
            if (TextUtils.isEmpty(usertel)) {
                intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                intent.putExtra("state", LoginActivity.WITHOUT_USERTEL);

                editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                publishProgress(LoginActivity.WITHOUT_USERTEL);
            } else {
                if (TextUtils.isEmpty(password)) {
                    intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    intent.putExtra("state", LoginActivity.WITHOUT_PASSWORD);
                    intent.putExtra("usertel", usertel);

                    publishProgress(LoginActivity.WITHOUT_PASSWORD);
                } else {

                    HttpConnect httpConnect = new HttpConnect();
                    String fix = "?usertel=" + usertel;
                    fix += "&password=" + password;
                    try {
                        JSONObject json = httpConnect.getRequest(HttpConnect.loginUri + fix);
                        //用户登陆成功    直接跳过登陆界面
                        if (json != null && json.get("msg").equals("1")) {

                            JSONObject userObject = json.getJSONObject("data");
                            User user = new User();
                            user.setSex(userObject.getString("sex"));
                            user.setUserid(userObject.getString("userid"));
                            user.setUsername(userObject.getString("username"));
                            user.setPassword(userObject.getString("password"));
                            user.setUsertel(userObject.getString("usertel"));
                            myApplication.setUser(user);
                            intent = new Intent(WelcomeActivity.this, MainActivity.class);

                            publishProgress(CORRECT_STATE);
                        } else {
                            //密码错误
                            intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                            intent.putExtra("state", LoginActivity.WRONG_PASSWORD);
                            intent.putExtra("usertel", usertel);

                            editor = sharedPreferences.edit();
                            editor.remove("password");
                            editor.apply();

                            publishProgress(LoginActivity.WRONG_PASSWORD);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            //与服务器连接，保存全部动态
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
                            String uri = "http://api.map.baidu.com/geocoder/v2/?ak=" + myApplication.ak + "&location=" + x + "," + y + "&output=json&pois=0&mcode=" + myApplication.sha1 + ";com.example.cspy.floweranalysis";
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
                        myApplication.setAllDongtaiList(dongtais);
                        publishProgress(DongtaiActivity.ALL_DONGTAI_SUCCESS);
                    } else {
                        publishProgress(DongtaiActivity.ALL_DONGTAI_FAIL);
                    }
                } else {
                    publishProgress(DongtaiActivity.ALL_DONGTAI_FAIL);
                }
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress(DongtaiActivity.ALL_DONGTAI_FAIL);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //获取本机位置
            int i = 10;
            //等待定位2秒钟
            while (true) {
                if (myApplication.isLocationValid()) {
                    publishProgress(FujinFragment.CORRECT_LOCATION);
                    break;
                }
                if (i < 0) {
                    break;
                }
                i--;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (i < 0) {
                publishProgress(FujinFragment.WRONG_LOCATION);
            }

            while (true) {
                try {
                    Thread.sleep(300);
                    if (already) {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            finish();
            startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int value = values[0];

            switch (value) {
                case LoginActivity.CORRECT_STATE:
                    task1.setBackgroundColor(getResources().getColor(R.color.success_color, null));
                    break;
                case LoginActivity.WITHOUT_PASSWORD:
                case LoginActivity.WRONG_PASSWORD:
                case LoginActivity.WITHOUT_USERTEL:
                    task1.setBackgroundColor(getResources().getColor(R.color.error_color, null));
                    break;
                case DongtaiActivity.ALL_DONGTAI_FAIL:
                    task2.setBackgroundColor(getResources().getColor(R.color.error_color, null));
                    break;
                case DongtaiActivity.ALL_DONGTAI_SUCCESS:
                    task2.setBackgroundColor(getResources().getColor(R.color.success_color, null));
                    break;
                case FujinFragment.CORRECT_LOCATION:
                    task3.setBackgroundColor(getResources().getColor(R.color.success_color, null));
                    break;
                case FujinFragment.WRONG_LOCATION:
                    task3.setBackgroundColor(getResources().getColor(R.color.error_color, null));
                    break;
            }
            super.onProgressUpdate(values);
        }
    }

}
