package teamgodeater.car_net.Fragment;

import android.support.v4.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import teamgodeater.car_net.Activity.MainActivity;
import teamgodeater.car_net.Attribute.ToolbarAttribute;
import teamgodeater.car_net.R;

/**
 * Created by G on 2016/5/3 0003.
 */
public abstract class GeneralBaseFragment extends Fragment {

    public boolean CanPopUp() {
        return true;
    }

    public abstract ToolbarAttribute GetToolBarAttribute();

    protected generalToolbarFragment getToolbar() {
        Fragment g = getMainActivity().GetToolBarFragemtn();
        if (g instanceof generalToolbarFragment) {
            return (generalToolbarFragment) g;
        }
        return null;
    }

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected int TOOLBAR_NOMAL = 1;
    protected int TOOLBAR_REFLASH = 2;
    protected int mToolbarState = TOOLBAR_NOMAL;

    protected void setToolbarIsRefresh(int state) {
        if (mToolbarState == state){
            return;
        }
        mToolbarState = state;
        generalToolbarFragment toolbar = getToolbar();
        TextView titleview = toolbar.GetTitleView();
        ImageView midimage = toolbar.GeRefreshImage();

        midimage.setImageResource(R.drawable.ic_refresh_white);
        if (state == TOOLBAR_NOMAL) {
            midimage.clearAnimation();
            midimage.setAlpha(0f);
            titleview.setAlpha(1f);
        } else if (state == TOOLBAR_REFLASH) {
            midimage.setAlpha(0.87f);
            titleview.setAlpha(0f);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_always);
            animation.setInterpolator(new LinearInterpolator());
            midimage.startAnimation(animation);
        }
    }
    public abstract void OnActionButtonClick();

}
