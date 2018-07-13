package cn.fengmang.libui.flying;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.fengmang.baselib.ELog;
import cn.fengmang.libui.R;
import cn.fengmang.libui.scroller.FlowHorScrollView;


/**
 * Created by Administrator on 2018/7/10.
 */

public abstract class BaseFlyingFrameView extends View implements IFlying {

    private final static String TAG = "BaseFlowView";

    private static final int DEFAULT_ANIM_DURATION_TIME = 200;
    protected int mAnimDuration = DEFAULT_ANIM_DURATION_TIME;

    protected RectF mPaddingOfsetRectF = new RectF();
    protected RectF mPaddingRectF = new RectF();
    protected RectF mFrameRectF = new RectF();
    protected RectF mTempRectF = new RectF();

    private WeakReference<View> mOldViewReference;
    private AnimatorSet mAnimatorSet;

    private ObjectAnimator mTranslationXAnimator;
    private ObjectAnimator mTranslationYAnimator;
    private ObjectAnimator mWidthAnimator;
    private ObjectAnimator mHeightAnimator;
    private RecyclerViewScrollListener mRecyclerViewScrollListener;
    private float mScaleX, mScaleY;
    private WeakReference<RecyclerView> mWeakRecyclerViewRef;


    public BaseFlyingFrameView(Context context) {
        this(context, null);
    }

