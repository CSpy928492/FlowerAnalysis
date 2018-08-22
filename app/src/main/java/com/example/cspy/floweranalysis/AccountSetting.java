package com.example.cspy.floweranalysis;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cspy.floweranalysis.pojo.User;

public class AccountSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MyApplication myApplication = (MyApplication) getApplication();
        User user = myApplication.getUser();

        TextView textUserName = (TextView) findViewById(R.id.account_name);
        textUserName.setText(user.getUsername());
        TextView textUserTel = (TextView) findViewById(R.id.account_tel);
        String usertel = user.getUsertel();
        String modifiedTel;
        if (usertel.length() != 11) {
            modifiedTel = "错误";
        } else {
            modifiedTel = usertel.substring(0, 3) + "****" + usertel.substring(7);
        }
        textUserTel.setText(modifiedTel);
        TextView textUserSex = (TextView) findViewById(R.id.account_sex);
        textUserSex.setText(user.getSex());
        TextView textUserDongtaiNum = (TextView) findViewById(R.id.account_dongtai_num);
        textUserDongtaiNum.setText(myApplication.getAllDongtai().getDongtaiListByUID(user.getUserid()).size() + "");

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

}
