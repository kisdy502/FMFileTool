package cn.fengmang.libui.pager;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import cn.fengmang.baselib.ELog;
import cn.fengmang.libui.R;


/**
 * Created by Administrator on 2018/8/7.
 */

public class AutoTurnViewPager<T> extends LooperViewPager {
    private boolean isRunning; //是否正在执行翻页中   如果是canLoop 到头了 那就不翻页
    private boolean canTurn;   //能否能执行自动翻页
    private int autoTurnTime = 3000;//间隔
    private boolean reverse;

    ViewPagerScroller scroller;

    public AutoTurnViewPager.TurnRunnable turnRunnable;

    public AutoTurnViewPager(Context context) {
        this(context, null);
    }

    public AutoTurnViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        turnRunnable = new AutoTurnViewPager.TurnRunnable(this);
        setRunning(true);
        setCanTurn(true);
        initViewPagerScroll();
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AutoTurnViewPager);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.AutoTurnViewPager_canLoop) {
                    setCanLoop(a.getBoolean(attr, true));
                } else if (attr == R.styleable.AutoTurnViewPager_canTurn) {
                    setCanTurn(a.getBoolean(attr, true));
                } else if (attr == R.styleable.AutoTurnViewPager_isTouchScroll) {
                    setTouchScroll(a.getBoolean(attr, true));
                } else if (attr == R.styleable.AutoTurnViewPager_autoTurnTime) {
                    setAutoTurnTime(a.getInteger(attr, getAutoTurnTime()));
                } else if (attr == R.styleable.AutoTurnViewPager_scrollDuration) {
                    setScrollDuration(a.getInteger(attr, getScrollDuration()));
                } else if (attr == R.styleable.AutoTurnViewPager_reverse) {
                    setReverse(a.getBoolean(attr, false));
                }
            }
            a.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTurn();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopTurning();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            startTurn();
        } else if (action == MotionEvent.ACTION_DOWN) {
            stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }


    boolean isRunning() {
        return isRunning;
    }

    AutoTurnViewPager setRunning(boolean running) {
        isRunning = running;
        return this;
    }


    AutoTurnViewPager setCanTurn(boolean canTurn) {
        if (this.canTurn == canTurn)
            return this;
        this.canTurn = canTurn;
        if (canTurn) {
            startTurn();
        } else {
            stopTurning();
        }
        return this;
    }

    @Override
    public void setCanLoop(boolean canLoop) {
        if (!isRunning()) {
            startTurn();
        }
        super.setCanLoop(canLoop);
    }

    boolean isCanTurn() {
        return canTurn;
    }


    int getAutoTurnTime() {
        return autoTurnTime;
    }

    AutoTurnViewPager setAutoTurnTime(int autoTurnTime) {
        this.autoTurnTime = autoTurnTime;
        return this;
    }

    AutoTurnViewPager setScrollDuration(int duration) {
        scroller.setScrollDuration(duration);
        return this;
    }

    int getScrollDuration() {
        return scroller.getScrollDuration();
    }

    void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    boolean isReverse() {
        return reverse;
    }

    AutoTurnViewPager startTurn() {
        startTurn(autoTurnTime);
        return this;
    }

    private AutoTurnViewPager startTurn(int autoTurnTime) {
        stopTurning();
        setRunning(true);
        setAutoTurnTime(autoTurnTime);
        postDelayed(turnRunnable, this.autoTurnTime);
        return this;
    }

    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(
                    getContext());
            mScroller.set(this, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopTurning() {
        //关闭翻页
        setRunning(false);
        removeCallbacks(turnRunnable);
    }




    static class TurnRunnable implements Runnable {

        private final WeakReference<AutoTurnViewPager> reference;

        public TurnRunnable(AutoTurnViewPager autoTurnViewPager) {
            this.reference = new WeakReference<AutoTurnViewPager>(autoTurnViewPager);
        }

        @Override
        public void run() {
            AutoTurnViewPager autoTurnViewPager = reference.get();
            if (autoTurnViewPager != null) {
                if (autoTurnViewPager.isRunning() && autoTurnViewPager.isCanTurn()) {
                    int page = autoTurnViewPager.getCurrentItem() + (autoTurnViewPager.isReverse() ? -1 : 1);
                    ELog.d("page:" + page);
                    if (!autoTurnViewPager.getAdapter().isRealCanLoop() && (page >= autoTurnViewPager.getAdapter().getRealCount() || page < 0)) {
                        autoTurnViewPager.setRunning(false);
                        return;
                    }
                    autoTurnViewPager.setCurrentItem(page);
                    autoTurnViewPager.startTurn();
                }
            }
        }
    }
}
