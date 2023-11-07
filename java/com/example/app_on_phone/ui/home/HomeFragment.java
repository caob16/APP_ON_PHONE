package com.example.app_on_phone.ui.home;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ck.rtcckinfo.channel.entity.ChannelAdd;
import com.ck.rtcckinfo.channel.entity.ChannelDelUser;
import com.ck.rtcckinfo.channel.entity.ChannelForward;
import com.ck.rtcckinfo.channel.entity.ChannelGet;
import com.ck.rtcckinfo.channel.entity.ChannelGroup;
import com.ck.rtcckinfo.channel.entity.ChannelHangup;
import com.ck.rtcckinfo.channel.entity.ChannelIn;
import com.ck.rtcckinfo.channel.entity.ChannelInOther;
import com.ck.rtcckinfo.channel.entity.ChannelList;
import com.ck.rtcckinfo.channel.entity.ChannelOut;
import com.ck.rtcckinfo.channel.entity.ChannelRelease;
import com.ck.rtcckinfo.http.HttpDefine;
import com.ck.rtcckinfo.player.AsyncPlayerUtil;
import com.ck.rtcckinfo.rtc.AvEngine;
import com.ck.rtcckinfo.rtc.WebRtcPlayUtil;
import com.ck.rtcckinfo.rtc.WebRtcPushUtil;
import com.ck.rtcckinfo.user.entity.UserInfoDto;
import com.ck.rtcckinfo.user.entity.UserInfoGobal;
import com.ck.rtcckinfo.util.ButtonUtils;
import com.ck.rtcckinfo.util.EventBusUtils;
import com.ck.rtcckinfo.util.StringUtils;
import com.example.app_on_phone.R;
import com.example.app_on_phone.databinding.FragmentHomeBinding;
import com.example.app_on_phone.signin.ToastUtils;
import com.example.app_on_phone.signin.util.UploadFileDto;
import com.example.app_on_phone.ui.adapter.DropDownChannel;
import com.example.app_on_phone.ui.adapter.DropDownMember;
import com.example.app_on_phone.ui.bean.PPTHangup;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.webrtc.EglBase;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "c";
    private static final String KEY_INDEX = "INDEX";
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    public static boolean isChannelIn = false;//判断是否在对讲中
    private static AsyncPlayerUtil ringPlayer;
    private static EglBase mRootEglBase;
    private static WebRtcPushUtil pushWebRtcPushUtil;
    private static WebRtcPlayUtil playWebRtcUtil;
    private final int ownerms = 1001;
    private final int groupnamems = 1002;
    private final int channeluser = 1003;

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(final Message msg) {
            switch (msg.what) {

                case ownerms:
                    try {
                        String str = (String) msg.obj;
                        Log.d(TAG, "【ownerms】" + str);
                        Log.d(TAG, getString(R.string.ppt_owner) + str);
                        binding.shouyeOwner.setText(getString(R.string.ppt_owner) + str);
                        binding.shouyeOwner.postInvalidate();
                    } catch (Exception e) {
                        Log.e(TAG, ": " + e.getMessage());
                    }

                    break;
                case groupnamems:
                    try {
                        String str = (String) msg.obj;
                        binding.shouyeChannelGroup.setText(str);
                        binding.shouyeChannelGroup.postInvalidate();
                        // currentDayTextView.setText(DateTime.currentDay());
                    } catch (Exception e) {
                        Log.e(TAG, ": " + e.getMessage());
                    }
                    break;
            }
        }
    };
    private List<ChannelList> channelLists;// 获取频道
    private BasePopupView xbottomwindow;
    private List<UserInfoDto> userInfoDtoList;
    private boolean isChannelget = false;

    private BasePopupView xbottomwindowMemberList;
    private DropDownMember mDropDownMember;

    private FragmentHomeBinding binding;
    //1.点击重新刷新页面

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
             HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EventBus.getDefault().register(this);
        ringPlayer = new AsyncPlayerUtil(getActivity());
        binding.btnPptVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case 0:
                        channelget(ChannelGroup.getChannel_id());
                        break;
                    case 1:
                    case 3:
                        channelRelease();
                        break;
                }
                return true;
            }
        });


        binding.pptSwitchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channelList();
            }
        });

        binding.pptMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(ChannelGroup.getChannel_id())) {
                    ToastUtils.show(getString(R.string.ppt_swicth_group));
                    return;
                }
                getChannelUser(ChannelGroup.getChannel_id());

            }
        });
        //setInitPushPlay();
        return root;
    }

    @Override
    public void onResume() {
        String str = StringUtils.isNotEmpty(ChannelGroup.getChannel_name()) ? ChannelGroup.getChannel_name() : getString(R.string.ppt_channel_name);
        sendMessage(str, groupnamems);
        super.onResume();
    }

    /**
     * 获取频道
     */
    private void channelList() {
        try {
            OkHttpUtils
                    .get()
                    .url(HttpDefine.getInstance().channelList(UserInfoGobal.getId()))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (StringUtils.isNotEmpty(HttpDefine.getInstance().resCode(response))) {
                                String data = JSON.parseObject(response).getString("data");
                                String channelList = JSON.parseObject(data).getString("channelList");
                                channelLists = JSON.parseArray(channelList, ChannelList.class);
                                Log.d(TAG, "【获取频道列表成功】: " + channelLists);
                                groupName();
                            } else {
                                Log.d(TAG, "【获取频道列表失败】 ");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
            ;
        }
    }
    /**
     * 点击频道
     */
    private void groupName() {

        try {
            if (channelLists != null && channelLists.size() > 0) {
                if (xbottomwindow != null && !xbottomwindow.isShow() || xbottomwindow == null) {
                    xbottomwindow = new XPopup.Builder(getActivity())
                            .asCustom(new DropDownChannel(getActivity())
                                    .setStringData("选择对讲组", channelLists, null)
                                    .setCheckedPosition(-1)
                                    .setOnBottomSelectListener(new DropDownChannel.OnBottomSelectGroupListener() {
                                        @Override
                                        public void onSelectGroup(ChannelList mdata) {
                                            Log.d(TAG, "【选择对讲组频道】 " + mdata);
                                            // 主动进入对讲组
                                            getChannelIn(mdata.getChannel_id(), UserInfoGobal.getId(), mdata.getChannel_name());

                                        }

                                    }))
                            .show();
                }
            } else {
                ToastUtils.show("暂无对讲组");
            }
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }



    @Override
    public void onDestroy() {
        EventBusUtils.unregister(this);
        stopPushClose();
        stopPlayClose();
        ChannelGroup.setChannel_id("");
        ChannelGroup.setIsChannel(false);
        ChannelGroup.setChannel_name("");
        super.onDestroy();
    }


    /**
     * 按下对讲
     *
     * @param channel_id
     */


    public void channelget(String channel_id) {
        try {
            if (!ButtonUtils.isPTTButtonFastClick()) {
                Log.d(TAG, "【解决重复点击channelget】 ");
                return;
            }
            Log.e(TAG, "onResponse:1 " + (System.currentTimeMillis()));
            if (StringUtils.isNotEmpty(channel_id)) {
                OkHttpUtils
                        .get()
                        .url(HttpDefine.getInstance().channelget(channel_id, UserInfoGobal.getId()))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if (StringUtils.isNotEmpty(HttpDefine.getInstance().resCode(response))) {
                                    Log.d(TAG, "【channelget】成功 ");
                                    isChannelIn = true;
                                    startPush();
                                   // mHandler.sendEmptyMessage(4);
                                    Log.e(TAG, "onResponse:2 " + (System.currentTimeMillis()));
                                    sendMessage(UserInfoGobal.getName(), ownerms);
                                    Log.e(TAG, "onResponse:3 " + (System.currentTimeMillis()));
                                    isChannelget = false;
                                } else {
                                    ringPlayer.PPTFail();
                                    isChannelget = true;
                                    ToastUtils.show(getString(R.string.ppt_change_fail));
                                    stopPushClose();
                                    Log.d(TAG, "【channelget】失败 ");
                                }

                            }
                        });
            } else {
                ToastUtils.show(getString(R.string.ppt_swicth_group));
            }
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }

    /**
     * 释放话权
     */
    public void channelRelease() {
        try {
            isChannelIn = false;
            if (!isChannelget) {
                //  Log.e(TAG, "channelrelase: "+ownertv.getText().toString().replace(getString(R.string.ppt_owner),""));
                if (binding.shouyeOwner.getText().toString().replace(getString(R.string.ppt_owner), "").equals(UserInfoGobal.getName())) {
                    sendMessage("", ownerms);
                }
                ringPlayer.PPTHearEnd();
            }
            binding.btnPptVoice.setImageResource(R.mipmap.ppt_normal);
            stopPushClose();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e(TAG, ": "+e.getMessage());
            }
        } catch (Exception e) {
            stopPushClose();
            Log.e(TAG, ": " + e.getMessage());
        }

    }

    /**
     * 进入频道
     *
     * @param channel_id
     * @param user_id    主动和被动
     */
    private void getChannelIn(String channel_id, String user_id, String channnel_name) {
        try {


            OkHttpUtils
                    .get()
                    .url(HttpDefine.getInstance().channelIn(channel_id, user_id))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (StringUtils.isNotEmpty(HttpDefine.getInstance().resCode(response))) {
                                //防止在推流
                                sendMessage("", ownerms);
                                sendMessage(channnel_name, groupnamems);
                                stopPlayClose();//停止拉流
                                ChannelGroup.setChannel_name(channnel_name);
                                ChannelGroup.setChannel_id(channel_id);
                                ChannelGroup.setIsChannel(true);
                                Log.d(TAG, "进入【channlin】成功");
                            } else {
                                Log.d(TAG, "进入【channlin】失败 ");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }

    @Subscribe
    public void onEvent(ChannelAdd channelAdd) {
            Log.d(TAG, "【channelAdd】");
            if (UserInfoGobal.getPhone().equals(channelAdd.getPhone())) {
                getChannelIn(channelAdd.getChannel_id(), channelAdd.getUser_id(), channelAdd.getChannel_name());
            }

    }
    /**
     * 获取用户信息
     *
     * @param channel_id
     */
    private void getChannelUser(String channel_id) {
        Log.e(TAG, "getChannelUser:" + channel_id);
        try {
            OkHttpUtils
                    .get()
                    .url(HttpDefine.getInstance().getChannelUser(channel_id))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                        }

                        @SuppressLint("WrongConstant")
                        @Override
                        public void onResponse(String response, int id) {
                            if (StringUtils.isNotEmpty(HttpDefine.getInstance().resCode(response))) {
                                String data = JSON.parseObject(response).getString("data");

                                userInfoDtoList = JSON.parseArray(data, UserInfoDto.class);
                                Log.d(TAG, "【获取用户信息成功】" + userInfoDtoList);


                                memberList();
//                                for (UserInfoDto userInfoDto : userInfoDtoList) {
//                                    ChannelForward(userInfoDto);
//                                }

                                // sendMessage(userInfoDtoList, channeluser);
                            } else {
                                Log.d(TAG, "【获取用户信息失败】 ");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }
    private void memberList() {
        try {
            mDropDownMember = new DropDownMember(getActivity());
            if (userInfoDtoList != null && userInfoDtoList.size() > 0) {
                if (xbottomwindowMemberList != null && !xbottomwindowMemberList.isShow() || xbottomwindowMemberList == null) {
                    xbottomwindowMemberList = new XPopup.Builder(getActivity())
                            .asCustom(mDropDownMember
                                    .setStringData("成员列表", userInfoDtoList, null)
                                    .setCheckedPosition(-1)
                                    .setOnBottomSelectListener(new DropDownMember.OnBottomSelectGroupListener() {
                                        @Override
                                        public void onSelectGroup(UserInfoDto mdata) {
                                        }
                                    }))
                            .show();
                }
            } else {
                ToastUtils.show("暂无对讲组");
            }
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }

    private void sendMessage(Object object, int what) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = object;
        mHandler.sendMessage(message);
        Log.d(TAG, "【sendMessage:】object=" + object + ",what=" + what);
    }

    private void setInitPushPlay() {
        mRootEglBase = EglBase.create();
        pushWebRtcPushUtil = new WebRtcPushUtil(getActivity());
        playWebRtcUtil = new WebRtcPlayUtil(getActivity());
        pushInit();
        playInit();

    }

    //推流控件
    private void pushInit() {
        //防止重复推流
        try {
            pushWebRtcPushUtil.create(mRootEglBase, binding.pushPlay.pushrtc, HttpDefine.getInstance().PTTUrl(ChannelGroup.getChannel_id(), UserInfoGobal.getPhone()), false, new WebRtcPushUtil.WebRtcCallBack() {
                @Override
                public void onSuccess(String str) {
                    Log.d(TAG, "【推流成功】 " + HttpDefine.getInstance().PTTUrl(ChannelGroup.getChannel_id(), UserInfoGobal.getPhone()));
                }

                @Override
                public void onFail(String str) {
                    Log.e(TAG, "【推流失败】");
                    ToastUtils.show(str);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }

    //播放控件
    private void playInit() {
        try {
            playWebRtcUtil.create(mRootEglBase, binding.pushPlay.playrtc, HttpDefine.getInstance().PTTUrl(ChannelGroup.getChannel_id(), UserInfoGobal.getPhone()), false, new WebRtcPlayUtil.WebRtcCallBack() {
                @Override
                public void onSuccess(String str) {
                    Log.d(TAG, "【拉流成功】 " + HttpDefine.getInstance().PTTUrl(ChannelGroup.getChannel_id(), UserInfoGobal.getPhone()));
                }
                @Override
                public void onFail(String str) {
                    ToastUtils.show(str);
                    Log.e(TAG, "【拉流失败】");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());

        }
    }

    /**
     * 开始推流
     */
    private void startPush() {
        Log.e("!!!!", "startPush: " + HttpDefine.getInstance().PTTUrl(ChannelGroup.getChannel_id(), UserInfoGobal.getPhone()));
        pushWebRtcPushUtil.pushConfig(HttpDefine.getInstance().PTTUrl(ChannelGroup.getChannel_id(), UserInfoGobal.getPhone()));
        Log.d(TAG, "【开始推流】 ");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Log.e(TAG, ": "+e.getMessage());
        }
        ringPlayer.PPTHearStart();
        binding.btnPptVoice.setImageResource(R.mipmap.ppt_sel);
    }

    /**
     * 开始播放
     */
    private void startPlay(String phone) {
        // playwebRtcUtil.openSpeaker();
        Log.e(TAG, "startPlay:======= " + HttpDefine.getInstance().PTTUrl(ChannelGroup.getChannel_id(), phone));
        playWebRtcUtil.playConfig(HttpDefine.getInstance().PTTUrl(ChannelGroup.getChannel_id(), phone));
        Log.d(TAG, "【开始拉流】 ");
    }

    private void stopPlayClose() {
        if (playWebRtcUtil != null) {
            playWebRtcUtil.onClose();
        }
        Log.d(TAG, "【释放拉流】 ");
    }

    private void stopPushClose() {
        if (pushWebRtcPushUtil != null) {
            pushWebRtcPushUtil.onClose();
        }
        Log.d(TAG, "【释放推流】 ");
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Subscribe
    public void onEvent(ChannelGet channelGet) {
        try {
            Log.d(TAG, "【channelGet】" + channelGet);
             if (ChannelGroup.isIsChannel()) {  //如果收到channin  ture 才能收channelget
                if (StringUtils.isNotEmpty(channelGet.getUser_id())) {
                    if (!UserInfoGobal.getId().equals(channelGet.getUser_id())) {
                        sendMessage(channelGet.getName(), ownerms);
                        // AudioUtils.changeToSpeaker(getActivity());
                        changeToSpeaker();

                        startPlay(channelGet.getPhone());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }

    @Subscribe
    public void onEvent(ChannelDelUser channelDelUser) {
        Log.e(TAG, "onEvent: " + channelDelUser);
        if (StringUtils.isNotEmpty(ChannelGroup.getChannel_id())) {
            if (channelDelUser.getChannel_id().equals(ChannelGroup.getChannel_id()) && channelDelUser.getPhone().equals(UserInfoGobal.getPhone())) {
                Log.e(TAG, "channelDelUser");
                exitChannel();
            }
        }
    }

    @Subscribe
    public void onEvent(ChannelRelease channelRelease) {
        try {
            Log.d(TAG, "【channelRelease】" + channelRelease);
             if (ChannelGroup.isIsChannel()) {
                if (StringUtils.isNotEmpty(channelRelease.getUser_id())) {
                    // if (!UserInfoGobal.getId().equals(channelRelease.getUser_id())) {
                    sendMessage("", ownerms);
                    // AudioUtils.changeToNomal(getActivity());

                    changeNormal();
                    stopPlayClose();
                }
            }
            muteAudioRelease();
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());

        }
    }

    @Subscribe
    public void onEvent(ChannelHangup channelHangup) {
        Log.d(TAG, "【channelHangup】");

        if (StringUtils.isNotEmpty(ChannelGroup.getChannel_id())) {
            if (ChannelGroup.getChannel_id().equals(channelHangup.getChannel_id())) {
                channelRelease();
                exitChannel();
            }
        }
    }
    /**
     * 为了释放对讲组的使用
     *
     * @param pPPTHangup
     */
    @Subscribe
    public void onEvent(PPTHangup pPPTHangup) {
        if (isChannelIn) {
            channelRelease();
        }
    }

    @Subscribe
    public void onEvent(ChannelIn channelIn) {
        try {
            if (null != channelIn && StringUtils.isNotEmpty(channelIn.getChannel_id())) {
                if (UserInfoGobal.getId().equals(channelIn.getUser_id()) && (StringUtils.isEmpty(ChannelGroup.getChannel_id()) || !channelIn.getChannel_id().equals(ChannelGroup.getChannel_id()))) {
                    //  if(!ChannelGroup.isIsChannel()) {
                    getChannelIn(channelIn.getChannel_id(), UserInfoGobal.getId(), channelIn.getChannel_name()); //进入对讲组
                    ChannelGroup.setIsChannel(true);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }

    @Subscribe
    public void onEvent(ChannelOut channelOut) {
        try {
            Log.d(TAG, "【ChannelOut】" + UserInfoGobal.getId() + "===" + channelOut.getUser_id());
            if (StringUtils.isNotEmpty(channelOut.getChannel_id()) && StringUtils.isNotEmpty(ChannelGroup.getChannel_id())) {
                if (channelOut.getUser_id().equals(UserInfoGobal.getId())) {
                    exitChannel();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, ": " + e.getMessage());
        }
    }

    @Subscribe
    public void onEvent(ChannelInOther channelInOther) {
        if (StringUtils.isNotEmpty(channelInOther.getChannel_id()) && StringUtils.isNotEmpty(channelInOther.getChannel_id())) {
            //防止在推流
            sendMessage("", ownerms);
            sendMessage(channelInOther.getChannel_name(), groupnamems);
            stopPlayClose();//停止拉流
            ChannelGroup.setChannel_name(channelInOther.getChannel_name());
            ChannelGroup.setChannel_id(channelInOther.getChannel_id());
            ChannelGroup.setIsChannel(true);
            Log.d(TAG, "进入【ChannelInOther】成功");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void changeToSpeaker() {
        if (playWebRtcUtil != null) {
            playWebRtcUtil.changeToSpeaker();
        }
        Log.d(TAG, "changeToSpeaker: ");
    }

    private void changeNormal() {
        if (playWebRtcUtil != null) {
            playWebRtcUtil.changeNormal();
        }
        Log.d(TAG, "changeNormal: ");
    }

    private void muteAudioRelease() {
        if (pushWebRtcPushUtil != null) {
            pushWebRtcPushUtil.muteAudio(false);
        }
    }

    private void exitChannel() {
        sendMessage(getString(R.string.ppt_channel_name), groupnamems);
        stopPushClose();
        stopPlayClose();

        ChannelGroup.setIsChannel(false);
        ChannelGroup.exitGroup();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

//    @Subscribe
//    public void onEvent(ChannelForward ChannelForward) {
//    }

    //第一步 起始位置  一直现在
    private void ChannelForward(UserInfoDto userInfoDto) {
        ChannelForward channelForward = new ChannelForward();
        channelForward.setAccount(userInfoDto.getAccount());
        channelForward.setChannel_id(ChannelGroup.getChannel_id());
        channelForward.setPhone(userInfoDto.getPhone());
        channelForward.setName(userInfoDto.getName());
        //channelForward.settype(2);
        channelForward.setUser_id(userInfoDto.getId());
        channelForward.setLng(AvEngine.ParamInfo.getLng());
        channelForward.setLat(AvEngine.ParamInfo.getLat());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "ChannelForward");
        jsonObject.put("user_id", userInfoDto.getId());
        jsonObject.put("data", channelForward);

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(HttpDefine.getInstance().wsMsgForwarding())
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response response) throws IOException {
                Log.e(TAG, "onResponse: "+response.body() );
                UploadFileDto uploadFileDto = JSON.parseObject(response.body().string(), UploadFileDto.class);
                Log.e(TAG, "onResponse: "+ uploadFileDto);
            }
            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.e(TAG, "onFailure: "+arg0 );
            }
        });
    }
}