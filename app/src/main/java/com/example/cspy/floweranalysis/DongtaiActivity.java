package com.example.cspy.floweranalysis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.cspy.floweranalysis.adapter.DongtaiAdapter;
import com.example.cspy.floweranalysis.pojo.Dongtai;

import java.util.ArrayList;
import java.util.List;

public class DongtaiActivity extends AppCompatActivity {

    private static final String TAG = "DongtaiActivity";
    List<Dongtai> dongtaiList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongtai);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("动态");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        for (int i = 0; i < 20; i++) {
            Dongtai dongtai = new Dongtai();
            dongtai.setUserName("用户" + i);
            dongtai.setContent("内容rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" + i);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
            dongtai.setImage(bitmap);
            dongtai.setZhiwuName("植物" + i);
            dongtai.setTime("2018-07-17");
            dongtai.setLocation("南京市");
            dongtaiList.add(dongtai);
        }
        DongtaiAdapter adapter = new DongtaiAdapter(dongtaiList);

        Log.e(TAG, "onCreate: ListSize" + dongtaiList.size());


        recyclerView.setAdapter(adapter);


    }

    private void initList(List<Dongtai> list) {


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
