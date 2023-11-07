package com.example.app_on_phone.timetable.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.app_on_phone.R;
import com.example.app_on_phone.databinding.ActivityAltertimeBinding;
import com.google.android.material.snackbar.Snackbar;

public class AlterTimeActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAltertimeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAltertimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void back(View view) {
        finish();
    }
    //send date value back to optionactivity
    public void save(View view) {
        String lunchValue = binding.lunchvalue.getText().toString();
        String breakValue = binding.breakvalue.getText().toString();
        String startValue = binding.starttimevalue.getText().toString();
        int classLength = Integer.parseInt(binding.classLength2.getText().toString());
        int classCnt = Integer.parseInt(binding.classcount.getText().toString());

        //put Date into sharedpreference
        SharedPreferences.Editor editor = getSharedPreferences("time",MODE_PRIVATE).edit();
        editor.putString("lunch",lunchValue);
        editor.putString("break",breakValue);
        editor.putString("start",startValue);
        editor.putInt("Length",classLength);
        editor.putInt("Cnt",classCnt);
        Snackbar.make(view, "保存成功", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        finish();
    }

}