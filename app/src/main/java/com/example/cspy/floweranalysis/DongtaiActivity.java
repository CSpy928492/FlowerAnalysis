package com.example.cspy.floweranalysis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.cspy.floweranalysis.adapter.DongtaiAdapter;
import com.example.cspy.floweranalysis.background.DongtaiGetTask;
import com.example.cspy.floweranalysis.pojo.Dongtai;

import java.util.ArrayList;
import java.util.List;

public class DongtaiActivity extends AppCompatActivity {

    private static final String TAG = "DongtaiActivity";
    List<Dongtai> dongtaiList = new ArrayList<>();

    SwipeRefreshLayout swipeRefreshLayout;



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
        swipeRefreshLayout = findViewById(R.id.swiptlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DongtaiGetTask dongtaiGetTask = new DongtaiGetTask();
                dongtaiGetTask.execute(swipeRefreshLayout);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        DongtaiAdapter adapter = new DongtaiAdapter(MainActivity.dongtaiList);

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
