package teamgodeater.car_net.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teamgodeater.car_net.R;
import teamgodeater.car_net.Util.LogUtils;

/**
 * Created by G on 2016/4/17 0017.
 */
public class SetingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            LogUtils.L(this.getClass(),"savedInstanceState != null");
        }else {
            LogUtils.L(this.getClass(),"savedInstanceState == null");
        }
        View v = inflater.inflate(R.layout.fragment_seting, container, false);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(this.getClass().getSimpleName(), "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(this.getClass().getSimpleName(), "onSaveInstanceState");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(this.getClass().getSimpleName(), "onSaveInstanceState");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(this.getClass().getSimpleName(), "onCreate");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(this.getClass().getSimpleName(), "onDestroyView");

    }
}
