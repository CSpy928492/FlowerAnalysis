package com.example.cspy.floweranalysis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cspy.floweranalysis.pojo.User;

public class ShezhiFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_shezhi,container,false);


        MyApplication myApplication = (MyApplication) getActivity().getApplication();

        TextView username = (TextView) view.findViewById(R.id.uid);
        username.setText(myApplication.getUser().getUsername());

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.account_setting);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AccountSetting.class);
                startActivity(intent);
            }
        });

        TextView allDongtai = (TextView) view.findViewById(R.id.dongtai_all);
        allDongtai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DongtaiActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
            }
        });

        TextView myDongtai = (TextView) view.findViewById(R.id.dongtai_me);
        myDongtai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DongtaiActivity.class);
                intent.putExtra("type", "my");
                startActivity(intent);
            }
        });

        Button exitBtn = (Button) view.findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication myApplication = (MyApplication) getActivity().getApplication();
                User user = myApplication.getUser();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra("state", LoginActivity.WITHOUT_PASSWORD);
                intent.putExtra("usertel", user.getUsertel());

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                editor.putString("usertel", user.getUsertel());
                editor.apply();

                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}
