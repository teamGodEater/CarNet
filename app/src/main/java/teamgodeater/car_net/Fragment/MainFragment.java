package teamgodeater.car_net.Fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.car_net.Activity.MainActivity;
import teamgodeater.car_net.MapHelp.LocationHelp;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Util.LogUtils;
import teamgodeater.car_net.Util.Utils;

/**
 * Created by G on 2016/4/13 0013.
 */
public class MainFragment extends Fragment implements BDLocationListener, BaiduMap.OnMapLoadedCallback, BaiduMap.OnMapTouchListener, View.OnClickListener {

    static public View mView;
    boolean mIsFirstRequestLocation = true;
    Marker mLocMarker;
    BaiduMap mMap;
    boolean mIsLoaded;
    boolean mIsRequestLocation;



    @Bind(R.id.Map)
    MapView mMapView;
    @Bind(R.id.BottomContain)
    FrameLayout mBottomContain;
    @Bind(R.id.MyLocation)
    ImageView mMyLocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_main, container, false);
            mIsFirstRequestLocation = true;
        }

        ButterKnife.bind(this, mView);
        getMainActivity().SetStatusBarAlpha(0.5f);
        initMap();
        initBottom();
        return mView;
    }

    private void initBottom() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        childFragmentManager.beginTransaction().replace(R.id.BottomContain, new MainBottomViewPagerFramgnt()).commit();
    }


    private void initMap() {
        //获取Baidumap属性
        mMap = mMapView.getMap();
        //不显示Poi信息
        mMap.showMapPoi(false);
        //地图加载完毕回调
        mMap.setOnMapLoadedCallback(this);
        //触摸回调
        mMap.setOnMapTouchListener(this);
        //设置logo位置
        mMapView.setLogoPosition(LogoPosition.logoPostionCenterTop);
        //不显示ZoomControl
        mMapView.showZoomControls(false);
        //不显示mylocation view
        mMyLocation.setVisibility(View.INVISIBLE);
        mMyLocation.setOnClickListener(this);
        mBottomContain.addOnLayoutChangeListener(new mOnBootomLayoutChange());
    }




    //地图加载完毕回调
    @Override
    public void onMapLoaded() {
        mIsLoaded = true;
        //指南针位置
        mMap.setCompassPosition(new Point(Utils.dip2px(36), Utils.dip2px(100)));
    }


    boolean mIsTouchMap;

    //地图触摸回调
    @Override
    public void onTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMapView.showZoomControls(true);
                mMyLocation.setVisibility(View.VISIBLE);
                mIsTouchMap = true;
                break;
            case MotionEvent.ACTION_MOVE:
                mIsTouchMap = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsTouchMap = false;
                hideZoomControlDelay();
                break;
        }
    }

    int mHideZoomHandleCount = 0;

    //2s后隐藏ZoomControl
    private void hideZoomControlDelay() {
        mHideZoomHandleCount++;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHideZoomHandleCount--;
                if (!mIsTouchMap && mHideZoomHandleCount == 0) {
                    mMyLocation.setVisibility(View.INVISIBLE);
                    mMapView.showZoomControls(false);
                }
            }
        }, 3000);
    }


    //按钮点击
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.MyLocation) {
            mIsRequestLocation = true;
            LocationHelp.GetLocationClient().requestLocation();
        }
    }

    //缩放控制器伴随底部位置改变
    private class mOnBootomLayoutChange implements View.OnLayoutChangeListener {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            Point scaleControlPoint = new Point(left, top - mMapView.getScaleControlViewHeight() - Utils.dip2px(6));
            Point ZoomControlsPoint = new Point(right - Utils.dip2px(40), top / 2);
            mMapView.setZoomControlsPosition(ZoomControlsPoint);
            mMapView.setScaleControlPosition(scaleControlPoint);
        }
    }

    /**
     * 普通的得到MainActivity
     * @return
     */
    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        LocationHelp.Star(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        LocationHelp.Stop();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.L("onDestroy", "onde66s");
        mMapView.onDestroy();
    }

    //接收到location信息回调
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        //判断定位Marker对象是否创建
        if (mLocMarker == null) {
            ArrayList<BitmapDescriptor> giflist = new ArrayList<>();
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_0));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_1));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_2));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_3));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_4));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_5));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_5));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_4));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_3));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_2));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_1));
            giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_0));

            OverlayOptions ooD = new MarkerOptions().position(latLng).icons(giflist).zIndex(0).period(8);
            mLocMarker = (Marker) (mMap.addOverlay(ooD));
        } else {
            mLocMarker.setPosition(latLng);
        }

        //移动到定位位置
        //第一次进入或者主动请求定位
        if (mIsFirstRequestLocation || mIsRequestLocation) {
            mIsRequestLocation = false;
            mIsFirstRequestLocation = false;
            moveToMyLocation(latLng);
        }
    }

    //获取除去toolbar和bootombar 后的屏幕中心点位置
    private Point getCenterScreenPoint() {
        return new Point(mView.getWidth() / 2, mView.getHeight() / 2 - (mView.getHeight() - mBottomContain.getTop()) / 2 + Utils.dip2px(25));
    }

    private void moveToMyLocation(final LatLng latLng) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsLoaded) {
                    Point center = getCenterScreenPoint();
                    MapStatus status = new MapStatus.Builder().target(latLng).targetScreen(center).zoom(15).build();
                    MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(status);
                    mMap.animateMapStatus(mapStatusUpdate, 500);
                } else {
                    moveToMyLocation(latLng);
                }
            }
        }, 100);
    }


}
