package com.example.cspy.floweranalysis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cspy.floweranalysis.util.HttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowResultActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;
    File image;
    Bitmap bitmap;
    TextView resultText;

    AlertDialog inputAlertDialog;

    JSONObject dataJSON;


    private static final String TAG = "ShowResultActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setCancelable(false);
        resultText = findViewById(R.id.result_textview);



        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");

        image = new File(imagePath);
        if (!image.exists()) {
            Toast.makeText(this, "图片不存在", Toast.LENGTH_SHORT).show();
            finish();
        }


        ImageView imageView = findViewById(R.id.result_imageview);
        bitmap = BitmapFactory.decodeFile(imagePath);
        new ShiBieAsync().execute();

        imageView.setImageBitmap(bitmap);


        Button shareBtn = findViewById(R.id.btn_share);
        shareBtn.setOnClickListener(this);






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

    LayoutInflater inflater;
    View mView;
    AlertDialog.Builder inputDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                inflater = LayoutInflater.from(ShowResultActivity.this);
                mView = inflater.inflate(R.layout.set_content_alert, null);
                inputDialog = new AlertDialog.Builder(ShowResultActivity.this);
                inputDialog.setView(mView);

                inputAlertDialog = inputDialog.create();
                inputAlertDialog.show();

                Button cancelButton = mView.findViewById(R.id.alert_cancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (inputAlertDialog != null && inputAlertDialog.isShowing()) {
                            inputAlertDialog.dismiss();
                        }
                    }
                });


                final EditText input = (EditText) findViewById(R.id.alert_content);

                Button okButton = mView.findViewById(R.id.alert_confirm);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareTask shareTask = new ShareTask();
                        String content = "动态内容";
                        shareTask.execute(content);
                    }
                });


                break;
        }
    }


    class ShiBieAsync extends AsyncTask<Void, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody filebody = RequestBody.create(MediaType.parse("image/jpeg"), image);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("userid", MainActivity.user.getUserid())
                    .addFormDataPart("location", MainActivity.location.toString())
                    .addFormDataPart("src", "temp.jpg", filebody)
                    .build();
            Request request = new Request.Builder()
                    .url(HttpConnect.shibieUri)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return new JSONObject(response.body().string());
                } else {
                    return null;
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
            progressDialog.setMessage("识别中，请稍后");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressDialog.dismiss();
            if (jsonObject != null) {
                try {
                    dataJSON = jsonObject.getJSONObject("data");
                    resultText.setText("植物名：" + dataJSON.get("shibiename") + "\n可信度：" + dataJSON.get("kexingdu"));
                    Toast.makeText(ShowResultActivity.this, "识别成功", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "onPostExecute: jsonObject" + jsonObject.toString());
            } else {
                Toast.makeText(ShowResultActivity.this, "识别失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ShareTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... strings) {

            String userid = "";
            String username = "";
            String zhiwuname = "";
            String location = "";
            String time = "";
            String content = "";

            try {
                userid = MainActivity.user.getUserid();
                username = MainActivity.user.getUsername();
                zhiwuname = (String) dataJSON.get("shibiename");
                location = MainActivity.location.toString();
                time = System.currentTimeMillis() + "";
                content = strings[0];
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody filebody = RequestBody.create(MediaType.parse("image/jpeg"), image);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("userid", userid)
                    .addFormDataPart("username", username)
                    .addFormDataPart("zhiwuname", zhiwuname)
                    .addFormDataPart("location", location)
                    .addFormDataPart("time", time)
                    .addFormDataPart("content", content)
                    .addFormDataPart("src", "temp.jpg", filebody)
                    .build();
            Request request = new Request.Builder()
                    .url(HttpConnect.charuDongtai)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            progressDialog1.setTitle("提示");
            progressDialog1.setMessage("分享中，请稍后");
            progressDialog1.show();
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            progressDialog1.dismiss();
            if (bool) {
                Toast.makeText(ShowResultActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ShowResultActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
