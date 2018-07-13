package cn.fengmang.libui.scroller;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class FlowScroller extends Scroller {
    private final static int SPEED = 12 * 5;
    private static final int MAX_DURATION = 120;

    public FlowScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    public FlowScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public FlowScroller(Context context) {
        super(context);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        int totalTime = SPEED * Math.max(Math.abs(dx), Math.abs(dy));
        if (totalTime > MAX_DURATION) {
            totalTime = MAX_DURATION;
        }
        startScroll(startX, startY, dx, dy, totalTime);
    }

}
