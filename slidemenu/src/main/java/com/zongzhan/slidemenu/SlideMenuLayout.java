package com.zongzhan.slidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


public class SlideMenuLayout extends ViewGroup {

    private final int mTouchSlop;
    private final Scroller mScroller;
    private final VelocityTracker mVelocityTracker;
    private int mChildrenTotalWidth;
    private int mActionDownX;
    private int mInitialTouchX;
    private int mState = SCROLL_STATE_IDLE;
    private boolean mOpen = false;
    private SlideMenuListener slideMenuListener;

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    public SlideMenuLayout(Context context) {
        this(context, null);
    }

    public SlideMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();
    }

    public void open() {
        mScroller.forceFinished(true);
        mScroller.startScroll(getScrollX(), 0, mChildrenTotalWidth - getMeasuredWidth() - getScrollX(), 0, 500);
        invalidate();
        setState(SCROLL_STATE_SETTLING);
        final boolean o = mOpen;
        mOpen = true;
        if (!o && slideMenuListener != null) {
            slideMenuListener.onOpenChanged(true);
        }
    }

    public void close() {
        mScroller.forceFinished(true);
        mScroller.startScroll(getScrollX(), 0, - getScrollX(), 0, 500);
        invalidate();
        setState(SCROLL_STATE_SETTLING);
        final boolean o = mOpen;
        mOpen = false;
        if (o && slideMenuListener != null) {
            slideMenuListener.onOpenChanged(false);
        }
    }

    public boolean isMenuOpen() {
        return mOpen;
    }

    public void setSlideMenuListener(SlideMenuListener l) {
        this.slideMenuListener = l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mVelocityTracker.clear();
                mActionDownX = (int) ev.getX();
                setState(SCROLL_STATE_IDLE);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(ev);
                if (mActionDownX - ev.getX() > mTouchSlop && !mOpen && mState != SCROLL_STATE_DRAGGING) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setState(SCROLL_STATE_DRAGGING);
                    mInitialTouchX = (int) ev.getX();
                    return true;
                }

                if (ev.getX() - mActionDownX  > mTouchSlop && mOpen && mState != SCROLL_STATE_DRAGGING) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setState(SCROLL_STATE_DRAGGING);
                    mInitialTouchX = (int) ev.getX();
                    return true;
                }
                break;
        }


        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                mActionDownX = (int) event.getX();
                setState(SCROLL_STATE_IDLE);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                if (mActionDownX - event.getX() > mTouchSlop && !mOpen && mState != SCROLL_STATE_DRAGGING) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setState(SCROLL_STATE_DRAGGING);
                    mInitialTouchX = (int) event.getX();
                }

                if (event.getX() - mActionDownX  > mTouchSlop && mOpen && mState != SCROLL_STATE_DRAGGING) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setState(SCROLL_STATE_DRAGGING);
                    mInitialTouchX = (int) event.getX();
                }

                if (mState == SCROLL_STATE_DRAGGING) {
                    if (mOpen) {
                        int dx = (int) (event.getX() - mInitialTouchX);
                        if (dx < 0) {
                            dx = 0;
                        } else if (dx > mChildrenTotalWidth - getMeasuredWidth()) {
                            dx = mChildrenTotalWidth - getMeasuredWidth();
                        }
                        scrollTo(mChildrenTotalWidth - getMeasuredWidth() - dx, 0);
                    } else {
                        int dx = (int) (mInitialTouchX - event.getX());
                        if (dx < 0) {
                            dx = 0;
                        } else if (dx > mChildrenTotalWidth - getMeasuredWidth()) {
                            dx = mChildrenTotalWidth - getMeasuredWidth();
                        }
                        scrollTo(dx, 0);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(100);
                float xAddOn = mVelocityTracker.getXVelocity();
                boolean shouldOpen = getScrollX() - xAddOn > (mChildrenTotalWidth - getMeasuredWidth()) / 2;
                if (shouldOpen) {
                    open();
                } else {
                    close();
                }
                mVelocityTracker.clear();
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        mChildrenTotalWidth = 0;
        for(int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() == GONE) {
                continue;
            }
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            mChildrenTotalWidth += view.getMeasuredWidth();
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed) {
            return;
        }
        int xOffset = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() == GONE) {
                continue;
            }
            view.layout(xOffset, 0, xOffset + view.getMeasuredWidth(), view.getMeasuredHeight());
            xOffset += view.getMeasuredWidth();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            scrollTo(x, y);
            invalidate();
            if (mScroller.isFinished()) {
                setState(SCROLL_STATE_IDLE);
            }
        }
    }

    private void setState(int state) {
        final int oldState = mState;
        mState = state;
        if (mState != oldState && slideMenuListener != null) {
            slideMenuListener.onStateChanged(mState);
        }
    }

    public interface SlideMenuListener {
        void onOpenChanged(boolean open);
        void onStateChanged(int state);
    }
}
