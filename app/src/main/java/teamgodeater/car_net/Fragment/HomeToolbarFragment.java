package teamgodeater.car_net.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.car_net.Activity.MainActivity;
import teamgodeater.car_net.Interface.ToolBarInterFace;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Widget.RippleView;

/**
 * Created by G on 2016/4/18 0018.
 */
public class HomeToolbarFragment extends Fragment {
    @Bind(R.id.DrawerMenu)
    RippleView mDrawerMenu;
    @Bind(R.id.Search)
    View mSearch;
    @Bind(R.id.Mic)
    RippleView mMic;
    private ToolBarInterFace mInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInterface = (MainActivity)getActivity();
        View V = inflater.inflate(R.layout.fragment_toolbar_home, container, false);
        ButterKnife.bind(this, V);
        mDrawerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.OnDrawerClick();
                }
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.OnSearchClick();
                }
            }
        });
        mMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.OnMicClick();
                }
            }
        });
        return V;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
