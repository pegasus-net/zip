package com.icarus.unzip.coustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.view.MotionEventCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by ctrun on 2016/7/27.
 * 用于解决SwipeRefreshLayout与ViewPager滑动事件与下拉刷新冲突问题
 */
public class ViewPagerSwipeRefreshLayout extends SwipeRefreshLayout {

    private boolean disallowIntercept;

    public ViewPagerSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public ViewPagerSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            disallowIntercept = false;
        }
        //如果子控件请求不要拦截这个手势，那么就直接返回false，不拦截它的事件
        if(disallowIntercept) {
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        //disallowIntercept为true代表子控件请求父控件不要拦截事件
        this.disallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }
}
