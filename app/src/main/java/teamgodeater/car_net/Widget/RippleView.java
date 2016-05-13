package teamgodeater.car_net.Widget;

/**
 * Created by G on 2016/4/22 0022.
 */

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import teamgodeater.car_net.Attribute.RippleViewAttribute;
import teamgodeater.car_net.R;
import teamgodeater.car_net.Util.LogUtils;


public class RippleView extends TextView {

    private float mDownX;
    private float mDownY;
    private float mRadius;
    private float mMaxRadius;
    private float mMinRadius;
    private ObjectAnimator mRadiusAnimator;
    private boolean mIsCancel;
    private Paint mPaint;
    private Path mPath;
    private RippleViewAttribute mAttribute;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        RippleViewAttribute rippleViewAttribute = new RippleViewAttribute();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        rippleViewAttribute.mRippleColor = a.getColor(R.styleable.RippleView_rippleColor, Color.argb(13, 0, 0, 0));
        rippleViewAttribute.mShowClickBackground = a.getBoolean(R.styleable.RippleView_showClickBackground, true);
        rippleViewAttribute.mCircelBackground = a.getBoolean(R.styleable.RippleView_circelBackground, false);
        rippleViewAttribute.mAlphaSrc = a.getFloat(R.styleable.RippleView_alphaSrc, 0.87f);
        rippleViewAttribute.mSrc = a.getDrawable(R.styleable.RippleView_src);
        a.recycle();
        SetAttribute(rippleViewAttribute);
    }

    public void SetTextAndIgnoreSrc(String s, boolean hasSrc) {
        if (!hasSrc) {
            mAttribute.mSrc = null;
        }
        setText(s);
    }


    public RippleViewAttribute GetAttribute() {
        return mAttribute;
    }

    public void SetAttribute(RippleViewAttribute rippleViewAttribute) {
        mAttribute = rippleViewAttribute;

        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mAttribute.mRippleColor);

        if (rippleViewAttribute.mText != null) {
            setText(rippleViewAttribute.mText);
            setTextColor(rippleViewAttribute.mTextColor);
        }
        setClickable(true);
        mIsCancel = true;
    }

    public void SetSrc(Drawable dr) {
        mAttribute.mSrc = dr;
        if (mAttribute.mSrc != null) {
            mAttribute.mSrc.mutate().setAlpha((int) (mAttribute.mAlphaSrc * 255));
            mAttribute.mSrc.setBounds(getPaddingLeft(), getPaddingTop(), getHeight() - getPaddingRight(), getWidth() - getPaddingBottom());
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtils.L(this.getClass(), "onsizechange");
        mPath.addCircle(w / 2, h / 2, Math.min(h, w) / 2, Path.Direction.CW);
        if (mAttribute.mSrc != null) {
            mAttribute.mSrc.mutate().setAlpha((int) (mAttribute.mAlphaSrc * 255));
            mAttribute.mSrc.setBounds(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
        }
        mMaxRadius = (float) Math.sqrt(w * w + h * h);
        mMinRadius = mMaxRadius * 0.3f;
    }

    private Rect mRect;
    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        if (!isClickable()) {
            return false;
        }

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN
                && this.isEnabled()) {
            mRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
            mIsCancel = false;
            mDownX = event.getX();
            mDownY = event.getY();
            mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", 0, mMinRadius)
                    .setDuration(300);
            mRadiusAnimator
                    .setInterpolator(new AccelerateDecelerateInterpolator());
            mRadiusAnimator.start();

            return true;
        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE && this.isEnabled()) {
            mDownX = event.getX();
            mDownY = event.getY();

            // Cancel the ripple animation when moved outside
            if (mIsCancel = !mRect.contains(
                    getLeft() + (int) event.getX(),
                    getTop() + (int) event.getY())) {
                setRadius(0);
            } else {
                setRadius(mMinRadius);
            }
            return true;
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP
                && !mIsCancel && this.isEnabled()) {
            mRadiusAnimator.cancel();

            mDownX = event.getX();
            mDownY = event.getY();

            if (hasOnClickListeners()) {
                callOnClick();
            }

            mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", mMinRadius, mMaxRadius);
            mRadiusAnimator.setDuration(300);
            mRadiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mRadiusAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mIsCancel = true;
                    setRadius(0f);
                    setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    mIsCancel = true;
                    setRadius(0f);
                    setClickable(true);
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            mRadiusAnimator.start();
            return true;
        } else if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            mIsCancel = true;
            setRadius(0);
            return true;
        }
        return false;
    }

    public void setRadius(final float radius) {
        mRadius = radius;
        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        super.onDraw(canvas);

        if (mAttribute.mSrc != null) {
            mAttribute.mSrc.draw(canvas);
        }
        if (!isInEditMode() && !mIsCancel) {
            if (mAttribute.mCircelBackground) {
                canvas.clipPath(mPath);
            }
            if (mAttribute.mShowClickBackground) {
                canvas.drawColor(mAttribute.mRippleColor);
            }
            canvas.drawCircle(mDownX, mDownY, mRadius, mPaint);
        }
    }

}