package com.example.app_on_phone.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ck.rtcckinfo.call.CallPhone;
import com.ck.rtcckinfo.user.entity.UserInfoDto;
import com.ck.rtcckinfo.user.entity.UserInfoGobal;
import com.example.app_on_phone.R;
import com.example.app_on_phone.signin.ToastUtils;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.ArrayList;
import java.util.List;

public class DropDownMember extends BottomPopupView {
    RecyclerView recyclerView;
    TextView btn_cancel;
    TextView tv_title;
    private static final String TAG = "DropDownBoxAdapter";
    protected int bindLayoutId;
    protected int bindItemLayoutId;
    private Context mContext;
    private List<UserInfoDto> userInfoDtoList = new ArrayList<>();
    private String title;
    private List<UserInfoDto> data;
    private int[] iconIds;

    public DropDownMember(@NonNull Context nContext) {
        super(nContext);
        mContext=nContext;
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @return
     */
    public DropDownMember bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public DropDownMember bindItemLayout(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout.popwin_bottomcomm_select : bindLayoutId;
    }

   public static DropDownMemberAdapter mDropDownAdapter=null;

    @Override
    protected void initPopupContent() {

        super.initPopupContent();
        recyclerView = findViewById(R.id.recyclerView);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tv_title = findViewById(R.id.tv_title);

        if (tv_title != null) {
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

        mDropDownAdapter = new DropDownMemberAdapter(data, R.layout.popwin_bottomitem_member,mContext);
        mDropDownAdapter.setOnItemClickListeners(new DropDownMemberAdapter.OnItemClickListener() {




            @Override
            public void channelVideoItemClick(UserInfoDto userInfoDto) {
                if (UserInfoGobal.getPhone().equals(userInfoDto.getPhone())) {
                    ToastUtils.show(mContext.getString(R.string.callkeyboard_video_hint));
                    return;
                }
                CallPhone.callerCall(getContext(),  userInfoDto.getPhone(),true);
            }

            @Override
            public void channelAudioItemClick(UserInfoDto userInfoDto) {
                if (UserInfoGobal.getPhone().equals(userInfoDto.getPhone())) {
                    ToastUtils.show(mContext.getString(R.string.callkeyboard_audio_hint));
                    return;
                }
                CallPhone.callerCall(getContext(),  userInfoDto.getPhone(),false);
            }

            @Override
            public void channelImHistory(UserInfoDto userInfoDto) {
                if (UserInfoGobal.getPhone().equals(userInfoDto.getPhone())) {
                    ToastUtils.show(mContext.getString(R.string.callkeyboard_message_hint));
                    return;
                }

               // MessageUtil.SingleChat(mContext,userInfoDto.getId(),userInfoDto.getName(),userInfoDto.getHeadimg(),userInfoDto.getPhone());

            }


        });
        recyclerView.setAdapter(mDropDownAdapter);


    }


    public DropDownMember setStringData(String title, List<UserInfoDto> data, int[] iconIds) {
        this.title = title;
        this.data = data;
        this.userInfoDtoList = data;
        this.iconIds = iconIds;
        return this;
    }

    private OnBottomSelectGroupListener selectListener;

    public DropDownMember setOnBottomSelectListener(OnBottomSelectGroupListener selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    public interface OnBottomSelectGroupListener {

        void onSelectGroup(UserInfoDto mdata);
    }


    int checkedPosition = -1;

    /**
     * 设置默认选中的位置
     *
     * @param position
     * @return
     */
    public DropDownMember setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }




    public void  remove(){
        mDropDownAdapter.getData().remove(1);
    }

}
