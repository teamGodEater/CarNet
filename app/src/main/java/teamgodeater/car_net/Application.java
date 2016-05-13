package teamgodeater.car_net;

import com.baidu.mapapi.SDKInitializer;

import g.xdroidrequest.XRequest;
import teamgodeater.car_net.MapHelp.LocationHelp;
import teamgodeater.car_net.Util.Utils;

/**
 * Created by G on 2016/4/11 0011.
 */
public class Application extends android.app.Application {

    public static Application app;
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        LocationHelp.InitLLocationClien(this);
        XRequest.initXRequest(this);
        Utils.Initialize(this);
        app = this;
    }

    public static Application GetApp(){
        return app;
    }
}
