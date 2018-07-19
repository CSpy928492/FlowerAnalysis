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

import com.example.cspy.floweranalysis.util.HttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    TextInputEditText usertel;
    TextInputEditText password;
    ProgressDialog progressDialog;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usertel = findViewById(R.id.login_account);
        password = findViewById(R.id.login_password);
        progressDialog = new ProgressDialog(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        editor = preferences.edit();
        String tel = preferences.getString("usertel", null);
        String passwd = preferences.getString("password", null);
        Log.e(TAG, "onCreate: savedTel:" + tel + "\n savedPasswd:" + passwd);

        if (tel != null && passwd != null) {
            usertel.setText(tel);
            password.setText(passwd);
            loginAction();
        } else {
            if (tel != null) {
                usertel.setText(tel);
            }
        }


        Button regsiterBtn = findViewById(R.id.register_btn);
        regsiterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        Button loginBtn = findViewById(R.id.loggin_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction();
            }
        });

    }

    private void loginAction() {
        try {
            String loginMsg = getLoginMsg();
            if (null != loginMsg) {
                JSONObject loginJSON = new JSONObject(loginMsg);
                String fix = "?usertel=" + loginJSON.get("usertel");
                fix += "&password=" + loginJSON.get("password");
                LoginTask loginTask = new LoginTask();
                loginTask.execute(HttpConnect.loginUri + fix, loginJSON.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getLoginMsg() throws JSONException {
        String usertelStr = usertel.getText().toString();
        String passwordStr = password.getText().toString();

        if (TextUtils.isEmpty(usertelStr) || TextUtils.isEmpty(passwordStr)) {
            Toast.makeText(this, "输入有误", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            JSONObject object = new JSONObject();
            object.put("usertel", usertelStr);
            object.put("password", passwordStr);
            return object.toString();
        }

    }

    class LoginTask extends AsyncTask<String, Void, Boolean> {

        private JSONObject loginJSON;

        @Override
        protected Boolean doInBackground(String... strings) {
            String uri = strings[0];
            try {
                loginJSON = new JSONObject(strings[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpConnect connect = new HttpConnect();
            try {
                JSONObject resultJSON = connect.getRequest(uri);
                if (resultJSON != null) {
                    if (resultJSON.get("msg").equals("1")) {
                        JSONObject dataObject = resultJSON.getJSONObject("data");
                        MainActivity.setUser(dataObject);
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
                finish();

                try {
                    editor.putString("usertel", loginJSON.getString("usertel"));
                    editor.putString("password", loginJSON.getString("password"));
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
