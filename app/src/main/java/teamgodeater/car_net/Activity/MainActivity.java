package teamgodeater.car_net.Activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.car_net.Fragment.DraweFragment;
import teamgodeater.car_net.Fragment.GeneralBaseFragment;
import teamgodeater.car_net.Fragment.HomeToolbarFragment;
import teamgodeater.car_net.Fragment.MainFragment;
import teamgodeater.car_net.Fragment.SearchFragment;
import teamgodeater.car_net.Fragment.generalToolbarFragment;
import teamgodeater.car_net.Interface.ToolBarInterFace;
import teamgodeater.car_net.MapHelp.LocationHelp;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Util.LogUtils;
import teamgodeater.car_net.Util.Utils;

public class MainActivity extends AppCompatActivity implements ToolBarInterFace {


    @Bind(R.id.MainContain)
    FrameLayout getmMainContain;
    @Bind(R.id.ToolBarContain)
    FrameLayout mToolBarContain;
    @Bind(R.id.StatusBarView)
    View mStatusBarView;
    @Bind(R.id.MainLayout)
    FrameLayout mMainLayout;
    @Bind(R.id.DrawerContain)
    FrameLayout mDrawerContain;
    @Bind(R.id.DrawerLayout)
    android.support.v4.widget.DrawerLayout mDrawerLayout;
    private SDKReceiver mReceiver;
    private boolean mIsFritEntry = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mIsFritEntry) {
            mIsFritEntry = false;
        }else {
            getSupportFragmentManager().popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        //个性化地图
        CustomMapView();
        //不允许旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //状态栏 透明
        TransparentStatusBar();
        //初始化Status
        mStatusBarView.getLayoutParams().height = GetStatuBarHeight();
        //初始化工具栏位置
        mToolBarContain.setTranslationY(GetStatuBarHeight());
        SwitchFragment(new MainFragment());

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        LocationHelp.Stop();
    }

    @Override
    public void OnbackClick() {
        onBackPressed();
    }

    @Override
    public void OnDrawerClick() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void OnActionButtonClick() {
        Fragment fragmentById = getSupportFragmentManager().findFragmentById(R.id.MainContain);
        if (fragmentById instanceof GeneralBaseFragment) {
            ((GeneralBaseFragment)fragmentById).OnActionButtonClick();
        }
    }

    @Override
    public void OnSearchClick() {
        SwitchFragment(new SearchFragment());
    }

    @Override
    public void OnMicClick() {
    }

    //SDK授权状态广播接收者
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            LogUtils.L(MainActivity.class, "action" + s);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                LogUtils.L(MainActivity.class, "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                LogUtils.L(MainActivity.class, "key 验证成功! 功能可以正常使用");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                LogUtils.L(MainActivity.class, "网络出错");
            }
        }
    }


    // 获得状态栏高度
    public int GetStatuBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }


    //5.0以上 statusbar 不透明度20% 以下跟随系统(默认40%）
    public void TransparentStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.argb(51, 0, 0, 0));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /*
    显示自定义百度地图
    */
    private void CustomMapView() {
        File f = Utils.Assest2Phone("custom_config(night).txt");
        MapView.setCustomMapStylePath(f.getAbsolutePath());
    }

    /*
      注册百度地图SDk状态广播接受者
      */
    private void registerReceiver() {
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        if (mReceiver == null) {
            mReceiver = new SDKReceiver();
        }
        registerReceiver(mReceiver, iFilter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver();
    }

    public void SwitchFragment(Fragment to) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment from = fragmentManager.findFragmentById(R.id.MainContain);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (from == null) {
            LogUtils.L(this.getClass(), "SwitchFragment from = null");
            transaction.replace(R.id.DrawerContain, new DraweFragment());
            transaction.replace((R.id.ToolBarContain), new HomeToolbarFragment());
            transaction.replace(R.id.MainContain, new MainFragment());
            transaction.commit();
            return;
        }

        if (from.getClass() != to.getClass()) {
            LogUtils.L("SwitchFragment" ,"To class name "  + to.getClass().getSimpleName());
            if (to instanceof MainFragment) {
                LogUtils.L(this.getClass(), "SwitchFragment to = MainFragment");
                transaction.replace((R.id.ToolBarContain), new HomeToolbarFragment());
            } else {
                LogUtils.L(this.getClass(), "SwitchFragment to ! = MainFragment");

                transaction.replace((R.id.ToolBarContain), new generalToolbarFragment().setAttribute(((GeneralBaseFragment) to).GetToolBarAttribute()));
            }
                transaction.replace(R.id.MainContain, to).addToBackStack(null).commit();
        }
    }

    public void BelowToolbar(View view) {
        view.setPadding(0, GetStatuBarHeight() + mToolBarContain.getHeight(), 0, 0);
    }

    public void SetToolBarPosition(int y) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.ToolBarContain);
        if (f != null) {
            f.getView().setTranslationY(y);
        }
    }

    public void SetToolbarAlpha(float a) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.ToolBarContain);
        if (f != null) {
            Drawable background = f.getView().getBackground();
            if (background == null) {
                f.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            f.getView().getBackground().mutate().setAlpha((int) (a * 255));

        }
    }

    public Fragment GetToolBarFragemtn() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.ToolBarContain);
        return f;
    }

    public void SetStatusBarAlpha(float a) {
        LogUtils.L(this.getClass(), "SetStatusBarAlpha " + a);
        mStatusBarView.getBackground().mutate().setAlpha((int) (a * 255));
    }

    private long mBackPressdTime = 0;

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.MainContain);
        if (fragment instanceof  GeneralBaseFragment && !((GeneralBaseFragment) fragment).CanPopUp()) {
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        long pressdtime = System.currentTimeMillis();

        if (pressdtime - mBackPressdTime < 2000) {
            finish();
            return;
        }
        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        mBackPressdTime = pressdtime;
    }

    //Hide input when click others
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (Utils.IsShouldRemoveFocus(v, ev)) {
                v.clearFocus();
                LogUtils.L("dispatchTouchEvent", "cleanfocus");
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainFragment.mView = null;
    }
}
