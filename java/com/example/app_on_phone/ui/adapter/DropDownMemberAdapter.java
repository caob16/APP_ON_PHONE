package com.example.app_on_phone.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ck.rtcckinfo.user.entity.UserInfoDto;
import com.example.app_on_phone.R;
import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * 通话记录 适配器
 */
public class DropDownMemberAdapter extends EasyAdapter<UserInfoDto> {

    private static final String TAG = "DropDownAdapter";
    Context mContext;


    public DropDownMemberAdapter(@NotNull List data, int mLayoutId,Context context) {

        super(data, mLayoutId);
        mContext=context;
    }

    public DropDownMemberAdapter(@NotNull List<? extends UserInfoDto> data, int mLayoutId) {
        super(data, mLayoutId);
    }

    @Override
    protected void bind(@NotNull ViewHolder viewHolder, UserInfoDto zUserInfoDto, int position) {
        try {
            viewHolder.setText(R.id.id_name, zUserInfoDto.getName());
            viewHolder.setText(R.id.id_phone, zUserInfoDto.getPhone());
            // 0 在线 1不在线
            if (zUserInfoDto.getNow_extension_status() == 0) {
                ((ImageView) viewHolder.getView(R.id.line_statiu)).setBackgroundResource(R.drawable.ic_online);
            } else {
                ((ImageView) viewHolder.getView(R.id.line_statiu)).setBackgroundResource(R.drawable.ic_offline);
            }
            if (zUserInfoDto.isChannel_in()) {
                ((TextView) viewHolder.getView(R.id.id_name)).setTextColor(mContext.getResources().getColor(R.color.white));
                ((TextView) viewHolder.getView(R.id.id_phone)).setTextColor(mContext.getResources().getColor(R.color.white));
            } else {
                ((TextView) viewHolder.getView(R.id.id_name)).setTextColor(mContext.getResources().getColor(R.color.list_context));
                ((TextView) viewHolder.getView(R.id.id_phone)).setTextColor(mContext.getResources().getColor(R.color.list_context));
            }


            ((Button) viewHolder.getView(R.id.tv_video)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    monItemClickListener.channelVideoItemClick(zUserInfoDto);
                }
            });

            ((Button) viewHolder.getView(R.id.tv_audio)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    monItemClickListener.channelAudioItemClick(zUserInfoDto);
                }
            });

            ((Button) viewHolder.getView(R.id.tv_message)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    monItemClickListener.channelImHistory(zUserInfoDto);
                }
            });


        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
        }
    }

    OnItemClickListener monItemClickListener;

    public void setOnItemClickListeners(OnItemClickListener onItemClickListener) {
        monItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void channelVideoItemClick(UserInfoDto userInfoDto);

        void channelAudioItemClick(UserInfoDto userInfoDto);

        void channelImHistory(UserInfoDto userInfoDto);
    }


}

