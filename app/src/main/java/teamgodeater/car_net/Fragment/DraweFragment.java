package teamgodeater.car_net.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.baidu.location.LocationClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.car_net.MapHelp.LocationHelp;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Util.LogUtils;
import teamgodeater.car_net.Widget.RippleView;
import teamgodeater.car_net.Widget.RoundImageView;

/**
 * Created by G on 2016/4/13 0013.
 */
public class DraweFragment extends Fragment {
    @Bind(R.id.HeadImage)
    RoundImageView HeadImage;
    @Bind(R.id.Greetings)
    TextView Greetings;
    @Bind(R.id.TitleMain)
    TextView Location;
    @Bind(R.id.HeadContain)
    FrameLayout HeadContain;
    @Bind(R.id.RecyclerView)
    android.support.v7.widget.RecyclerView RecyclerView;
    @Bind(R.id.Set)
    RippleView mSet;
    @Bind(R.id.Fnish)
    RippleView mFnish;
    private View mView;
    //点击设置 或者 退出 app 功能
    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.Set) {
                int alpha = HeadContain.getBackground().getAlpha();

                LogUtils.L("gggg", "click" + "headContentAlpha" + alpha);
                HeadContain.getBackground().setAlpha(255);
            } else {

            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_drawer, container, false);
        LogUtils.L(this.getClass(), "oncreteview");
        ButterKnife.bind(this, mView);
        mSet.setOnClickListener(mListener);
        RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final List<ItemData> dataList = new ArrayList<>();
        dataList.add(new ItemData(R.drawable.ic_home_primery, "主页面", "", 0, HIDE));
        dataList.add(new ItemData(R.drawable.ic_directions_car_primary, "车辆信息", "当前京888888", 0, HIDE));
        dataList.add(new ItemData(R.drawable.ic_assignment_primery, "违章查询", "", 0, HIDE));
        dataList.add(new ItemData(R.drawable.ic_assignment_ind_white, "我的订单", "", 3, HIDE));
        dataList.add(new ItemData(R.drawable.ic_play_circle_filled_primary, "我的音乐", "", 0, HIDE));
        dataList.add(new ItemData(R.drawable.ic_audiotrack_primary, "自动播放", "", 0, OK));

        RecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = inflater.inflate(R.layout.rv_drawer_menu, parent, false);
                return new holder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
                holder hd = (holder) h;
                hd.BindView(position);

            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }

            class holder<T extends Fragment> extends android.support.v7.widget.RecyclerView.ViewHolder {
                public holder(View v) {
                    super(v);
                    this.v = v;
                    this.im_ico = (ImageView) v.findViewById(R.id.Ico);
                    this.tv_title = (TextView) v.findViewById(R.id.Title);
                    this.tv_tip = (TextView) v.findViewById(R.id.Tip);
                    this.tv_tipNum = (TextView) v.findViewById(R.id.Tv_TipNum);
                    this.sw_state = (Switch) v.findViewById(R.id.Switch);
                    this.bg = v.findViewById(R.id.Bg);
                    this.divider = v.findViewById(R.id.Divider);
                }

                public void BindView(final int position) {
                    ItemData data = dataList.get(position);
                    im_ico.setImageResource(data.GetIco());
                    tv_title.setText(data.GetTitle());
                    if (data.GetTipStr().trim().isEmpty()) {
                        tv_tip.setVisibility(View.GONE);
                    } else {
                        tv_tip.setVisibility(View.VISIBLE);
                        tv_tip.setText(data.GetTipStr());
                    }
                    if (position == 3) {
                        divider.setVisibility(View.VISIBLE);
                    } else {
                        divider.setVisibility(View.GONE);
                    }
                    if (data.GetTipNum() <= 0) {
                        tv_tipNum.setVisibility(View.GONE);
                    } else {
                        tv_tipNum.setVisibility(View.VISIBLE);
                        tv_tipNum.setText(String.valueOf(data.GetTipNum()));
                    }
                    if (data.GetState() == HIDE) {
                        sw_state.setVisibility(View.GONE);
                        bg.setVisibility(View.VISIBLE);
                        bg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocationClient locationClient = LocationHelp.GetLocationClient();
                                switch (position) {
                                    case 0:
                                        LogUtils.L("DraweFramgent","is star : " + locationClient.isStarted());
                                        break;
                                    case 1:
                                        locationClient.start();
                                        break;
                                    case 2:
                                        LogUtils.L("DraweFramgent"," requestLocation : " + locationClient.requestLocation());

                                        break;

                                }
                            }
                        });

                    } else {
                        sw_state.setVisibility(View.VISIBLE);
                        bg.setVisibility(View.GONE);
                        sw_state.setChecked((data.GetState() == 1 ? true : false));
                    }
                }

                public View GetView() {
                    return v;
                }

                View v;
                ImageView im_ico;
                TextView tv_title;
                TextView tv_tip;
                TextView tv_tipNum;
                Switch sw_state;
                View bg;
                View divider;
            }
        });
        return mView;
    }

    private final int OK = 1, NO = 2, HIDE = 3;

    public class ItemData {
        public ItemData(int ico, String title, String tipStr, int tipNum, int state) {
            this.ico = ico;
            this.title = title;
            this.tipStr = tipStr;
            this.tipNum = tipNum;
            this.state = state;
        }

        int ico;

        public int GetIco() {
            return ico;
        }

        public String GetTitle() {
            return title;
        }

        public String GetTipStr() {
            return tipStr;
        }

        public int GetTipNum() {
            return tipNum;
        }

        public int GetState() {
            return state;
        }

        String title;
        String tipStr;
        int tipNum;
        int state;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.L(this.getClass(), "onDestroyView");
        ButterKnife.unbind(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.L(this.getClass(), "onSaveInstanceState");

    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.L(this.getClass(), "onStop");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.L(this.getClass(), "onDetach");

    }
}
