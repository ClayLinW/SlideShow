package com.clay.slideshowdemo.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * HorizontalScrollView实现滚动状态监听
 */

public class CustomHorizontalScrollView extends HorizontalScrollView {

    private Handler mHandler;

    public CustomHorizontalScrollView(Context context) {
        this(context, null);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new Handler();
    }

    /**
     * 滚动状态:
     * IDLE=滚动停止
     * TOUCH_SCROLL=手指拖动滚动
     * FLING=滚动
     */
    public enum ScrollStatus {
        IDLE,
        TOUCH_SCROLL,
        FLING
    }

    /**
     * 记录当前滚动的距离
     */
    private int currentX = 0;

    /**
     * 当前滚动状态
     */
    private ScrollStatus scrollStatus = ScrollStatus.IDLE;

    private ScrollStatusListener mScrollStatusListener;

    public interface ScrollStatusListener {
        void onScrollChanged(ScrollStatus scrollStatus);
    }

    public void setScrollStatusListener(ScrollStatusListener scrollStatusListener) {
        this.mScrollStatusListener = scrollStatusListener;
    }

    /**
     * 滚动状态监听runnable
     */
    private Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (getScrollX() == currentX) {
                //滚动停止,取消监听线程
                scrollStatus = ScrollStatus.IDLE;
                if (mScrollStatusListener != null) {
                    mScrollStatusListener.onScrollChanged(scrollStatus);
                }
                mHandler.removeCallbacks(this);
                return;
            } else {
                //手指离开屏幕,但是view还在滚动
                scrollStatus = ScrollStatus.FLING;
                if (mScrollStatusListener != null) {
                    mScrollStatusListener.onScrollChanged(scrollStatus);
                }
            }
            currentX = getScrollX();
            //滚动监听间隔:milliseconds
            mHandler.postDelayed(this, 50);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                this.scrollStatus = ScrollStatus.TOUCH_SCROLL;
                if (mScrollStatusListener != null) {
                    mScrollStatusListener.onScrollChanged(scrollStatus);
                }
                mHandler.removeCallbacks(scrollRunnable);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.post(scrollRunnable);
                break;
        }
        return super.onTouchEvent(ev);
    }

    private ScrollViewListener scrollViewListener = null;

    //滚动监听
    public interface ScrollViewListener {
        void onScrollChanged(View chsv, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, scrollX, scrollY, oldScrollX, oldScrollY);
        }
    }

}

