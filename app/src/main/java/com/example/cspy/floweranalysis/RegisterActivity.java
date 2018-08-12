package com.example.cspy.floweranalysis;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.cspy.floweranalysis.util.HttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    Button sendRegisterBtn;
    TextInputEditText username;
    TextInputEditText password1;
    TextInputEditText password2;
    TextInputEditText usertel;
    TextInputEditText msg;
    RadioGroup sexgroup;
    String trueMsg = "";

    TextInputLayout usernameLayout;
    TextInputLayout password1Layout;
    TextInputLayout password2Layout;
    TextInputLayout usertelLayout;
    TextInputLayout msgLayout;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        username = (TextInputEditText) findViewById(R.id.register_username);
        usernameLayout = (TextInputLayout) findViewById(R.id.layout_register_account);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String name = username.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        usernameLayout.setError("用户名不能为空");
                    } else {
                        usernameLayout.setErrorEnabled(false);
                    }
                }
            }
        });
        password1 = (TextInputEditText) findViewById(R.id.register_password1);
        password1Layout = (TextInputLayout) findViewById(R.id.layout_register_password);
        password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //失去焦点的时候
                if (!b) {
                    String p1 = password1.getText().toString();
                    if (TextUtils.isEmpty(p1)) {
                        password1Layout.setError("密码不能为空");
                    } else {
                        password1Layout.setErrorEnabled(false);
                    }
                }
            }
        });

        password2 = (TextInputEditText) findViewById(R.id.register_password2);
        password2Layout = (TextInputLayout) findViewById(R.id.layout_register_password1);
        password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (TextUtils.isEmpty(password2.getText().toString())) {
                        password2Layout.setError("确认密码不能为空");
                    } else {
                        String p1 = password1.getText().toString();
                        String p2 = password2.getText().toString();
                        if (!TextUtils.equals(p1, p2)) {
                            password2Layout.setError("密码不一致");
                        } else {
                            password2Layout.setErrorEnabled(false);
                        }
                    }
                }

            }
        });
        usertel = (TextInputEditText) findViewById(R.id.register_tel);
        usertelLayout = (TextInputLayout) findViewById(R.id.layout_register_tel);
        usertel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String tel = usertel.getText().toString();
                    if (TextUtils.isEmpty(tel)) {
                        usertelLayout.setError("手机号不能为空");
                    } else {
                        if (tel.length() != 11) {
                            usertelLayout.setError("手机格式不正确");
                        } else {
                            usertelLayout.setErrorEnabled(false);
                        }
                    }
                }
            }
        });

        msg = (TextInputEditText) findViewById(R.id.register_msg);
        msgLayout = (TextInputLayout) findViewById(R.id.layout_userMsg);
        msg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String msgStr = msg.getText().toString();
                    if (TextUtils.isEmpty(msgStr)) {
                        msgLayout.setError("验证码不能为空");
                    } else {
                        if (msg.length() != 6) {
                            msgLayout.setError("验证码格式不正确");
                        } else {
                            msgLayout.setErrorEnabled(false);
                        }
                    }
                }
            }
        });
        sexgroup = (RadioGroup) findViewById(R.id.register_sex_group);
        sexgroup.check(R.id.sex_male);

        progressDialog = new ProgressDialog(this);


        sendRegisterBtn = (Button) findViewById(R.id.register_confirm_btn);
        sendRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String json = getRegisterMsg();
                    if (TextUtils.isEmpty(json)) {
                        Toast.makeText(RegisterActivity.this, "注册信息填写错误", Toast.LENGTH_SHORT).show();
                    } else {
                        RegisterTask registerTask = new RegisterTask();
                        registerTask.execute(json);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        final Button sendMsgBtn = (Button) findViewById(R.id.register_sendMsg);
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String usertelStr = usertel.getText().toString();
                        if (!TextUtils.isEmpty(usertelStr) && usertelStr.length() == 11) {
                            String uri = HttpConnect.sendMsgUri + "?usertel=" + usertelStr;
                            Log.e(TAG, "run: sendMsgUri JSON:" + uri);

                            try {
                                HttpConnect hc = new HttpConnect();
                                JSONObject resultJSON = hc.getRequest(uri);

                                if (resultJSON != null && resultJSON.get("msg").equals("1")) {
                                    trueMsg = resultJSON.getString("data");
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RegisterActivity.this, "手机号错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                Log.e(TAG, "run: resultJSON" + resultJSON);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                    }
                }).start();

                sendMsgBtn.setEnabled(false);
                CountDownTimer timer = new CountDownTimer(30 * 1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        sendMsgBtn.setText(millisUntilFinished / 1000 + "秒后再次发送");
                    }

                    @Override
                    public void onFinish() {
                        sendMsgBtn.setText("获取验证码");
                        sendMsgBtn.setClickable(true);
                    }
                }.start();
            }


        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getRegisterMsg() throws JSONException {
        JSONObject object = new JSONObject();

        String usernameStr = username.getText().toString();
        String password1Str = password1.getText().toString();
        String password2Str = password2.getText().toString();
        String usertelStr = usertel.getText().toString();
        String userMsg = msg.getText().toString();
        RadioButton selectedSex = (RadioButton) findViewById(sexgroup.getCheckedRadioButtonId());
        String sex = String.valueOf(selectedSex.getText());

        if (TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(password1Str) || TextUtils.isEmpty(password2Str) || !password1Str.equals(password2Str)
                || TextUtils.isEmpty(usertelStr) || TextUtils.isEmpty(userMsg) || TextUtils.isEmpty(sex)) {
            return null;
        } else {
            if (userMsg.equals(trueMsg)) {
                object.put("usertel", usertelStr);
                object.put("password", password1Str);
                object.put("sex", sex);
                object.put("username", usernameStr);
                Log.e(TAG, "getRegisterMsg: json" + object.toString());
                return object.toString();
            } else {
                Toast.makeText(this, "验证码不正确", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

    }

    class RegisterTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... strings) {
            String json = strings[0];
            try {
                HttpConnect hc = new HttpConnect();
                Log.e(TAG, "doInBackground: json:" + json);
                JSONObject object = new JSONObject(json);
                String fix = "?usertel=" + object.get("usertel");
                fix += "&password=" + object.get("password");
                fix += "&sex=" + object.get("sex");
                fix += "&username=" + object.get("username");
                JSONObject resultJSON = hc.getRequest(HttpConnect.registerUri + fix);
                if (TextUtils.isEmpty(resultJSON.toString())) {
                    Log.e(TAG, "doInBackground: resultJSON" + ":null");
                    return false;
                } else {
                    Log.e(TAG, "doInBackground: resultJSON:" + resultJSON);
                    if (resultJSON.get("msg").equals("1")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("提示");
            progressDialog.setMessage("注册中，请稍后");
            progressDialog.show();
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();

        }
    }



}
