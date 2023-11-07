package com.example.app_on_phone.signin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ck.rtcckinfo.rtc.AvEngine;
import com.ck.rtcckinfo.user.entity.UserInfoGobal;
import com.ck.rtcckinfo.user.entity.UserInfoRegisterInfo;
import com.ck.rtcckinfo.user.entity.UserLogin;
import com.ck.rtcckinfo.util.StringUtils;
import com.ck.rtcckinfo.websocket.HeartTask;
import com.example.app_on_phone.MainActivity;
import com.example.app_on_phone.R;
import com.example.app_on_phone.signin.service.HeartAmapService;
import com.example.app_on_phone.signin.util.NetUtil;
import com.example.app_on_phone.signin.widget.MyEditText;
import com.example.app_on_phone.signin.widget.MyEditTextPass;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private Button btn_login;

      MyEditTextPass tv_password;
      MyEditText tv_username;
    private Boolean islogn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        EventBus.getDefault().register(this);


        UserInfoGobal.setLast_token("");
        btn_login=(Button) findViewById(R.id.btn_login);
        tv_username=(MyEditText)findViewById(R.id.tv_username);
        tv_password=(MyEditTextPass)findViewById(R.id.tv_password);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();
            }
        });

        islogn = true;
        getIMEIInit();

        //设置记住密码
        //if ((boolean) SharedPreferencesUtils.getParam(this, ConstansSharedPreference.LOGINCBREMEMBERPASSWORD, false)) {
        tv_username.setText(AvEngine.ParamInfo.getAccount());
        tv_password.setText(AvEngine.ParamInfo.getPassWord());

        //}

    }

    private void getIMEIInit() {
        UserInfoGobal.setSjch( NetUtil.getDeviceId(this));
//        SharedPreferencesUtils.setParam(this, ConstansSharedPreference.uuid, NetUtil.getDeviceId(this));
//        String UUID = (String) SharedPreferencesUtils.getParam(this, ConstansSharedPreference.uuid, "").toString();
//        if (StringUtils.isEmpty(UUID)) {
//            SharedPreferencesUtils.setParam(this, ConstansSharedPreference.uuid, NetUtil.getDeviceId(this));
//            UserInfoGobal.setSjch( NetUtil.getDeviceId(this));
//        }else{
//            UserInfoGobal.setSjch(UUID);
//        }
        requestPermissioner();//申请权限
    }

    public void startHeartService() {
        Intent intent=new Intent(this,HeartTask.class);
        startService(intent);


        Intent intent2=new Intent(this, HeartAmapService.class);
        startService(intent2);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe
    public void onEvent(UserLogin userLogin) {
        try {
            if (islogn) {
                if ("0".equals(userLogin.getRspMsg().getRes_code())) {
                    //收到通知消息
                    UserInfoRegisterInfo.UserInfoInit(userLogin);//初始化用户信息

                    startHeartService();
                    Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                    startActivity(intent);
                    islogn = false;
                    finish();

                } else if ("b0007".equals(userLogin.getRspMsg().getRes_code())) {
                   // showDialog(userLogin.getRspMsg().getRes_msg(), userLogin.getConfig().getLast_token());
                } else {
                    ToastUtils.show(userLogin.getRspMsg().getRes_msg());
                }

            }

        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }



    private void loginUser() {
        if (StringUtils.isEmpty(tv_username.getText().toString())) {
            ToastUtils.show("用户名不可为空！");
            return;
        }
        if (StringUtils.isEmpty(tv_password.getText().toString())) {
            ToastUtils.show("密码不可为空！");
            return;
        }
        getIMEIInit();

        AvEngine.ParamInfo.login(tv_username.getText().toString(), tv_password.getText().toString()); //调用登陆

    }
    private void requestPermissioner() {
        String[] groups={Permission.RECORD_AUDIO,Permission.CAMERA,Permission.RECORD_AUDIO,Permission.WRITE_EXTERNAL_STORAGE,Permission.READ_PHONE_STATE};
        AndPermission.with(SignInActivity.this)
                .permission(groups)
                // 准备方法，和 okhttp 的拦截器一样，在请求权限之前先运行改方法，已经拥有权限不会触发该方法
                .rationale((context, permissions, executor) -> {
                    // 此处可以选择显示提示弹窗
                    executor.execute();
                })
                // 用户给权限了
                .onGranted(permissions -> Log.v("qqq4", "设置权限了"))
                // 用户拒绝权限，包括不再显示权限弹窗也在此列
                .onDenied(permissions -> {
                    // 判断用户是不是不再显示权限弹窗了，若不再显示的话进入权限设置页
                    if (AndPermission.hasAlwaysDeniedPermission(SignInActivity.this, permissions)) {
                        // 打开权限设置页
                        AndPermission.permissionSetting(SignInActivity.this).execute();
                        return;
                    }
                })
                .start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
