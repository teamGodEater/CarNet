package teamgodeater.car_net.Fragment;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.car_net.Attribute.RippleViewAttribute;
import teamgodeater.car_net.Attribute.ToolbarAttribute;
import teamgodeater.car_net.Data.SearchData;
import teamgodeater.car_net.MapHelp.LocationHelp;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Util.AnimatorUtils;
import teamgodeater.car_net.Util.LogUtils;
import teamgodeater.car_net.Util.Utils;
import teamgodeater.car_net.Widget.RippleView;

/**
 * Created by G on 2016/4/24 0024.
 */
public class SearchFragment extends GeneralBaseFragment implements BDLocationListener {

    @Bind(R.id.From)
    EditText mFrom;
    @Bind(R.id.To)
    EditText mTo;
    @Bind(R.id.ActionButton)
    RippleView mActionButton;
    @Bind(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.Top_Layout)
    LinearLayout mTopLayout;
    @Bind(R.id.Point2Point)
    LinearLayout mPoint2Point;
    @Bind(R.id.Divider)
    View mDivider;

    private View mView;
    private List<teamgodeater.car_net.Data.SearchData> mHistoryList;
    private List<teamgodeater.car_net.Data.SearchData> mSuggestList;
    private List<routeData> mResultList;
    private SuggestionSearch mSuggestionSearch;
    private RoutePlanSearch mRoutePlanSearch;

    private PlanNode mFromPlanNode;
    private PlanNode mToPlanNode;

    private boolean mHasTopHide = false;

