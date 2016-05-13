package teamgodeater.car_net.Data;

import java.io.Serializable;

/**
 * Created by G on 2016/5/7 0007.
 */
public class SearchData implements Serializable {
    private double mLongitude;
    private double mLatitude;
    private boolean mIsLocation;
    private String mName;
    private String mAddrs;

    public double getmLongitude() {
        return mLongitude;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatng(double latitude,double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public boolean ismIsLocation() {
        return mIsLocation;
    }

    public void setmIsLocation(boolean mIsLocation) {
        this.mIsLocation = mIsLocation;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAddrs() {
        return mAddrs;
    }

    public void setmAddrs(String mAddrs) {
        this.mAddrs = mAddrs;
    }

}