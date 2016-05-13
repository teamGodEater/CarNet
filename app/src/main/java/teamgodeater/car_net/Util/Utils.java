package teamgodeater.car_net.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import teamgodeater.car_net.R;

/**
 * Created by G on 2016/4/11 0011.
 */
public class Utils {
    private static String mSavePath;

    public static Context GetContext() {
        return mContext;
    }

    private static Context mContext;
    private static float mScale;


    public static void Initialize(android.content.Context context) {
        mContext = context;
        mSavePath = context.getFilesDir().getPath();
        mScale = context.getResources().getDisplayMetrics().density;
    }

    public static String getmSavePath() {
        return mSavePath;
    }

    /**
     * 执行拷贝任务
     *
     * @param assest 需要拷贝的assets文件路径
     * @return 拷贝成功后的目标文件句柄
     * @throws IOException
     */
    public static File Assest2Phone(String assest) {
        File file = new File(mSavePath, assest);
        FileOutputStream fos = null;
        InputStream is = null;

        try {
            is = mContext.getAssets().open(assest);
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();//刷新缓冲区
            LogUtils.L(file.getClass(), assest + " 复制到内存成功");
        } catch (IOException e) {
            LogUtils.L(file.getClass(), assest + " 写入文件失败");
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static boolean IsShouldRemoveFocus(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static void HideInputMethoe(View v) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static SpannableStringBuilder SpannableString(String all, String hightLight) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(all);
        int star = all.indexOf(hightLight);
        int end = star + hightLight.length();
        if (star >= 0) {
            spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorPrimary)), star, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        return (int) (dpValue * mScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        return (int) (pxValue / mScale + 0.5f);
    }


    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!extraInfo.isEmpty()) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    public static String Object2String(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String out;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(o);
            out = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        } catch (IOException e) {
            out = "fuck u";
            e.printStackTrace();
        }
        return out;
    }

    public static Object String2Object(String s) {
        byte[] stringToBytes = Base64.decode(s, Base64.DEFAULT);
        ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
        ObjectInputStream is = null;
        Object readObject = null;

        try {
            is = new ObjectInputStream(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回反序列化得到的对象
        try {
            readObject = is.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readObject;
    }

    public static void ShowToast(String s) {
        Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
    }

    public static int GetColorById(int id){
       return  mContext.getResources().getColor(id);
    }

}
