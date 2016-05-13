package teamgodeater.car_net.Util;

import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by G on 2016/5/3 0003.
 */
public  class AnimatorUtils {

    public static ValueAnimator HeightAnimator(final View v, int Star, int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(Star, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        return valueAnimator;
    }


}
