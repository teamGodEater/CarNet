package teamgodeater.car_net.MapHelp;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import teamgodeater.car_net.Util.LogUtils;

/**
 * Created by G on 2016/5/5 0005.
 */
public class LocationHelp {
    private static LocationClient mLocClient;

    public static void InitLLocationClien(Context c) {
        mLocClient = new LocationClient(c);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);// 打开gps
        option.setCoorType(BDLocation.BDLOCATION_GCJ02_TO_BD09LL); // 设置坐标类型
        option.setScanSpan(10000);//扫描间隔
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(true);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
    }

    public static LocationClient GetLocationClient() {
        return mLocClient;
    }

    public static void Star(BDLocationListener listener) {
        if (listener == null) {
            return;
        }
        if (mLocClient.isStarted()) {
            mLocClient.stop();
        }
        mLocClient.registerLocationListener(listener);
        mLocClient.start();
        mLocClient.requestLocation();
    }

    public static void Stop(){
        if (mLocClient.isStarted()) {
            LogUtils.L("?? "  ," stop ");
            mLocClient.stop();
        }
    }

}