    /*
       1.搜索建议
       2.输入时开始搜索
       3.搜索中途显示刷新状态 √
       4.网络状态监测
       5.将结果返回个activity
       6.语音输入
       7.历史记录  √
     */

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, mView);
        sdkInit();
        recycleViewInit();
        stateInit();
        listenInit();
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSuggestionSearch.destroy();
        mRoutePlanSearch.destroy();
        LogUtils.L(this.getClass(), "onDestroyView");
        ButterKnife.unbind(this);
    }

    @Override
    public boolean CanPopUp() {
        if (mHasTopHide) {
            topAnimatorShow();
            return false;
        }
        return true;
    }

    @Override
    public ToolbarAttribute GetToolBarAttribute() {
        ToolbarAttribute toolbarAttribute = new ToolbarAttribute();
        toolbarAttribute.mTitle = "出行";
        toolbarAttribute.mRippleViewAttribute = new RippleViewAttribute();
        RippleViewAttribute rippleViewAttribute = toolbarAttribute.mRippleViewAttribute;
        rippleViewAttribute.mShowClickBackground = false;
        rippleViewAttribute.mCircelBackground = true;
        rippleViewAttribute.mText = "搜索";
        rippleViewAttribute.mTextColor = Utils.GetContext().getResources().getColor(R.color.colorWhite54);
        return toolbarAttribute;
    }

    @Override
    public void OnActionButtonClick() {
        if (canSearchRoute()) {
            searchRoute();
        } else {
            Toast.makeText(getActivity(), "地点格式错误 请从建议结果中选取一个", Toast.LENGTH_LONG).show();
        }
    }

    private void searchRoute() {
        getLocAndCheckError getLocAndCheckError = new getLocAndCheckError().invoke();
        if (getLocAndCheckError.is()) return;
        LatLng latLng = getLocAndCheckError.getLatLng();
        if (mFrom.getText().toString().trim().equals("我的位置")) {
            mFromPlanNode = PlanNode.withLocation(latLng);
        }
        if (mTo.getText().toString().trim().equals("我的位置")) {
            mToPlanNode = PlanNode.withLocation(latLng);
        }

        DrivingRoutePlanOption option = new DrivingRoutePlanOption();
        option.from(mFromPlanNode);
        option.to(mToPlanNode);
        option.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
        if (mRoutePlanSearch.drivingSearch(option)) {
            setToolbarIsRefresh(TOOLBAR_REFLASH);
            Toast.makeText(getActivity(), "路线搜索中...", Toast.LENGTH_SHORT).show();
        }
    }

    public void sdkInit() {
        mSuggestionSearch = SuggestionSearch.newInstance();
        mRoutePlanSearch = RoutePlanSearch.newInstance();


        mSuggestionSearch.setOnGetSuggestionResultListener(new mOnGetSuggestionResultListener());
        mRoutePlanSearch.setOnGetRoutePlanResultListener(new mOnGetRoutePlanResultListener());
    }


    private void stateInit() {
        getMainActivity().BelowToolbar(mView);
        getMainActivity().SetStatusBarAlpha(1f);
        getSearchHistory();
    }

    private void recycleViewInit() {
        mHistoryList = new ArrayList<>();
        mResultList = new ArrayList<>();
        mSuggestList = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new historyAdapter());
    }


    private void listenInit() {
        new mTextWatch(mFrom);
        new mTextWatch(mTo);
        mFrom.setOnFocusChangeListener(new mOnFocusChangeListener());
        mTo.setOnFocusChangeListener(new mOnFocusChangeListener());

        mTo.setOnEditorActionListener(new mEditorActionListem());
        mFrom.setOnEditorActionListener(new mEditorActionListem());
        mActionButton.setOnClickListener(new mOnCLickListener());

    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        LogUtils.L("searchFramgent", "receive");
        mLocationn = bdLocation;
    }

    private class mEditorActionListem implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                suggestSearch(v.getText().toString());
                return true;
            }
            return false;
        }
    }


    //按钮点击
    private class mOnCLickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (mActionMode) {
                case ACTIONMODE_MIC:
                    //语音输入
                    break;
                case ACTIONMODE_OK:
                    LogUtils.L("ActionButton", "Mode OK  点击");

                    String s = Utils.Object2String(new teamgodeater.car_net.Data.SearchData());
                    LogUtils.L("ActionButton", "Mode OK  Object2String  " + s);
                    Object o = Utils.String2Object(s);
                    teamgodeater.car_net.Data.SearchData d = (teamgodeater.car_net.Data.SearchData) o;
                    LogUtils.L("ActionButton", "Mode OK  d  " + d.getmName());

                    break;
                case ACTIONMODE_SWAP:
                    String temp = mTo.getText().toString();
                    mTo.setText(mFrom.getText());
                    mFrom.setText(temp);

                    PlanNode tempp = mToPlanNode;
                    mToPlanNode = mFromPlanNode;
                    mFromPlanNode = tempp;
                    break;
            }
        }
    }


    private void topAnimatorHide(EditText view) {
        if (mHasTopHide || mToolbarState == TOOLBAR_REFLASH) {
            return;
        }
        mHasTopHide = true;
        mSearchText = "";
        getToolbar().GoneActionButoon();
        LogUtils.L("topAnimatorHide", " HasTopHide " + mHasTopHide);

        showHistory();
        view.setVisibility(View.GONE);
        mDivider.setAlpha(0f);

        if (getToolbar() != null) {
            getToolbar().SetTitleText("输入" + GetFocusEditText().getHint());
        }
        if (GetFocusEditText().getText().length() == 0) {
            mActionMode = ACTIONMODE_MIC;
            mActionButton.setText(null);
            mActionButton.SetSrc(getResources().getDrawable(R.drawable.ic_mic_white));
        } else {
            mActionMode = ACTIONMODE_OK;
            mActionButton.SetTextAndIgnoreSrc("确定", false);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator valueAnimator = AnimatorUtils.HeightAnimator(mPoint2Point, Utils.dip2px(45f), Utils.dip2px(8f));
        ValueAnimator valueAnimator1 = AnimatorUtils.HeightAnimator(mTopLayout, Utils.dip2px(90f), Utils.dip2px(45f));
        animatorSet.playTogether(valueAnimator, valueAnimator1);
        animatorSet.start();
    }

    private void topAnimatorShow() {
        if (!mHasTopHide) {
            return;
        }
        mHasTopHide = false;

        getToolbar().VisibleActionButtion();
        LogUtils.L("topAnimatorShow", " HasTopHide " + mHasTopHide);

        showHistory();
        mFrom.setVisibility(View.VISIBLE);
        mTo.setVisibility(View.VISIBLE);
        mTo.setAlpha(0f);

        if (getToolbar() != null) {
            getToolbar().SetTitleText("出行");
        }

        mActionMode = ACTIONMODE_SWAP;
        mActionButton.setText(null);
        mActionButton.SetSrc(getResources().getDrawable(R.drawable.ic_swap_vert_white));

        mDivider.setAlpha(1f);
        mTo.animate().alpha(1f).setStartDelay(200L).start();
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator valueAnimator = AnimatorUtils.HeightAnimator(mPoint2Point, Utils.dip2px(8f), Utils.dip2px(45f));
        ValueAnimator valueAnimator1 = AnimatorUtils.HeightAnimator(mTopLayout, Utils.dip2px(45f), Utils.dip2px(90f));
        animatorSet.playTogether(valueAnimator, valueAnimator1);
        animatorSet.start();
    }

    //edittext 焦点改变
    private class mOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            LogUtils.L("onFocusChange", "View " + (v.getId() == R.id.To ? "To" : "From") + " hasFocus" + hasFocus);
            if (hasFocus) {
                topAnimatorHide(v.getId() == R.id.To ? mFrom : mTo);
            } else {
                Utils.HideInputMethoe(v);
            }
        }
    }

    private EditText GetFocusEditText() {
        if (!mHasTopHide) {
            return mTo;
        }
        if (mTo.getVisibility() == View.VISIBLE) {
            return mTo;
        } else {
            LogUtils.L("getfocusEditext", " from");
            return mFrom;
        }
    }

    //建议搜索结果
    private class mOnGetSuggestionResultListener implements OnGetSuggestionResultListener {

        @Override
        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
            setToolbarIsRefresh(TOOLBAR_NOMAL);
            if (mHasTopHide) {
                if (suggestionResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    List<SuggestionResult.SuggestionInfo> allSuggestions = suggestionResult.getAllSuggestions();
                    if (allSuggestions != null) {
                        mSuggestList.clear();
                        for (SuggestionResult.SuggestionInfo sug : allSuggestions) {
                            SearchData scdata = new SearchData();
                            if (sug.city.isEmpty()) {
                                scdata.setmIsLocation(false);
                                scdata.setmName(sug.key);
                            } else {
                                scdata.setmIsLocation(true);
                                if (sug.pt != null)
                                    scdata.setmLatng(sug.pt.latitude, sug.pt.longitude);
                                scdata.setmName(sug.key);
                                scdata.setmAddrs(sug.city + "-" + sug.district);
                            }
                            mSuggestList.add(scdata);
                        }
                        mRecyclerView.getLayoutManager().removeAllViews();
                        mRecyclerView.setAdapter(new suggestAdapter());
                    } else {
                        Toast.makeText(getActivity(), "没有找到搜索结果", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            LogUtils.L("onGetSuggestionResult", "Resultcode " + suggestionResult.error);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocationHelp.Star(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationHelp.Stop();
    }

    private BDLocation mLocationn;


    //路线查询结果
    private class mOnGetRoutePlanResultListener implements OnGetRoutePlanResultListener {


        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            setToolbarIsRefresh(TOOLBAR_NOMAL);
            LogUtils.L("onGetDrivingRouteResult", "err  " + drivingRouteResult.error);
            List<DrivingRouteLine> routeLines = drivingRouteResult.getRouteLines();
            if (routeLines != null) {
                LogUtils.L("onGetDrivingRouteResult", "routelines  " + routeLines.size());
                for (DrivingRouteLine line : routeLines) {
                    LogUtils.L("onGetDrivingRouteResult", "describeContents  " + line.describeContents());
                    LogUtils.L("onGetDrivingRouteResult", "getDistance  " + line.getDistance());
                    LogUtils.L("onGetDrivingRouteResult", "getDuration  " + line.getDuration());
                    LogUtils.L("onGetDrivingRouteResult", "toString  " + line.toString());
                    LogUtils.L("onGetDrivingRouteResult", "getTitle  " + line.getTitle());

                }
            } else {
                Toast.makeText(getActivity(), "没有找到路线", Toast.LENGTH_SHORT).show();
            }

        }
        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    }


    private final int ACTIONMODE_SWAP = 1, ACTIONMODE_OK = 2, ACTIONMODE_MIC = 3;
    private int mActionMode = 1;

    //搜索框内容改变监听
    private class mTextWatch implements TextWatcher {
        private TextView mTx;
        private final int mDelayedTime = 1000;
        private long mCount = 0;
        private Runnable mRunnbale;
        ReentrantReadWriteLock mLock;

        public mTextWatch(TextView tx) {
            mTx = tx;
            mTx.addTextChangedListener(this);
            mLock = new ReentrantReadWriteLock();
            mRunnbale = new Runnable() {
                @Override
                public void run() {
                    mCount--;
                    if (mCount == 0) {
                        suggestSearch(mTx.getText().toString());
                    }
                }
            };
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mHasTopHide) {
                if (s.length() == 0 && mActionMode != ACTIONMODE_MIC) {
                    mActionMode = ACTIONMODE_MIC;
                    mActionButton.setText(null);
                    mActionButton.SetSrc(getResources().getDrawable(R.drawable.ic_mic_white));
                } else if (mActionMode != ACTIONMODE_OK) {
                    mActionMode = ACTIONMODE_OK;
                    mActionButton.SetTextAndIgnoreSrc("确定", false);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mHasTopHide) {
                if (GetFocusEditText().getId() == R.id.From) {
                    mFromPlanNode = null;
                } else {
                    mToPlanNode = null;
                }
                Handler h = new Handler();
                mCount++;
                h.postDelayed(mRunnbale, mDelayedTime);
            }
        }
    }

    private String mSearchText;

    private void suggestSearch(String text) {
        text = text.trim();
        LogUtils.L("suggest", "search " + text);
        //搜索内容为空 ,我的位置 上次搜索的内容 Top没有收缩  不搜索
        if (!text.isEmpty() && !text.equals("我的位置")) {
            if (text.equals(mSearchText)) {
                LogUtils.L("suggest", "search 搜索内容和之前相同 : " + text);
                return;
            }
            if (!mHasTopHide) {
                return;
            }
            LogUtils.L("suggest", "search 内容 : " + text);
            //获取当前地点
            LogUtils.L("suggest", "search 获取当前地点");
            getLocAndCheckError getLocAndCheckError = new getLocAndCheckError().invoke();
            //检查定位错误
            LogUtils.L("suggest", "search 检查错误");
            if (getLocAndCheckError.is()) return;
            //定位正确
            LogUtils.L("suggest", "search 定位正确");
            LatLng latLng = getLocAndCheckError.getLatLng();
            String city = getLocAndCheckError.getCity();
            //进行建议搜索
            LogUtils.L("suggest", "search 准备建议搜索");
            if (mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().location(latLng).city(city).keyword(text))) {
                setToolbarIsRefresh(TOOLBAR_REFLASH);
                mSearchText = text;
                Toast.makeText(getActivity(), "搜索 " + text, Toast.LENGTH_SHORT).show();
                LogUtils.L("suggest", "search 正确 进行建议搜索" + text);
            } else {
                LogUtils.L("suggest", "search 错误 取消建议搜索");
            }
        } else {
            LogUtils.L("suggest", "search 显示历史记录");
            showHistory();
        }
    }

    //关于历史记录的操作
    private void showHistory() {
        //关于历史记录的操作
        LogUtils.L("History", "准备显示历史记录");
        if (!(mRecyclerView.getAdapter() instanceof historyAdapter)) {
            LogUtils.L("History", "显示历史记录");
            mRecyclerView.getLayoutManager().removeAllViews();
            mRecyclerView.setAdapter(new historyAdapter());
        } else {
            LogUtils.L("History", "原来已经显示的是历史记录 不进行更改");
        }
    }

    private void getSearchHistory() {
        SharedPreferences search_history = getActivity().getSharedPreferences("Search_History", Context.MODE_PRIVATE);
        Map<String, ?> all = search_history.getAll();
        int size = all.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String value = (String) all.get(String.valueOf(i));
                LogUtils.L("getHistory", "index " + i + "  neirong " + value);
                SearchData s = (SearchData) Utils.String2Object(value);
                mHistoryList.add(s);
            }
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void saveSearchHistory(SearchData savadata) {
        for (SearchData search : mHistoryList) {
            if (search.getmLatitude() == savadata.getmLatitude() && savadata.getmLongitude() == savadata.getmLongitude()) {
                LogUtils.L("saveSearchHistory", "数据已经存在 返回!");
                return;
            }
        }

        SharedPreferences search_history = getActivity().getSharedPreferences("Search_History", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = search_history.edit();
        edit.clear();

        mHistoryList.add(0, savadata);

        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof historyAdapter) {
            adapter.notifyDataSetChanged();
        }

        int size = mHistoryList.size();
        for (int i = 0; i < size; i++) {
            edit.putString(String.valueOf(i), Utils.Object2String(mHistoryList.get(i)));
        }
        edit.apply();
    }


    private void cleanSearchHistory() {
        SharedPreferences search_history = getActivity().getSharedPreferences("Search_History", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = search_history.edit();
        edit.clear();
        edit.apply();
        mHistoryList.clear();

        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof historyAdapter) {
            adapter.notifyDataSetChanged();
        }
    }


    private class historyAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LogUtils.L("historyAdapter", "type  " + viewType);
            if (viewType == NOMAL) {
                View mV = LayoutInflater.from(getActivity()).inflate(R.layout.rv_ico_2line_tip, parent, false);
                LogUtils.L("historyAdapter", "onCreateViewHolder");
                return new HistoryHolder(mV);
            } else if (viewType == CLEANALL) {
                View mV = LayoutInflater.from(getActivity()).inflate(R.layout.rv_single_line_center, parent, false);
                LogUtils.L("historyAdapter", "onCreateViewHolderCleanAll");
                return new HistoryCleanAllHolder(mV);
            } else {
                View mV = LayoutInflater.from(getActivity()).inflate(R.layout.rv_search_slt_loc_on_map, parent, false);
                LogUtils.L("historyAdapter", "onCreateViewHolderSlcOnMap");
                return new SlcLocOnMapHolder(mV);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LogUtils.L("historyAdapter", "onBindViewHolder" + position);
            if (holder instanceof HistoryCleanAllHolder) {
                ((HistoryCleanAllHolder) holder).SetClickLisent();
                return;
            }
            if (holder instanceof SlcLocOnMapHolder) {
                ((SlcLocOnMapHolder) holder).SetClickLisent();
                return;
            }
            if (!mHistoryList.isEmpty()) {
                ((HistoryHolder) holder).BindViewData(mHistoryList.get(position - 1));
            }
        }

        @Override
        public int getItemCount() {
            int size = mHistoryList.size();
            if (size == 0) {
                return 1;
            }
            return size + 2;
        }

        private final int SLCMAP = 0, CLEANALL = 1, NOMAL = 2;

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? SLCMAP : (position == getItemCount() - 1 ? CLEANALL : NOMAL);
        }

    }


    public class HistoryHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.Ico)
        ImageView mIco;
        @Bind(R.id.TitleMain)
        TextView mTitleMain;
        @Bind(R.id.TitleTip)
        TextView mTitleTip;
        @Bind(R.id.Tip)
        TextView mTip;
        @Bind(R.id.TipIco)
        ImageView mTipIco;
        @Bind(R.id.Click)
        RippleView mClick;

        private SearchData mSearchData;

        public HistoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void OnClickListener() {
            mClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //当前edit内容改变
                    EditText editText = GetFocusEditText();
                    mSearchText = mSearchData.getmName();
                    editText.setText(mSearchText);

                    LatLng lat = new LatLng(mSearchData.getmLatitude(), mSearchData.getmLongitude());
                    //添加PlanNode
                    if (editText.getId() == R.id.From) {
                        mFromPlanNode = PlanNode.withLocation(lat);
                    } else {
                        mToPlanNode = PlanNode.withLocation(lat);
                    }
                    topAnimatorShow();
                    //是否能直接进行搜索
                    if (canSearchRoute()) {
                        searchRoute();
                    }
                }
            });
        }

        public void BindViewData(SearchData s) {
            mSearchData = s;
            mTip.setVisibility(View.GONE);
            mTipIco.setVisibility(View.GONE);
            mTitleMain.setTextColor(getResources().getColor(R.color.colorBlack87));
            mTitleMain.setText(s.getmName());
            mTitleTip.setText(s.getmAddrs());
            mIco.setImageResource(R.drawable.ic_history_peimary);
            OnClickListener();
        }
    }

    public class HistoryCleanAllHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.Click)
        RippleView mClick;

        public HistoryCleanAllHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mClick.setText("清空历史记录");
        }

        public void SetClickLisent() {
            mClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cleanSearchHistory();
                }
            });
        }
    }


    public class SlcLocOnMapHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.Click)
        RippleView mClick;

        public SlcLocOnMapHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void SetClickLisent() {
            mClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //地图选点
                    LogUtils.L("slcloconmap", "点击 在地图上选点");
                }
            });
        }
    }


    public class suggestHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.Ico)
        ImageView mIco;
        @Bind(R.id.TitleMain)
        TextView mTitleMain;
        @Bind(R.id.TitleTip)
        TextView mTitleTip;
        @Bind(R.id.Tip)
        TextView mTip;
        @Bind(R.id.TipIco)
        ImageView mTipIco;
        @Bind(R.id.Click)
        RippleView mClick;

        private SearchData mSearchData;

        public suggestHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void OnClickListener() {
            mClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSearchData.ismIsLocation()) {
                        //添加搜索历史
                        saveSearchHistory(mSearchData);
                        //当前edit内容改变
                        EditText editText = GetFocusEditText();
                        mSearchText = mSearchData.getmName();
                        editText.setText(mSearchText);

                        LatLng lat = new LatLng(mSearchData.getmLatitude(), mSearchData.getmLongitude());
                        //添加PlanNode
                        if (editText.getId() == R.id.From) {
                            mFromPlanNode = PlanNode.withLocation(lat);
                        } else {
                            mToPlanNode = PlanNode.withLocation(lat);
                        }
                        topAnimatorShow();
                        //是否能直接进行搜索
                        if (canSearchRoute()) {
                            searchRoute();
                        }
                        LogUtils.L("suggest", "seachHoder 点击 地点");
                        return;
                    }
                    //二次检索
                    suggestSearch(mSearchData.getmName());
                    GetFocusEditText().setText(mSearchData.getmName());
                    LogUtils.L("suggest", "seachHoder 二次搜索");

                }
            });
        }

        public void BindViewData(SearchData s) {
            mSearchData = s;
            mTip.setVisibility(View.GONE);
            mTipIco.setVisibility(View.GONE);

            mTitleMain.setTextColor(getResources().getColor(R.color.colorBlack54));
            mTitleMain.setText(Utils.SpannableString(mSearchData.getmName(), mSearchText));
            if (!mSearchData.ismIsLocation()) {
                mTitleTip.setVisibility(View.GONE);
                mIco.setImageResource(R.drawable.ic_search_primery);
            } else {
                mTitleTip.setVisibility(View.VISIBLE);
                mTitleTip.setText(mSearchData.getmAddrs());
                mIco.setImageResource(R.drawable.ic_location_on_primery);
            }
            OnClickListener();
        }
    }

    private boolean canSearchRoute() {
        if ((mFromPlanNode != null || mFrom.getText().toString().trim().equals("我的位置"))
                && (mToPlanNode != null || mTo.getText().toString().trim().equals("我的位置"))
                && !mFrom.getText().toString().trim().equals(mTo.getText().toString().trim())) {
            LogUtils.L("suggest", "seachHoder  可以直接搜搜");
            getToolbar().GetActionButton().setTextColor(getResources().getColor(R.color.colorWhite87));
            return true;
        }
        getToolbar().GetActionButton().setTextColor(getResources().getColor(R.color.colorWhite54));
        return false;
    }


    private class suggestAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == NOMAL) {
                View mV = LayoutInflater.from(getActivity()).inflate(R.layout.rv_ico_2line_tip, parent, false);
                return new suggestHolder(mV);
            }
            View mV = LayoutInflater.from(getActivity()).inflate(R.layout.rv_search_slt_loc_on_map, parent, false);
            return new SlcLocOnMapHolder(mV);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0) {
                ((SlcLocOnMapHolder) holder).SetClickLisent();
                return;
            }
            if (!mSuggestList.isEmpty()) {
                ((suggestHolder) holder).BindViewData(mSuggestList.get(position - 1));
            }
        }

        @Override
        public int getItemCount() {
            return mSuggestList.size() + 1;
        }


        private final int SLCMAP = 0, NOMAL = 1;

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? SLCMAP : NOMAL;
        }
    }

    public class RouteHolder extends RecyclerView.ViewHolder {
        public RouteHolder(View itemView) {
            super(itemView);
        }
    }

    private class routeAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LogUtils.L("routeAdapter", "onCreateViewHolder");
            View mV = LayoutInflater.from(getActivity()).inflate(R.layout.rv_search_route, parent, false);
            return new RouteHolder(mV);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LogUtils.L("routeAdapter", "onBindViewHolder");

        }

        @Override
        public int getItemCount() {
            return mResultList.size();
        }

    }

    private class routeData {

    }


    private class getLocAndCheckError {
        private boolean myResult;
        private LatLng latLng;
        private String city;

        boolean is() {
            return myResult;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public String getCity() {
            return city;
        }

        public getLocAndCheckError invoke() {
            if (!Utils.isNetworkConnected()) {
                //无法连接网络请检查网络设置
                Toast.makeText(getActivity(), "无法连接到服务器,请检查网络设置", Toast.LENGTH_SHORT).show();
                myResult = true;
                return this;
            }
            if (mLocationn != null) {
                latLng = new LatLng(mLocationn.getLatitude(), mLocationn.getLongitude());
                city = mLocationn.getCity();
                if (city != null && !city.trim().isEmpty() && latLng != null) {
                    myResult = false;
                    return this;
                }
            }
            //定位失败
            Toast.makeText(getActivity(), "定位失败,请检查网络和Gps设置", Toast.LENGTH_SHORT).show();
            myResult = true;
            return this;

        }
    }
}
