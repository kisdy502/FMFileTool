package cn.fengmang.libui.scroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.List;

public abstract class HorTouchViewGroup extends ViewGroup {
	private final static String TAG = "HorTouchViewGroup";

	private int mLastMotionX;
	private int mTouchSlop;
	private int mMinimumVelocity;
	private int mMaximumVelocity;

	private int mOverscrollDistance;
	private int mOverflingDistance;
	private int mActivePointerId = INVALID_POINTER;
	private static final int INVALID_POINTER = -1;
	private boolean mIsBeingDragged;
	private VelocityTracker mVelocityTracker;
	private OverScroller mScroller;

	protected List<OnFlowScrolledListener> onFlowScrollChangedListenerList;

	public HorTouchViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public HorTouchViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HorTouchViewGroup(Context context) {
		this(context, null, 0);
	}

	private void init(Context context) {
		mScroller = new OverScroller(context);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		mOverscrollDistance = configuration.getScaledOverscrollDistance();
		mOverflingDistance = configuration.getScaledOverflingDistance();
	}

	private void initOrResetVelocityTracker() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		} else {
			mVelocityTracker.clear();
		}
	}

	private void initVelocityTrackerIfNotExists() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		if (disallowIntercept) {
			recycleVelocityTracker();
		}
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/*
		 * This method JUST determines whether we want to intercept the motion.
		 * If we return true, onMotionEvent will be called and we do the actual
		 * scrolling there.
		 */

		/*
		 * Shortcut the most recurring case: the user is in the dragging state
		 * and he is moving his finger. We want to intercept this motion.
		 */
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
			return true;
		}

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE: {
			/*
			 * mIsBeingDragged == false, otherwise the shortcut would have
			 * caught it. Check whether the user has moved far enough from his
			 * original down touch.
			 */

			/*
			 * Locally do absolute value. mLastMotionX is set to the x value of
			 * the down event.
			 */
			final int activePointerId = mActivePointerId;
			if (activePointerId == INVALID_POINTER) {
				// If we don't have a valid id, the touch down wasn't on
				// content.
				break;
			}

			final int pointerIndex = ev.findPointerIndex(activePointerId);
			if (pointerIndex == -1) {
				Log.e(TAG, "Invalid pointerId=" + activePointerId + " in onInterceptTouchEvent");
				break;
			}

			final int x = (int) ev.getX(pointerIndex);
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			if (xDiff > mTouchSlop) {
				mIsBeingDragged = true;
				mLastMotionX = x;
				initVelocityTrackerIfNotExists();
				mVelocityTracker.addMovement(ev);
				if (getParent() != null)
					getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			final int x = (int) ev.getX();
			if (!inChild((int) x, (int) ev.getY())) {
				mIsBeingDragged = false;
				recycleVelocityTracker();
				break;
			}

			/*
			 * Remember location of down touch. ACTION_DOWN always refers to
			 * pointer index 0.
			 */
			mLastMotionX = x;
			mActivePointerId = ev.getPointerId(0);

			initOrResetVelocityTracker();
			mVelocityTracker.addMovement(ev);

			/*
			 * If being flinged and user touches the screen, initiate drag;
			 * otherwise don't. mScroller.isFinished should be false when being
			 * flinged.
			 */
			mIsBeingDragged = !mScroller.isFinished();
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			/* Release the drag */
			mIsBeingDragged = false;
			mActivePointerId = INVALID_POINTER;
			if (mScroller.springBack(getScrollX(), getScrollY(), 0, getScrollRange(), 0, 0)) {
				postInvalidateOnAnimation();
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int index = ev.getActionIndex();
			mLastMotionX = (int) ev.getX(index);
			mActivePointerId = ev.getPointerId(index);
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			mLastMotionX = (int) ev.getX(ev.findPointerIndex(mActivePointerId));
			break;
		}

		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		return mIsBeingDragged;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			if (getChildCount() == 0) {
				return false;
			}
			if ((mIsBeingDragged = !mScroller.isFinished())) {
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
			}

			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			// Remember where the motion event started
			mLastMotionX = (int) ev.getX();
			mActivePointerId = ev.getPointerId(0);
			break;
		}
		case MotionEvent.ACTION_MOVE:
			final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
			if (activePointerIndex == -1) {
				Log.e(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
				break;
			}

			final int x = (int) ev.getX(activePointerIndex);
			int deltaX = mLastMotionX - x;
			if (!mIsBeingDragged && Math.abs(deltaX) > mTouchSlop) {
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
				mIsBeingDragged = true;
				if (deltaX > 0) {
					deltaX -= mTouchSlop;
				} else {
					deltaX += mTouchSlop;
				}
			}
			if (mIsBeingDragged) {
				// Scroll to follow the motion event
				mLastMotionX = x;

				final int range = getScrollRange();

				// Calling overScrollBy will call onOverScrolled, which
				// calls onScrollChanged if applicable.
				if (overScrollBy(deltaX, 0, getScrollX(), 0, range, 0, mOverscrollDistance, 0, true)) {
					// Break our velocity if we hit a scroll barrier.
					mVelocityTracker.clear();
				}

			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int initialVelocity = (int) velocityTracker.getXVelocity(mActivePointerId);

				if (getChildCount() > 0) {
					if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
						fling(-initialVelocity);
					} else {
						if (mScroller.springBack(getScrollX(), getScrollY(), 0, getScrollRange(), 0, 0)) {
							postInvalidateOnAnimation();
						}
					}
				}

				mActivePointerId = INVALID_POINTER;
				mIsBeingDragged = false;
				recycleVelocityTracker();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsBeingDragged && getChildCount() > 0) {
				if (mScroller.springBack(getScrollX(), getScrollY(), 0, getScrollRange(), 0, 0)) {
					postInvalidateOnAnimation();
				}
				mActivePointerId = INVALID_POINTER;
				mIsBeingDragged = false;
				recycleVelocityTracker();
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}
		return true;
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction()
				& MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = (int) ev.getX(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
		}
	}

	private boolean inChild(int x, int y) {
		if (getChildCount() > 0) {
			final int scrollX = getScrollX();
			final View child = getChildrenParent();
			return !(y < child.getTop() || y >= child.getBottom() || x < child.getLeft() - scrollX
					|| x >= child.getRight() - scrollX);
		}
		return false;
	}

	public int getScrollRange() {
		int scrollRange = 0;
		if (getChildCount() > 0) {
			View child = getChildrenParent();
			scrollRange = Math.max(0, child.getWidth() - getShowWidth());
		}
		return scrollRange;
	}

	/**
	 * Fling the scroll view
	 *
	 * @param velocityX
	 *            The initial velocity in the X direction. Positive numbers mean
	 *            that the finger/cursor is moving down the screen, which means
	 *            we want to scroll towards the left.
	 */
	public void fling(int velocityX) {
		if (getChildCount() > 0) {
			int width = getShowWidth();
			int right = getChildrenParent().getWidth();

			mScroller.fling(getScrollX(), getScrollY(), velocityX, 0, 0, Math.max(0, right - width), 0, 0, width / 2,
					0);

			postInvalidateOnAnimation();
		}
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		// Treat animating scrolls differently; see #computeScroll() for why.
		if (!mScroller.isFinished()) {
			final int oldX = getScrollX();
			final int oldY = getScrollY();
			setScrollX(scrollX);
			setScrollY(scrollY);
			invalidateParentIfNeeded();
			onScrollChanged(scrollX, scrollY, oldX, oldY);
			if (clampedX) {
				mScroller.springBack(scrollX, scrollY, 0, getScrollRange(), 0, 0);
			}
		} else {
			super.scrollTo(scrollX, scrollY);
		}
	}

	protected void invalidateParentIfNeeded() {
		if (isHardwareAccelerated() && getParent() instanceof View) {
			((View) getParent()).invalidate();
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();

			if (oldX != x || oldY != y) {
				final int range = getScrollRange();

				overScrollBy(x - oldX, y - oldY, oldX, oldY, range, 0, mOverflingDistance, 0, false);
				onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
			}

			if (!awakenScrollBars()) {
				postInvalidateOnAnimation();
			}
		}
	}

	@SuppressLint("NewApi")
	public void postInvalidateOnAnimation() {
		if (Build.VERSION.SDK_INT >= 16) {
			super.postInvalidateOnAnimation();
		}
	}

	public void addOnFlowScrolledListener(OnFlowScrolledListener listener) {
		if (onFlowScrollChangedListenerList == null) {
			onFlowScrollChangedListenerList = new ArrayList<OnFlowScrolledListener>();
		}
		onFlowScrollChangedListenerList.add(listener);
	}

	protected ViewGroup getChildrenParent() {
		return (ViewGroup) getChildAt(0);
	}

	protected int getShowWidth() {
		return getWidth() - getPaddingLeft() - getPaddingRight();
	}
}