    public BaseFlyingFrameView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseFlyingFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseFlyingFrameView);
        mAnimDuration = a.getInt(R.styleable.BaseFlyingFrameView_flyingDuration, DEFAULT_ANIM_DURATION_TIME);
        final int padding = a.getDimensionPixelOffset(R.styleable.BaseFlyingFrameView_flyingSpace, 0);
        mPaddingOfsetRectF.left = padding;
        mPaddingOfsetRectF.top = padding;
        mPaddingOfsetRectF.right = padding;
        mPaddingOfsetRectF.bottom = padding;
        a.recycle();
        init();
    }

    /**************************私有方法*******************************/
    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null); //关闭硬件加速
        setVisibility(INVISIBLE);
    }

    /**
     * view缩放
     *
     * @param tagetView
     * @param scaleX
     * @param scaleY
     */
    private void scale(@Nullable View tagetView, final float scaleX, final float scaleY) {
        if (null == tagetView) {
            ELog.d(TAG, "tagetView is null");
            return;
        }
        tagetView.animate().scaleX(scaleX).scaleY(scaleY).setDuration(mAnimDuration).start();
    }

    private void buildMoveAnimation(View tagetView, final float scaleX, final float scaleY) {
        final float paddingWidth = mPaddingRectF.left + mPaddingRectF.right + mPaddingOfsetRectF.left + mPaddingOfsetRectF.right;
        final float paddingHeight = mPaddingRectF.top + mPaddingRectF.bottom + mPaddingOfsetRectF.top + mPaddingOfsetRectF.bottom;
        ELog.v(TAG, String.format("paddingWidth:%f,paddingHeight:%f", paddingWidth, paddingHeight));
        final int ofsetWidth = (int) (tagetView.getMeasuredWidth() * (scaleX - 1f) + paddingWidth);
        final int ofsetHeight = (int) (tagetView.getMeasuredHeight() * (scaleY - 1f) + paddingHeight);
        final Rect fromRect = findLocationWithView2(this);
        final Rect toRect = findLocationWithView2(tagetView);
        View child = tagetView;
        while (child != null && child.getParent() != null && child instanceof View && child.getParent() instanceof View) {
            ViewParent parent = child.getParent();
            if (parent instanceof RecyclerView) {
                final RecyclerView rv = (RecyclerView) tagetView.getParent();
                registerScrollListener(rv);
                Object tag = rv.getTag();
                if (null != tag && tag instanceof Point) {
                    Point point = (Point) tag;
                    toRect.offset(rv.getLayoutManager().canScrollHorizontally() ? -point.x : 0,
                            rv.getLayoutManager().canScrollVertically() ? -point.y : 0);
                }
                toRect.offset(rv.getLeft(), rv.getTop());
            } else {
                View parentView = (View) parent;
                int dx = parentView.getLeft() + parentView.getScrollX();
                int dy = parentView.getTop() + parentView.getScrollY();
                toRect.offset(dx, dy);
            }
            child = (View) child.getParent();
        }
        toRect.right = toRect.left + tagetView.getMeasuredWidth();
        toRect.bottom = toRect.top + tagetView.getMeasuredHeight();
        toRect.inset(-ofsetWidth / 2, -ofsetHeight / 2);


        final int newWidth = toRect.width();
        final int newHeight = toRect.height();
        final int newX = toRect.left - fromRect.left;
        final int newY = toRect.top - fromRect.top;

        ELog.v(TAG, String.format("newWidth:%d,newHeight:%d", newWidth, newHeight));
        ELog.v(TAG, String.format("newX:%d,newY:%d", newX, newY));

        final List<Animator> together = new ArrayList<>();
        final List<Animator> appendTogether = getTogetherAnimators(newX, newY, newWidth, newHeight, scaleX, scaleY);
        if (null != appendTogether && !appendTogether.isEmpty()) {
            together.addAll(appendTogether);
        }

        together.add(getTranslationXAnimator(newX));
        together.add(getTranslationYAnimator(newY));
        together.add(getWidthAnimator(newWidth));
        together.add(getHeightAnimator(newHeight));

        final List<Animator> sequentially = new ArrayList<>();
        final List<Animator> appendSequentially = getSequentiallyAnimators(newX, newY, newWidth, newHeight, scaleX, scaleY);
        if (null != appendSequentially && !appendSequentially.isEmpty()) {
            sequentially.addAll(appendSequentially);
        }

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator(1));
        mAnimatorSet.playTogether(together);
        mAnimatorSet.playSequentially(sequentially);
    }


    private ObjectAnimator getTranslationXAnimator(float x) {
        if (null == mTranslationXAnimator) {
            mTranslationXAnimator = ObjectAnimator.ofFloat(this, "translationX", x)
                    .setDuration(mAnimDuration);
        } else {
            mTranslationXAnimator.setFloatValues(x);
        }
        return mTranslationXAnimator;
    }

    private ObjectAnimator getTranslationYAnimator(float y) {
        if (null == mTranslationYAnimator) {
            mTranslationYAnimator = ObjectAnimator.ofFloat(this, "translationY", y)
                    .setDuration(mAnimDuration);
        } else {
            mTranslationYAnimator.setFloatValues(y);
        }
        return mTranslationYAnimator;
    }

    private ObjectAnimator getHeightAnimator(int height) {
        if (null == mHeightAnimator) {
            mHeightAnimator = ObjectAnimator.ofInt(this, "height", getMeasuredHeight(), height)
                    .setDuration(mAnimDuration);
        } else {
            mHeightAnimator.setIntValues(getMeasuredHeight(), height);
        }
        return mHeightAnimator;
    }

    private ObjectAnimator getWidthAnimator(int width) {
        if (null == mWidthAnimator) {
            mWidthAnimator = ObjectAnimator.ofInt(this, "width", getMeasuredWidth(), width)
                    .setDuration(mAnimDuration);
        } else {
            mWidthAnimator.setIntValues(getMeasuredWidth(), width);
        }
        return mWidthAnimator;
    }

    private void registerScrollListener(RecyclerView recyclerView) {
        if (null != mWeakRecyclerViewRef && mWeakRecyclerViewRef.get() == recyclerView) {
            return;
        }
        if (null == mRecyclerViewScrollListener) {
            mRecyclerViewScrollListener = new RecyclerViewScrollListener(this);
        }
        if (null != mWeakRecyclerViewRef && null != mWeakRecyclerViewRef.get()) {
            mWeakRecyclerViewRef.get().removeOnScrollListener(mRecyclerViewScrollListener);
            mWeakRecyclerViewRef.clear();
        }
        recyclerView.removeOnScrollListener(mRecyclerViewScrollListener);
        recyclerView.addOnScrollListener(mRecyclerViewScrollListener);
        mWeakRecyclerViewRef = new WeakReference<>(recyclerView);
    }

    abstract List<Animator> getTogetherAnimators(int newX, int newY, int newWidth, int newHeight, float scaleX, float scaleY);


    abstract List<Animator> getSequentiallyAnimators(int newX, int newY, int newWidth, int newHeight, float scaleX, float scaleY);


    private void runFocusViewAnimation(@NonNull View tagetView, final float scaleX, final float scaleY) {
        setVisibility(View.VISIBLE);
        scale(tagetView, scaleX, scaleY);
        runBorderAnimation(tagetView, scaleX, scaleY);
    }

    protected void runBorderAnimation(@NonNull View tagetView, final float scaleX, final float scaleY) {
        if (null != mAnimatorSet) {
            mAnimatorSet.cancel();
        }
        buildMoveAnimation(tagetView, scaleX, scaleY);
        mAnimatorSet.start();
    }


    protected Rect findLocationWithView(@NonNull View descendant) {
        final ViewGroup root = (ViewGroup) getParent();
        final Rect rect = new Rect();
        if (descendant == root) {
            return rect;
        }

        final View srcDescendant = descendant;

        ViewParent theParent = descendant.getParent();
        Object tag;
        Point point;
        rect.offset(descendant.getLeft() - descendant.getScrollX(),
                descendant.getTop() - descendant.getScrollY());
        // search and offset up to the parent
        while ((theParent != null)
                && (theParent instanceof View)
                && (theParent != root)) {

            //兼容TvRecyclerView
            if (theParent instanceof RecyclerView) {
                final RecyclerView rv = (RecyclerView) theParent;
                registerScrollListener(rv);
                tag = rv.getTag();
                if (null != tag && tag instanceof Point) {
                    point = (Point) tag;
                    rect.offset(-point.x, -point.y);
                    ELog.v("point.x=" + point.x + " point.y=" + point.y);
                }
                rect.offset(rv.getLeft(), rv.getTop());
            }

            descendant = (View) theParent;
            theParent = descendant.getParent();
        }

        // now that we are up to this view, need to offset one more time
        // to get into our coordinate space
        if (theParent == root) {
            rect.offset(descendant.getLeft() - descendant.getScrollX(),
                    descendant.getTop() - descendant.getScrollY());
        }

        rect.right = rect.left + srcDescendant.getMeasuredWidth();
        rect.bottom = rect.top + srcDescendant.getMeasuredHeight();

        return rect;
    }

    protected Rect findLocationWithView2(@NonNull View view) {
        ViewGroup root = (ViewGroup) view.getParent();
        Rect rect = new Rect();
        root.offsetDescendantRectToMyCoords(view, rect);
        return rect;
    }

    /**
     * 属性动画用到的方法
     **/
    protected void setWidth(int width) {
        if (getLayoutParams().width != width) {
            getLayoutParams().width = width;
            requestLayout();
        }
    }

    protected void setHeight(int height) {
        if (getLayoutParams().height != height) {
            getLayoutParams().height = height;
            requestLayout();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            ELog.v(TAG, String.format("w:%d,h:%d;oldw:%d,oldh:%d", w, h, oldw, oldh));
            mFrameRectF.set(mPaddingRectF.left, mPaddingRectF.top, w - mPaddingRectF.right, h - mPaddingRectF.bottom);
        }
    }

    @Override
    public void onMoveTo(@NonNull View focusView, float scaleX, float scaleY, float raduis) {
        mScaleX = scaleX;
        mScaleY = scaleY;
        if (null != mOldViewReference && null != mOldViewReference.get()) {
            scale(mOldViewReference.get(), 1f, 1f);
            mOldViewReference.clear();
        }
        if (scaleX != 1f && scaleY != 1f) {
            mOldViewReference = new WeakReference<>(focusView);
        }
        runFocusViewAnimation(focusView, scaleX, scaleY);
    }

    public void setAnimDuration(int mAnimDuration) {
        this.mAnimDuration = mAnimDuration;
    }

    public void setPadding(int padding) {
        mPaddingOfsetRectF.left = padding;
        mPaddingOfsetRectF.top = padding;
        mPaddingOfsetRectF.right = padding;
        mPaddingOfsetRectF.bottom = padding;
    }


    private static class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        private WeakReference<BaseFlyingFrameView> mFlyingFrameViewRef;
        private int mScrolledX = 0, mScrolledY = 0;

        public RecyclerViewScrollListener(BaseFlyingFrameView flyingFrameView) {
            mFlyingFrameViewRef = new WeakReference<>(flyingFrameView);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mScrolledX = Math.abs(dx) == 1 ? 0 : dx;
            mScrolledY = Math.abs(dy) == 1 ? 0 : dy;
            ELog.v("onScrolled...dx=" + dx + " dy=" + dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                ELog.v("onScrollStateChanged...IDLE");
                final BaseFlyingFrameView flyingFrameView = mFlyingFrameViewRef.get();
                final View focused = recyclerView.getFocusedChild();
                ELog.v("onScrollStateChanged...border is null = " + (null == flyingFrameView));
                if (null != flyingFrameView && null != focused) {
                    if (mScrolledX != 0 || mScrolledY != 0) {
                        ELog.i("onScrollStateChanged...scleX = " + flyingFrameView.mScaleX + " scleY = " + flyingFrameView.mScaleY);
                        flyingFrameView.runBorderAnimation(focused, flyingFrameView.mScaleX, flyingFrameView.mScaleY);
                    }
                }
                mScrolledX = mScrolledY = 0;
            }
        }
    }
}
