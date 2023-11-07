package com.example.app_on_phone;

import android.os.Bundle;
import android.util.Log;

import com.example.app_on_phone.ui.dashboard.DashboardFragment;
import com.example.app_on_phone.ui.home.HomeFragment;
import com.example.app_on_phone.ui.map.MapFragment;
import com.example.app_on_phone.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.app_on_phone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment fragmentHome;
    private Fragment fragmentDashboard;
    private Fragment fragmentNotifications;

    private Fragment fragmentMap;
    private Fragment activeFragment;  // 用于追踪当前显示的 Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化 Fragment
        fragmentHome = new HomeFragment();
        fragmentDashboard = new DashboardFragment();
        fragmentNotifications = new NotificationsFragment();
        fragmentMap = new MapFragment();

        // 使用 FragmentManager 添加 Fragment，但是将除了第一个 Fragment 的其他 Fragment 都隐藏
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragmentHome, "1")
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragmentMap, "2")
                .hide(fragmentMap)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragmentDashboard, "3")
                .hide(fragmentDashboard)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragmentNotifications, "4")
                .hide(fragmentNotifications)
                .commit();

        activeFragment = fragmentHome;  // 默认显示第一个 Fragment

        // 设置 BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            fragmentManager.beginTransaction().hide(activeFragment).commit();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentManager.beginTransaction().show(fragmentHome).commit();
                    activeFragment = fragmentHome;
                    return true;
                case R.id.navigation_map:
                    fragmentManager.beginTransaction().show(fragmentMap).commit();
                    activeFragment = fragmentMap;
                    return true;
                case R.id.navigation_dashboard:
                    fragmentManager.beginTransaction().show(fragmentDashboard).commit();
                    activeFragment = fragmentDashboard;
                    return true;
                case R.id.navigation_notifications:
                    fragmentManager.beginTransaction().show(fragmentNotifications).commit();
                    activeFragment = fragmentNotifications;
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}