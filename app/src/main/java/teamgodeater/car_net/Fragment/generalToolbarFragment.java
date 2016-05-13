package teamgodeater.car_net.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.car_net.Activity.MainActivity;
import teamgodeater.car_net.Attribute.ToolbarAttribute;
import teamgodeater.car_net.Interface.ToolBarInterFace;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Util.LogUtils;
import teamgodeater.car_net.Widget.RippleView;

/**
 * Created by G on 2016/4/23 0023.
 */
public class generalToolbarFragment extends Fragment {

    @Bind(R.id.Back)
    FrameLayout mBack;
    @Bind(R.id.Title)
    TextView mTitle;
    @Bind(R.id.ActionButton)
    RippleView mActionButton;
    @Bind(R.id.RefreshImage)
    ImageView mRefreshImage;
    private View mLayout;
    private ToolBarInterFace mInterface;
    private ToolbarAttribute mAttribute;

    public generalToolbarFragment setAttribute(ToolbarAttribute attribute) {
        mAttribute = attribute;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInterface = (MainActivity) getActivity();
        mLayout = inflater.inflate(R.layout.fragment_toolbar_general, container, false);
        ButterKnife.bind(this, mLayout);

        if (mAttribute != null) {
            if (mAttribute.mTitle != null) {
                mTitle.setText(mAttribute.mTitle);
            }
            if (mAttribute.mRippleViewAttribute != null) {
                mActionButton.setVisibility(View.VISIBLE);
                LogUtils.L("GeneralToolbar","CreteView   Actionbutton");
                mActionButton.SetAttribute(mAttribute.mRippleViewAttribute);
                mActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterface != null) {
                            mInterface.OnActionButtonClick();
                        }
                    }
                });
            }
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.OnbackClick();
                }
            }
        });

        return mLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public RippleView GetActionButton() {
        return mActionButton;
    }

    public void GoneActionButoon (){
        mActionButton.setVisibility(View.GONE);
    }

    public void VisibleActionButtion(){
        mActionButton.setVisibility(View.VISIBLE);
    }
    public void SetTitleText(String s) {
        mTitle.setText(s);
    }

    public TextView GetTitleView() {
        return mTitle;
    }

    public ImageView GeRefreshImage() {
        return mRefreshImage;
    }


}
