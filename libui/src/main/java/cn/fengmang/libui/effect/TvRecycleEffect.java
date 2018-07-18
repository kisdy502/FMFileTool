package cn.fengmang.libui.effect;

import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import cn.fengmang.baselib.ELog;
import cn.fengmang.libui.recycler.FMRecyclerView;

/**
 * Created by SDT13411 on 2018/1/15.
 */

public class TvRecycleEffect extends BaseEffect {

    protected int mDuration = 250;
    protected AnimatorSet mAnimatorSet;
    protected IntEvaluator mEvaluator = new IntEvaluator();

    protected int mNewWidth = 0;
    protected int mNewHeight = 0;
    protected int mOldWidth = 0;
    protected int mOldHeight = 0;

    @Override
    public boolean onFocusViewDraw(Canvas canvas) {
        if (mFocusDrawable != null && mFocusFramePadding != null) {
            canvas.save();
            int width = mFocusFrameView.getWidth();
            int height = mFocusFrameView.getHeight();
            Rect padding = new Rect();
            // 边框的绘制.
            mFocusDrawable.getPadding(padding);
            mFocusDrawable.setBounds(-padding.left - (mFocusFramePadding.left), -padding.top - (mFocusFramePadding.top),
                    width + padding.right + (mFocusFramePadding.right), height + padding.bottom + (mFocusFramePadding.bottom));
            mFocusDrawable.draw(canvas);
            canvas.restore();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onUnFocusView(View view) {
        if (view == null) return;
        view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(mDuration).start();
    }

    @Override
    public void onFocusView(View focusView, float scaleX, float scaleY) {
        if (focusView == null) return;
        //执行移动动画
        runFocusMoveAnimation(focusView, mFocusFrameView, scaleX, scaleY);
    }


    protected void runFocusMoveAnimation(View focusView, final View moveView, float scaleX, float scaleY) {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
        int newX = 0;
        int newY = 0;
        if (focusView != null) {
            mNewWidth = (int) (focusView.getMeasuredWidth() * scaleX);
            mNewHeight = (int) (focusView.getMeasuredHeight() * scaleY);
            mOldWidth = moveView.getMeasuredWidth();
            mOldHeight = moveView.getMeasuredHeight();
            Rect fromRect = findLocationWithView(moveView);
            Rect toRect = findLocationWithView(focusView);
            if (null != focusView.getParent() && focusView.getParent() instanceof FMRecyclerView) {
                FMRecyclerView rv = (FMRecyclerView) focusView.getParent();
                ELog.v("getLeft:" + rv.getLeft());
                ELog.v("getTop:" + rv.getTop());
//                if (offset != -1) {
//                    toRect.offset(rv.getLayoutManager().canScrollHorizontally() ? -offset : 0,
//                            rv.getLayoutManager().canScrollVertically() ? -offset : 0);
//                }
                toRect.offset(rv.getLeft(), rv.getTop());
            }


            int x = toRect.left - fromRect.left;
            int y = toRect.top - fromRect.top;
            newX = x - Math.abs(focusView.getMeasuredWidth() - mNewWidth) / 2;
            newY = y - Math.abs(focusView.getMeasuredHeight() - mNewHeight) / 2;
        }
        mAnimatorSet = new AnimatorSet();
        //焦点框
        final ObjectAnimator transAnimatorX = ObjectAnimator.ofFloat(moveView, "translationX", newX);
        ObjectAnimator transAnimatorY = ObjectAnimator.ofFloat(moveView, "translationY", newY);
        transAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams lp = moveView.getLayoutParams();
                lp.width = mEvaluator.evaluate(valueAnimator.getAnimatedFraction(), mOldWidth, mNewWidth);
                lp.height = mEvaluator.evaluate(valueAnimator.getAnimatedFraction(), mOldHeight, mNewHeight);
                moveView.setLayoutParams(lp);
                moveView.requestLayout();
            }
        });
        //获取焦点的view
        ObjectAnimator scaleFocusViewX = ObjectAnimator.ofFloat(focusView, "scaleX", scaleX);
        ObjectAnimator scaleFocusViewY = ObjectAnimator.ofFloat(focusView, "scaleY", scaleY);
        mAnimatorSet.playTogether(transAnimatorX, transAnimatorY, scaleFocusViewX, scaleFocusViewY);
        mAnimatorSet.setInterpolator(new DecelerateInterpolator(1));
        mAnimatorSet.setDuration(mDuration);
        mAnimatorSet.start();
    }


    protected Rect findLocationWithView(View view) {
        ViewGroup root = (ViewGroup) view.getParent();
        Rect rect = new Rect();
        root.offsetDescendantRectToMyCoords(view, rect);
        return rect;
    }

}
