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

public class ShezhiFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_shezhi,container,false);


        LinearLayout linearLayout = view.findViewById(R.id.account_setting);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AccountSetting.class);
                startActivity(intent);
            }
        });

        TextView allDongtai = view.findViewById(R.id.dongtai_all);
        allDongtai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DongtaiActivity.class);
                startActivity(intent);
            }
        });

        Button exitBtn = view.findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferenceManager.edit();

                String usertel = MainActivity.user.getUsertel();
                editor.clear();
                editor.apply();
                editor.putString("usertel", usertel);
                editor.apply();
                startActivity(intent);
                getActivity().finish();
            }
        });








        return view;
    }
}
