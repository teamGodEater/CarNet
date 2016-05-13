package teamgodeater.car_net.Data;

import android.graphics.drawable.Drawable;

/**
 * Created by G on 2016/5/13 0013.
 */
public class Ico2LineTipData {
    Drawable mIco;
    Drawable mOvalPoint;
    Drawable mTipIco;
    String mTitleMain;
    String mTitleTip;
    String mTip;
    boolean mHasDivider = true;

    public Drawable getmIco() {
        return mIco;
    }

    public void setmIco(Drawable mIco) {
        this.mIco = mIco;
    }

    public Drawable getmOvalPoint() {
        return mOvalPoint;
    }

    public void setmOvalPoint(Drawable mOvalPoint) {
        this.mOvalPoint = mOvalPoint;
    }

    public Drawable getmTipIco() {
        return mTipIco;
    }

    public void setmTipIco(Drawable mTipIco) {
        this.mTipIco = mTipIco;
    }

    public String getmTitleMain() {
        return mTitleMain;
    }

    public void setmTitleMain(String mTitleMain) {
        this.mTitleMain = mTitleMain;
    }

    public String getmTitleTip() {
        return mTitleTip;
    }

    public void setmTitleTip(String mTitleTip) {
        this.mTitleTip = mTitleTip;
    }

    public String getmTip() {
        return mTip;
    }

    public void setmTip(String mTip) {
        this.mTip = mTip;
    }

    public boolean ismHasDivider() {
        return mHasDivider;
    }

    public void setmHasDivider(boolean mHasDivider) {
        this.mHasDivider = mHasDivider;
    }
}
