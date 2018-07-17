package com.example.cspy.floweranalysis.pojo;

import android.graphics.Bitmap;
import android.media.Image;

public class Dongtai {

    String userId;
    String userName;
    String zhiwuName;
    String location;
    String time;
    String content;
    Bitmap image;

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
}
