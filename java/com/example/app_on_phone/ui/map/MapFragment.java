package com.example.app_on_phone.ui.map;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.ck.rtcckinfo.channel.entity.ChannelForward;
import com.example.app_on_phone.R;
import com.example.app_on_phone.databinding.FragmentMapBinding;
import com.example.app_on_phone.signin.util.MapUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MapFragment extends Fragment {
    private FragmentMapBinding binding;
    private MapView mMapView = null;
    //初始化地图控制器对象
    private AMap aMap;
    private Double startLng = 116.397477;
    private Double startLat = 39.908692;
    private Double endLng = 118.793881;
    private Double endLat = 31.918242;
    private MarkerOptions markerOption;
    private Marker startMarker;
    private Marker endMarker;
    private LatLng latlngStart = new LatLng(startLat, startLng);
    private LatLng latlngEnd = new LatLng(endLat, endLng);

    private CameraPosition cameraPosition;
    private Circle circle;
    private static final double radius = 100;

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0:
                    ChannelForward channelForward = (ChannelForward) msg.obj;
                    if (latlngStart == null) {
                            latlngStart = new LatLng(Double.valueOf(channelForward.getLat()), Double.valueOf(channelForward.getLng()));
                        addMarkersStartToMap();
                    }else{
                        latlngStart = new LatLng(Double.valueOf(channelForward.getLat()), Double.valueOf(channelForward.getLng()));
                        startMarker.setPosition(latlngStart);
                        cameraPosition = aMap.getCameraPosition();
                        changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlngStart, cameraPosition.zoom, cameraPosition.tilt, cameraPosition.bearing)));
                    }
                    if(binding.destinySwitch.isChecked()){
                        boolean inCircle = MapUtils.isInCircle(radius, Double.valueOf(channelForward.getLat()), Double.valueOf(channelForward.getLng()), Double.valueOf(endLat), Double.valueOf(endLng));
                        if (inCircle) {
                            Toast.makeText(getContext(), "已经到达终点", Toast.LENGTH_LONG).show();
                        }
                    }
                    Log.e("Received$",Double.valueOf(channelForward.getLat())+"");
                    Log.e("Received$",Double.valueOf(channelForward.getLng())+"");
                    break;
            }
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize the binding
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mMapView = binding.amapView;

        mMapView.onCreate(savedInstanceState);

        binding.destinySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addMarkersEndToMap();
                    addCircle();
                } else {
                    deleteMarkersEndToMap();
                    deleteCircle();
                }
            }
        });

        EventBus.getDefault().register(this);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        MapsInitializer.updatePrivacyShow(getContext(), true, true);
        MapsInitializer.updatePrivacyAgree(getContext(), true);
        changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlngStart, 16, 34, 34)));
        addMarkersStartToMap();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void addCircle(){
        if(circle == null){
            circle = aMap.addCircle(new CircleOptions().center(latlngEnd).radius(radius).strokeColor(Color.argb(0, 255, 0, 0)).fillColor(Color.argb(50, 255, 0, 0)));
        }
    }

    private void deleteCircle(){
        if(circle!= null){
            circle.remove();
            circle = null;
        }
    }

    private void addMarkersStartToMap() {
        if (latlngStart != null) {
            markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_start))
                    .position(latlngStart)
                    .draggable(true);
            startMarker = aMap.addMarker(markerOption);
        }
    }
    private void addMarkersEndToMap() {
        if (latlngEnd != null) {
            markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_end))
                    .position(latlngEnd)
                    .draggable(true);
            endMarker = aMap.addMarker(markerOption);
        }
    }
    private void deleteMarkersStartToMap() {
        if (startMarker != null) {
            startMarker.remove();
        }
    }
    private void deleteMarkersEndToMap() {
        if (endMarker != null) {
            endMarker.remove();
        }
    }
    private void changeCamera(CameraUpdate update) {
        aMap.moveCamera(update);
    }

    @Subscribe
    public void onEvent(ChannelForward channelForward) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = channelForward;
        if(channelForward.getType().equals("1")){
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}