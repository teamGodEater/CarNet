package teamgodeater.car_net.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.car_net.Data.Ico2LineTipData;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Widget.RippleView;

/**
 * Created by G on 2016/5/13 0013.
 */
public class Ico2LineTipHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.Ico)
    ImageView mIco;
    @Bind(R.id.OvalPoint)
    ImageView mOvalPoint;
    @Bind(R.id.TitleMain)
    TextView mTitleMain;
    @Bind(R.id.TitleTip)
    TextView mTitleTip;
    @Bind(R.id.Tip)
    TextView mTip;
    @Bind(R.id.TipIco)
    ImageView mTipIco;
    @Bind(R.id.Divider)
    View mDivider;
    @Bind(R.id.Click)
    RippleView mClick;

    Ico2LineTipData mData;


    public Ico2LineTipHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void BindView(Ico2LineTipData data, float titlemainalpha, float titletipalpha, float tipalpha) {
        mData = data;

        mTitleMain.setText(mData.getmTitleMain());
        mTitleMain.setAlpha(titlemainalpha);
        mTitleTip.setText(mData.getmTitleTip());
        mTitleTip.setAlpha(titletipalpha);
        mTip.setText(mData.getmTip());
        mTip.setAlpha(tipalpha);

        mIco.setImageDrawable(mData.getmIco());
        mTipIco.setImageDrawable(mData.getmTipIco());
        mOvalPoint.setImageDrawable(mData.getmOvalPoint());

        if (mData.ismHasDivider()) {
            mDivider.setVisibility(View.VISIBLE);
        } else {
            mDivider.setVisibility(View.INVISIBLE);
        }
    }

    public void SetClickListen(View.OnClickListener listener) {
        mClick.setOnClickListener(listener);
    }

}
