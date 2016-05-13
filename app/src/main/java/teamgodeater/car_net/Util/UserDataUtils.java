package teamgodeater.car_net.Util;

import g.xdroidrequest.XRequest;
import g.xdroidrequest.impl.OnRequestListenerAdapter;

/**
 * Created by G on 2016/5/12 0012.
 */
public class UserDataUtils {

    static final String SERVICEURI = "http://127.0.0.1:880/";
    static boolean mIsloging = false;

    public static boolean IsLoging() {
        return mIsloging;
    }

    public static void Login(OnRequestListenerAdapter la,String username, String passwd) {
        String uri = SERVICEURI + "login?username=" + username + "&passwd=" + passwd;
        XRequest.getInstance().sendGet("login", uri, la);
    }



}
