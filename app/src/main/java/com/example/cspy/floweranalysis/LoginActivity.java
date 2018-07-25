package com.example.cspy.floweranalysis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cspy.floweranalysis.pojo.User;
import com.example.cspy.floweranalysis.util.HttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    public static final int CORRECT_STATE = 10;
    public static final int WITHOUT_USERTEL = 11;
    public static final int WITHOUT_PASSWORD = 12;
    public static final int WRONG_PASSWORD = 13;


    private static final String TAG = "LoginActivity";

    TextInputEditText usertel;
    TextInputEditText password;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usertel = (TextInputEditText) findViewById(R.id.login_account);
        password = (TextInputEditText) findViewById(R.id.login_password);
        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        int state = intent.getIntExtra("state", -1);
        switch (state) {
            case WITHOUT_PASSWORD:
            case WRONG_PASSWORD:
                String usertelStr = intent.getStringExtra("usertel");
                usertel.setText(usertelStr);
                break;
            case WITHOUT_USERTEL:
                break;
            case -1:
                break;
        }


        Button regsiterBtn = (Button) findViewById(R.id.register_btn);
        regsiterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        Button loginBtn = (Button) findViewById(R.id.loggin_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction();
            }
        });

    }

    private void loginAction() {
        try {
            JSONObject loginMsg = getLoginMsg();
            if (null != loginMsg) {
                String fix = "?usertel=" + loginMsg.get("usertel");
                fix += "&password=" + loginMsg.get("password");
                LoginTask loginTask = new LoginTask();
                loginTask.execute(HttpConnect.loginUri + fix);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getLoginMsg() throws JSONException {
        String usertelStr = usertel.getText().toString();
        String passwordStr = password.getText().toString();

        if (TextUtils.isEmpty(usertelStr) || TextUtils.isEmpty(passwordStr)) {
            Toast.makeText(this, "输入有误", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            JSONObject object = new JSONObject();
            object.put("usertel", usertelStr);
            object.put("password", passwordStr);
            return object;
        }

    }

    class LoginTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... strings) {
            String uri = strings[0];

            HttpConnect connect = new HttpConnect();
            try {
                JSONObject resultJSON = connect.getRequest(uri);
                if (resultJSON != null) {
                    if (resultJSON.get("msg").equals("1")) {
                        JSONObject userJSON = resultJSON.getJSONObject("data");
                        User user = new User();
                        user.setSex(userJSON.getString("sex"));
                        user.setUserid(userJSON.getString("userid"));
                        user.setUsername(userJSON.getString("username"));
                        user.setPassword(userJSON.getString("password"));
                        user.setUsertel(userJSON.getString("usertel"));
                        MyApplication myApplication = (MyApplication) getApplication();
                        myApplication.setUser(user);

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        editor.putString("usertel", user.getUsertel());
                        editor.putString("password", user.getPassword());
                        editor.apply();


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
        protected void onPreExecute() {
            progressDialog.setTitle("提示");
            progressDialog.setMessage("登陆中");
            progressDialog.show();
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

}
