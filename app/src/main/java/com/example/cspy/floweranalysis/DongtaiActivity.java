package com.example.cspy.floweranalysis;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.cspy.floweranalysis.adapter.DongtaiAdapter;
import com.example.cspy.floweranalysis.pojo.Dongtai;

import java.util.List;

public class DongtaiActivity extends AppCompatActivity {

    public static final int ALL_DONGTAI_SUCCESS = 20;
    public static final int ALL_DONGTAI_FAIL = 21;



    private static final String TAG = "DongtaiActivity";

    List<Dongtai> currentList;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;


    Boolean myDongtai;
    DongtaiAdapter adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongtai);

        Intent intent = getIntent();
        myDongtai = intent.getStringExtra("type").equals("my");

        MyApplication myApplication = (MyApplication) getApplication();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (myDongtai) {
                actionBar.setTitle("我的动态");
            } else {
                actionBar.setTitle("动态");
            }
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        recyclerView = findViewById(R.id.recyclerview);
        swipeRefreshLayout = findViewById(R.id.swiptlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshDongtaiList refreshDongtai = new RefreshDongtaiList();
                refreshDongtai.execute();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);


        if (myDongtai) {
            currentList = myApplication.getMyDongtaiList();
        } else {
            currentList = myApplication.getAllDongtaiList();
        }

        adapter = new DongtaiAdapter(currentList, myApplication.getUser(), myApplication);
        recyclerView.setAdapter(adapter);
        RefreshDongtaiList refreshDongtai = new RefreshDongtaiList();
        refreshDongtai.execute();


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

    class RefreshDongtaiList extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            MyApplication myApplication = (MyApplication) getApplication();
            myApplication.refreshDongtai();
            if (myDongtai) {
                adapter.refreshMyItem();
            } else {
                adapter.refreshItem();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            recyclerView.scrollToPosition(0);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }


}
