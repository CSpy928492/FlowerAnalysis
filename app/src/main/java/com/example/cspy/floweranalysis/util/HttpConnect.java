package com.example.cspy.floweranalysis.util;

import android.util.Log;

import com.baidu.mapapi.http.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpConnect {

    private static final String TAG = "HttpConnect";

    public static String loginUri = "http://www.hzong.club:8080/Image_process/LoginServlet";
    public static String registerUri = "http://www.hzong.club:8080/Image_process/RegisterServlet";
    public static String sendMsgUri = "http://www.hzong.club:8080/Image_process/SendVerificationCodeServlet";
    public static String shibieUri = "http://www.hzong.club:8080/Image_process/InsertShibieServlet";
    public static String charuDongtai = "http://www.hzong.club:8080/Image_process/InsertDongtaiServlet";
    public static String chaxunAllDongtai = "http://www.hzong.club:8080/Image_process/SelectAllDongtaiServlet";
    public static String getImageUri = "http://www.hzong.club:8080/Image_process/upload/";
    public static String delDongtai = "http://www.hzong.club:8080/Image_process/DeleteDongtaiServlet";

    public JSONObject getRequest(String uri) throws IOException, JSONException {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(uri).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String str = response.body().string();
            return new JSONObject(str);
        } else {
            return null;
        }
    }

    public byte[] getImage(String uri) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(uri).build();

        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().bytes();
        } else {
            return null;
        }
    }

}
