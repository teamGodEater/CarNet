package teamgodeater.car_net.Util;

import android.util.Log;

/**
 * Created by G on 2016/4/11 0011.
 */
public class LogUtils {
    private static boolean IsDebugMode = true;

    public static void SetIsDebugMode(boolean isDebugMode) {
        IsDebugMode = isDebugMode;
    }

    public static void L(Class c, String s) {
        if (IsDebugMode) {
            Log.i(c.getSimpleName(), s);
        }
    }
    public static void L(String tag, String s) {
        if (IsDebugMode) {
            Log.i(tag, s);
        }
    }

}
