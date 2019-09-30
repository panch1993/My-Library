package com.pan.mylibrary.widget.scrollVp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Create by panchenhuan on 2019-09-26
 * walkwindc8@foxmail.com
 * Description:
 */
public class ScrollableHelper {
    private ScrollableContainer currScrollableContainer;

    private static boolean isRecyclerViewTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);
                if (childAt == null || (firstVisibleItemPosition == 0 && layoutManager.getDecoratedTop(childAt) == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isAdapterViewTop(AdapterView adapterView) {
        if (adapterView != null) {
            int firstVisiblePosition = adapterView.getFirstVisiblePosition();
            View childAt = adapterView.getChildAt(0);
            if (childAt == null || (firstVisiblePosition == 0 && childAt.getTop() == 0)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isScrollViewTop(ScrollView scrollView) {
        if (scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    private static boolean isWebViewTop(WebView scrollView) {
        if (scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    public void setCurrentScrollableContainer(ScrollableContainer scrollableContainer) {
        this.currScrollableContainer = scrollableContainer;
    }

    private View getScrollableView() {
        if (currScrollableContainer == null) return null;
        return currScrollableContainer.getScrollableView();
    }

    /**
     * 判断是否滑动到顶部方法,ScrollAbleLayout根据此方法来做一些逻辑判断
     * 目前只实现了AdapterView,ScrollView,RecyclerView
     * 需要支持其他view可以自行补充实现
     */
    public boolean isTop() {
        View scrollableView = getScrollableView();
        if (scrollableView == null) return true;
        if (scrollableView instanceof AdapterView) {
            return isAdapterViewTop((AdapterView) scrollableView);
        }
        if (scrollableView instanceof ScrollView) {
            return isScrollViewTop((ScrollView) scrollableView);
        }
        if (scrollableView instanceof RecyclerView) {
            return isRecyclerViewTop((RecyclerView) scrollableView);
        }
        if (scrollableView instanceof WebView) {
            return isWebViewTop((WebView) scrollableView);
        }
        throw new IllegalStateException("scrollableView must be a instance of AdapterView|ScrollView|RecyclerView");
    }

    @SuppressLint("NewApi")
    public void smoothScrollBy(int velocityY, int distance, int duration) {
        View scrollableView = getScrollableView();
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                absListView.fling(velocityY);
            } else {
                absListView.smoothScrollBy(distance, duration);
            }
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocityY);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).fling(0, velocityY);
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).flingScroll(0, velocityY);
        }
    }

    public void smoothScrollToTop() {
        View scrollableView = getScrollableView();
        if (scrollableView instanceof AbsListView) {
            ((AbsListView) scrollableView).smoothScrollToPosition(0);
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).smoothScrollTo(0, 0);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).smoothScrollToPosition(0);
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).flingScroll(0, 0);
        }
    }

    /**
     * a viewgroup whitch contains ScrollView/ListView/RecycelerView..
     */
    public interface ScrollableContainer {
        /**
         * @return ScrollView/ListView/RecycelerView..'s instance
         */
        View getScrollableView();
    }
}
