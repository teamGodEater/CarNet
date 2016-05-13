package teamgodeater.car_net.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Util.Utils;

/**
 * Created by G on 2016/5/9 0009.
 */
public class LauncherActivity extends AppCompatActivity {


    @Bind(R.id.TimeTip)
    TextView mTimeTip;
    @Bind(R.id.Ico)
    ImageView mIco;
    @Bind(R.id.Motto)
    TextView mMotto;
    @Bind(R.id.Version)
    TextView mVersion;
    @Bind(R.id.Logo_Contain)
    LinearLayout mLogoContain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);

        String t1 = getDate();
        mTimeTip.setText(t1 + "\n让出行更美好");

        mTimeTip.setAlpha(0f);
        mTimeTip.animate().alpha(1f).setDuration(500).setStartDelay(300).start();

        mLogoContain.setTranslationY(Utils.dip2px(80f));
        mLogoContain.setAlpha(0f);
        mLogoContain.animate().alpha(1f).setDuration(500).translationY(0f).start();


        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        String t = format.format(new Date());
        return t;
    }

}
