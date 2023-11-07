package com.example.app_on_phone.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ck.rtcckinfo.channel.entity.ChannelList;
import com.example.app_on_phone.R;
import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * 通话记录 适配器
 */
public class DropDownChannelAdapter extends EasyAdapter<ChannelList> {

    private static final String TAG = "DropDownAdapter";
    Context mContext;
    private int checkedPosition;


    public DropDownChannelAdapter(@NotNull List data, int mLayoutId) {
        super(data, mLayoutId);
    }

    @Override
    protected void bind(@NotNull ViewHolder viewHolder, ChannelList zgroupDetailInfo, int position) {
        try {
            viewHolder.setText(R.id.tv_text, zgroupDetailInfo.getChannel_name());





            ((RelativeLayout) viewHolder.getView(R.id.ppt_channel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    monItemClickListener.channelItemClick(zgroupDetailInfo);
                }
            });

        } catch (Throwable e) {
            Log.e(TAG, e.getMessage() );
        }
    }

    OnItemClickListener monItemClickListener;

    public void setOnItemClickListeners(OnItemClickListener onItemClickListener) {
        monItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void channelItemClick(ChannelList channelList);
        void channelCheckItemClick(ChannelList channelList, int position);
        void channelImHistory(ChannelList channelList, int position);
    }
}

