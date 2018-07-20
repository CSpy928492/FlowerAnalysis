package com.example.cspy.floweranalysis;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        TextView textUserName = findViewById(R.id.account_name);
        textUserName.setText(MainActivity.user.getUsername());
        TextView textUserTel = findViewById(R.id.account_tel);
        String usertel = MainActivity.user.getUsertel();
        String modifiedTel = usertel.substring(0, 3) + "****" + usertel.substring(7);
        textUserTel.setText(modifiedTel);
        TextView textUserSex = findViewById(R.id.account_sex);
        textUserSex.setText(MainActivity.user.getSex());
        TextView textUserDongtaiNum = findViewById(R.id.account_dongtai_num);
        textUserDongtaiNum.setText(MainActivity.getMyDongtaiList().size() + "");

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
