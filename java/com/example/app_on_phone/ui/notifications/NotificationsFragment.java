package com.example.app_on_phone.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ck.rtcckinfo.rtc.AvEngine;
import com.example.app_on_phone.databinding.FragmentNotificationsBinding;
import com.example.app_on_phone.login.LoginActivity;

public class NotificationsFragment extends Fragment {

    ImageView imageView;
    private FragmentNotificationsBinding binding;
    LinearLayout linearLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize the binding
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Access views with the binding object
        ImageView btnLogin = binding.btnLogin;
        TextView loginText = binding.textLogin;
        TextView menuText1 = binding.textMenu1;
        TextView menuText2 = binding.textMenu2;
        TextView menuText3 = binding.textMenu3;
        loginText.setTextSize(30);
        menuText1.setTextSize(25);
        menuText2.setTextSize(25);
        menuText3.setTextSize(25);

        binding.textLogin.setText(AvEngine.ParamInfo.getAccount());

        // Use the binding to find views and set listeners
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*create a new intent and start the activity*/
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        binding.textMenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*create a new intent and start the activity*/
                Intent intent = new Intent(getActivity(), StatsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}