package teamgodeater.car_net.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.car_net.Data.Ico2LineTipData;
import teamgodeater.car_net.Holder.Ico2LineTipHolder;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Util.LogUtils;
import teamgodeater.car_net.Util.UserDataUtils;

/**
 * Created by G on 2016/5/13 0013.
 */
public class MainBottomViewPagerFramgnt extends Fragment implements ViewPager.OnPageChangeListener {


    @Bind(R.id.Select_1)
    ImageView mSelect1;
    @Bind(R.id.Select_2)
    ImageView mSelect2;
    @Bind(R.id.Vp)
    ViewPager mViewPager;

    private View mView;

    List<View> mRecycleViewList;
    //交通 + 汽车信息  数据
    List<Ico2LineTipData> mInforList;
    RecyclerView mRvInfor;
    //路线信息 数据
    List<Ico2LineTipData> mRouteList;
    RecyclerView mRvRoute;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.framgnet_main_bottom_viewpager, container, false);
        ButterKnife.bind(this, mView);

        mRvInfor = (RecyclerView) inflater.inflate(R.layout.recycleview, null);
        mRvRoute = (RecyclerView) inflater.inflate(R.layout.recycleview, null);
        initList();
        initRecycleView();
        initViewPager();
        return mView;
    }

    private void initRecycleView() {
        initInforData();
        mRvInfor.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvInfor.setAdapter(new inforTipAdapter());
    }

    private void initInforData() {
        Ico2LineTipData data = new Ico2LineTipData();
        data.setmTitleMain("测试标题");
        data.setmTitleTip("测试xiao标题");
        data.setmTip("测试提示");
        data.setmIco(getResources().getDrawable(R.drawable.ic_traffic_primary));
        data.setmTipIco(getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_black_54));
        mInforList.add(data);
    }

    private void initViewPager() {
        changePagerSelectStatus(true);
        mViewPager.setAdapter(new myPagerAdapter());
        mViewPager.addOnPageChangeListener(this);
    }

    private void initList() {
        mRecycleViewList = new ArrayList<>();
        mInforList = new ArrayList<>();
        mRouteList = new ArrayList<>();

        mRecycleViewList.add(mRvInfor);
        mRecycleViewList.add(mRvRoute);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 改变了当前页面
     *
     * @param position 就和游泳一样
     */
    @Override
    public void onPageSelected(int position) {
        changePagerSelectStatus(position == 0);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void changePagerSelectStatus(Boolean isfrist) {
        if (isfrist) {
            mSelect1.setAlpha(0.87f);
            mSelect2.setAlpha(0.54f);
        }else {
            mSelect2.setAlpha(0.87f);
            mSelect1.setAlpha(0.54f);
        }
    }
    private class myPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mRecycleViewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mRecycleViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mRecycleViewList.get(position);
            container.addView(v);
            return v;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    /**
     * 主页信息显示适配器
     */
    private class inforTipAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mV = LayoutInflater.from(getActivity()).inflate(R.layout.rv_ico_2line_tip, parent, false);
            return new Ico2LineTipHolder(mV);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Ico2LineTipHolder hd = (Ico2LineTipHolder) holder;
            hd.BindView(mInforList.get(position),0.87f,0.54f,0.54f);
            hd.SetClickListen(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.L("MainFramgent", "item点击测试");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mInforList.size();
        }

    }
}
