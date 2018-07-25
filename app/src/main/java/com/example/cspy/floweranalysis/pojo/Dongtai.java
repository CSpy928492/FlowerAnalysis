package com.example.cspy.floweranalysis.pojo;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.ArrayList;
import java.util.List;

public class Dongtai {

    private String dongtaiId;
    private String userId;
    private String userName;
    private String zhiwuName;
    private String location;
    private String time;
    private String content;
    private Bitmap image;

    private String hLocation;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getZhiwuName() {
        return zhiwuName;
    }

    public void setZhiwuName(String zhiwuName) {
        this.zhiwuName = zhiwuName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }



    public String getDongtaiId() {
        return dongtaiId;
    }

    public void setDongtaiId(String dongtaiId) {
        this.dongtaiId = dongtaiId;
    }

    @Override
    public String toString() {
        return dongtaiId + ":" + content + "/" + userId;
    }

    public String gethLocation() {
        return hLocation;
    }

    public void sethLocation(String hLocation) {
        this.hLocation = hLocation;
    }
}
