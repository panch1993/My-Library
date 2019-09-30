package com.pan.mylibrary.widget.scrollVp;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

/**
 * Create by panchenhuan on 2019-09-26
 * walkwindc8@foxmail.com
 * Description:
 */
public class ScrollableLayout extends LinearLayout implements SwipeRefreshLayout.OnChildScrollUpCallback {

    private float mDownX;
    private float mDownY;
    private float mLastY;

    private int minY = 0;//
    private int maxY = 0;//
    private int mHeadHeight;//头部高度
    private int mExpandHeight;//扩大高度
    private int mTouchSlop;//滑动前触摸范围
    private int mMinimumVelocity;//最小速度
    private int mMaximumVelocity;//最大速度

    private DIRECTION mDirection; // 方向
    private int mCurY;//当前Y坐标
    private int mLastScrollerY;
    private boolean needCheckUpDown;//需要检查当前滑动方向
    private boolean verticalMove;//是否是上下滑动
    private boolean mDisallowIntercept;
    private boolean isClickHead;//头部点击
    private boolean isClickHeadExpand;//是否点击在头部扩大范围

    private View mHeadView;
    private ViewPager childViewPager;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private ScrollableHelper mHelper;
    private OnScrollListener onScrollListener;

    public ScrollableLayout(Context context) {
        super(context);
        init(context);
    }

    public ScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mHelper = new ScrollableHelper();
        mScroller = new Scroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mVelocityTracker = VelocityTracker.obtain();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    /**
     * 是否置顶
     */
    public boolean isSticky() {
        return mCurY == maxY;
    }

    /**
     * 扩大头部点击滑动范围
     */
    public void setClickHeadExpand(int expandHeight) {
        mExpandHeight = expandHeight;
    }

    public int getMaxY() {
        return maxY;
    }

    public boolean isHeadTop() {
        return mCurY == minY;
    }

    public boolean canPtr() {
        return verticalMove && mCurY == minY && mHelper.isTop();
    }

    public void requestTestLayoutDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        mDisallowIntercept = disallowIntercept;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentX = ev.getX();
        float currentY = ev.getY();
        float deltaY;
        int shiftX = (int) Math.abs(currentX - mDownX);
        int shiftY = (int) Math.abs(currentY - mDownY);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDisallowIntercept = false;
                needCheckUpDown = true;
                verticalMove = true;
                mDownX = currentX;
                mDownY = currentY;
                mLastY = currentY;
                checkIsClickHead((int) currentY, mHeadHeight, getScrollY());
                checkIsClickHeadExpand((int) currentY, mHeadHeight, getScrollY());
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(ev);
                mScroller.forceFinished(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDisallowIntercept) break;
                mVelocityTracker.addMovement(ev);
                deltaY = mLastY - currentY;
                if (needCheckUpDown) {
                    if (shiftX > mTouchSlop && shiftX > shiftY) {
                        needCheckUpDown = false;
                        verticalMove = false;
                    } else if (shiftY > mTouchSlop && shiftY > shiftX) {
                        needCheckUpDown = false;
                        verticalMove = true;
                    }
                }
                if (verticalMove && shiftY > mTouchSlop && shiftY > shiftX && (!isSticky() || mHelper.isTop() || isClickHeadExpand)) {
                    if (childViewPager != null) {
                        childViewPager.requestDisallowInterceptTouchEvent(true);
                    }
                    scrollBy(0, (int) (deltaY + 0.5));
                }
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                if (verticalMove && shiftY > shiftX && shiftY > mTouchSlop) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    float yVelocity = -mVelocityTracker.getYVelocity();
                    boolean disallowChild = false;
                    if (Math.abs(yVelocity) > mMinimumVelocity) {
                        mDirection = yVelocity > 0 ? DIRECTION.UP : DIRECTION.DOWN;
                        if ((mDirection == DIRECTION.UP && isSticky()) || (!isSticky() && getScrollY() == 0 && mDirection == DIRECTION.DOWN)) {
                            disallowChild = true;
                        } else {
                            mScroller.fling(0, getScrollY(), 0, (int) yVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                            mScroller.computeScrollOffset();
                            mLastScrollerY = getScrollY();
                            invalidate();
                        }
                    }
                    if (!disallowChild && (isClickHead || !isSticky())) {
                        int action = ev.getAction();
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        boolean dispatchResult = super.dispatchTouchEvent(ev);
                        ev.setAction(action);
                        return dispatchResult;
                    }
                }
                break;
        }

        boolean result =  super.dispatchTouchEvent(ev);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private int getScrollerVelocity(int distance, int duration) {
        if (mScroller == null) {
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return (int) mScroller.getCurrVelocity();
        } else {
            return distance / duration;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            final int currY = mScroller.getCurrY();
            if (mDirection == DIRECTION.UP) {// 手势向上划
                if (isSticky()) {
                    int distance = mScroller.getFinalY() - currY;
                    int duration = calcDuration(mScroller.getDuration(), mScroller.timePassed());
                    mHelper.smoothScrollBy(getScrollerVelocity(distance, duration), distance, duration);
                    mScroller.forceFinished(true);
                    return;
                } else {
                    scrollTo(0, currY);
                }
            } else {// 手势向下划
                if (mHelper.isTop() || isClickHeadExpand) {
                    int deltaY = (currY - mLastScrollerY);
                    int toY = getScrollY() + deltaY;
                    scrollTo(0, toY);
                    if (mCurY <= minY) {
                        mScroller.forceFinished(true);
                        return;
                    }
                }
                invalidate();
            }
            mLastScrollerY = currY;
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        int scrollY = getScrollY();
        int toY = scrollY + y;
        if (toY >= maxY) {
            toY = maxY;
        } else if (toY <= minY) {
            toY = minY;
        }
        y = toY - scrollY;
        super.scrollBy(x, y);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y >= maxY) {
            y = maxY;
        } else if (y <= minY) {
            y = minY;
        }
        mCurY = y;
        if (onScrollListener != null) onScrollListener.onScroll(y, maxY);
        super.scrollTo(x, y);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public ScrollableHelper getHelper() {
        return mHelper;
    }

    private void checkIsClickHead(int downY, int headHeight, int scrollY) {
        isClickHead = downY + scrollY <= headHeight;
    }

    private void checkIsClickHeadExpand(int downY, int headHeight, int scrollY) {
        if (mExpandHeight <= 0) isClickHeadExpand = false;
        isClickHeadExpand = downY + scrollY <= headHeight + mExpandHeight;
    }

    private int calcDuration(int duration, int timePass) {
        return duration - timePass;
    }

    public void scrollToTop() {
        mHelper.smoothScrollToTop();
        scrollTo(0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeadView = getChildAt(0);
        measureChildWithMargins(mHeadView, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
        maxY = mHeadView.getMeasuredHeight();
        mHeadHeight = mHeadView.getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + maxY, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onFinishInflate() {
        if (mHeadView != null && !mHeadView.isClickable()) {
            mHeadView.setClickable(true);//添加头部可点击属性
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ViewPager) {
                childViewPager = (ViewPager) childAt;//获取到ViewPager
            }
        }
        super.onFinishInflate();
    }

    @Override
    public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
        return !isHeadTop() || (isHeadTop() && !verticalMove);//true拦截触摸事件
//        return !isHeadTop();
    }

    /**
     * 滑动方向
     */
    private enum DIRECTION {
        UP,// 向上划
        DOWN// 向下划
    }

    public interface OnScrollListener {
        void onScroll(int currentY, int maxY);
    }
}
