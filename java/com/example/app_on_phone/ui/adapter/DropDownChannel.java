package com.example.app_on_phone.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ck.rtcckinfo.channel.entity.ChannelList;
import com.example.app_on_phone.R;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.ArrayList;
import java.util.List;
public class DropDownChannel extends BottomPopupView {
    RecyclerView recyclerView;
    TextView btn_cancel;
    TextView tv_title;
    private static final String TAG = "DropDownBoxAdapter";
    protected int bindLayoutId;
    protected int bindItemLayoutId;
    private Context mContext;
    private List<ChannelList> groupDetailInfoList = new ArrayList<>();

    public DropDownChannel(@NonNull Context nContext) {
        super(nContext);
        mContext=nContext;
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @return
     */
    public DropDownChannel bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public DropDownChannel bindItemLayout(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout.popwin_bottomcomm_select : bindLayoutId;
    }

    int lastPosition;
    private boolean isFalse = false;
    private String lastChannlId;

    @Override
    protected void initPopupContent() {

        super.initPopupContent();

        recyclerView = findViewById(R.id.recyclerView);
        tv_title = findViewById(R.id.tv_title);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (tv_title != null) {
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

        DropDownChannelAdapter mDropDownAdapter = new DropDownChannelAdapter(data, R.layout.popwin_bottomitem_channel);
        mDropDownAdapter.setOnItemClickListeners(new DropDownChannelAdapter.OnItemClickListener() {
            @Override
            public void channelItemClick(ChannelList mdata) {
                try {
                    Log.e(TAG, "onvideoItemClick: " + mdata);
                    if (selectListener != null) {
                        selectListener.onSelectGroup(mdata);
                    }
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (popupInfo.autoDismiss) dismiss();
                        }
                    }, 100);
                } catch (Exception e) {
                    Log.e(TAG, ": "+e.getMessage());;
                }

            }

            @Override
            public void channelCheckItemClick(ChannelList mdata, int position) {


            }

            @Override
            public void channelImHistory(ChannelList groupDetailInfo, int position) {
                Log.e(TAG, "channelImHistory: "+groupDetailInfo+"==="+position );


            }

        });
        recyclerView.setAdapter(mDropDownAdapter);


    }

    String title;
    List<ChannelList> data;
    int[] iconIds;

    public DropDownChannel setStringData(String title, List<ChannelList> data, int[] iconIds) {
        this.title = title;
        this.data = data;
        this.groupDetailInfoList = data;

        this.iconIds = iconIds;
        return this;
    }

    private OnBottomSelectGroupListener selectListener;

    public DropDownChannel setOnBottomSelectListener(OnBottomSelectGroupListener selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    public interface OnBottomSelectGroupListener {

        void onSelectGroup(ChannelList mdata);
    }


    int checkedPosition = -1;

    /**
     * 设置默认选中的位置
     *
     * @param position
     * @return
     */
    public DropDownChannel setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }



}
