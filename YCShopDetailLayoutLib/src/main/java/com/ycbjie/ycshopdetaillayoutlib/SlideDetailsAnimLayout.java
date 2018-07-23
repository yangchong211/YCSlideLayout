package com.ycbjie.ycshopdetaillayoutlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class SlideDetailsAnimLayout extends ViewGroup {

    public enum Status {
        CLOSE,
        OPEN;
        public static Status valueOf(int stats) {
            if (0 == stats) {
                return CLOSE;
            } else if (1 == stats) {
                return OPEN;
            } else {
                return CLOSE;
            }
        }
    }

    private static final int DEFAULT_DURATION = 300;
    private int animation_height = 0;

    private View mFrontView;
    private View mAnimView;
    private View mBehindView;

    private float mTouchSlop;
    private float mInitMotionY;
    private float mInitMotionX;


    private View mTarget;
    private float mSlideOffset;
    private Status mStatus = Status.CLOSE;
    private boolean isFirstShowBehindView = true;
    private long mDuration = DEFAULT_DURATION;
    private int mDefaultPanel = 0;


    public SlideDetailsAnimLayout(Context context) {
        this(context, null);
    }

    public SlideDetailsAnimLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDetailsAnimLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("CustomViewStyleable")
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideDetailsLayout, defStyleAttr, 0);
        mDuration = a.getInt(R.styleable.SlideDetailsLayout_duration, DEFAULT_DURATION);
        mDefaultPanel = a.getInt(R.styleable.SlideDetailsLayout_default_panel, 0);
        a.recycle();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }


    public void smoothOpen(boolean smooth) {
        if (mStatus != Status.OPEN) {
            mStatus = Status.OPEN;
            final float height = -getMeasuredHeight();
            animatorSwitch(0, height, true, smooth ? mDuration : 0);
        }
    }

    public void smoothClose(boolean smooth) {
        if (mStatus != Status.CLOSE) {
            mStatus = Status.CLOSE;
            final float height = -getMeasuredHeight();
            animatorSwitch(height, 0, true, smooth ? mDuration : 0);
        }
    }


    public void setPageAnimationHeight(int height){
        this.animation_height = height;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (1 >= childCount) {
            throw new RuntimeException("SlideDetailsLayout only accept childs more than 1!!");
        }
        mFrontView = getChildAt(0);
        mAnimView = getChildAt(1);
        mBehindView = getChildAt(2);
        if(mDefaultPanel == 1){
            post(new Runnable() {
                @Override
                public void run() {
                    smoothOpen(false);
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int pWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int pHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(pWidth, MeasureSpec.EXACTLY);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(pHeight, MeasureSpec.EXACTLY);
        View child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if(getChildAt(i) == mAnimView){
                child.measure(0,0);
                measureChild(child, childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY));
            } else{
                measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
        setMeasuredDimension(pWidth, pHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top;
        int bottom;
        final int offset = (int) mSlideOffset;
        View child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);
            // skip layout
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (child == mBehindView) {
                top = b + offset+getChildAt(1).getMeasuredHeight() ;
                bottom = top + b - t+getChildAt(1).getMeasuredHeight();
            }else if(child == mAnimView){
                top = b + offset;
                bottom = top - t + child.getMeasuredHeight();
            } else {
                top = t + offset;
                bottom = b + offset;
            }
            child.layout(l, top, r, bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        if (null == mTarget) {
            return false;
        }
        if (!isEnabled()) {
            return false;
        }
        final int action = ev.getAction();
        boolean shouldIntercept = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mInitMotionX = ev.getX();
                mInitMotionY = ev.getY();
                shouldIntercept = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();

                final float xDiff = x - mInitMotionX;
                final float yDiff = y - mInitMotionY;

                if (canChildScrollVertically((int) yDiff)) {
                    shouldIntercept = false;
                } else {
                    final float xDiffabs = Math.abs(xDiff);
                    final float yDiffabs = Math.abs(yDiff);
                    if (yDiffabs > mTouchSlop && yDiffabs >= xDiffabs
                            && !(mStatus == Status.CLOSE && yDiff > 0
                            || mStatus == Status.OPEN && yDiff < 0)) {
                        shouldIntercept = true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                shouldIntercept = false;
                break;
            }

        }
        return shouldIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ensureTarget();
        if (null == mTarget) {
            return false;
        }
        if (!isEnabled()) {
            return false;
        }
        boolean wantTouch = true;
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (mTarget instanceof View) {
                    wantTouch = true;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float y = ev.getY();
                final float yDiff = y - mInitMotionY;
                if (canChildScrollVertically(((int) yDiff))  || (yDiff<=0 && Status.OPEN == mStatus) ) {
                    wantTouch = false;
                }else if((Status.OPEN == mStatus && yDiff>=animation_height)
                        || (Status.CLOSE == mStatus && Math.abs(yDiff)>=animation_height )){
                    wantTouch = true;
                } else {
                    processTouchEvent(yDiff);
                    wantTouch = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                finishTouchEvent();
                wantTouch = false;
                break;
            }
        }
        return wantTouch;
    }


    private void processTouchEvent(final float offset) {
        if (Math.abs(offset) < mTouchSlop) {
            return;
        }
        final float oldOffset = mSlideOffset;
        // pull up to open
        if (mStatus == Status.CLOSE) {
            // reset if pull down
            if (offset >= 0) {
                mSlideOffset = 0;
            } else {
                mSlideOffset = offset;
            }
            if (mSlideOffset == oldOffset) {
                return;
            }
        } else if (mStatus == Status.OPEN) {
            final float pHeight = -getMeasuredHeight();
            if (offset <= 0) {
                mSlideOffset = pHeight;
            } else {
                //此处导致下面的view显示不全
                final float newOffset = pHeight-getChildAt(1).getMeasuredHeight() + offset;
                mSlideOffset = newOffset;
            }
            if (mSlideOffset == oldOffset) {
                return;
            }
        }

        final int percent = animation_height;
        if (Status.CLOSE == mStatus) {
            if (offset <= -percent/2) {
                // LoggerUtils.i("准备翻下页，已超过一半");
                if(listener!=null){
                    listener.onStatusChanged(mStatus, true);
                }
            } else {
                //LoggerUtils.i("准备翻下页，不超过一半");
                if(listener!=null){
                    listener.onStatusChanged(mStatus, false);
                }
            }
        } else if (Status.OPEN == mStatus) {
            if ((offset ) >= percent/2) {
                if(listener!=null){
                    listener.onStatusChanged(mStatus, false);
                }
                //LoggerUtils.i("准备翻上页，已超过一半:offset:"+offset+"--->pHeight:"+pHeight+"--->:"+percent);
            } else {
                if(listener!=null){
                    listener.onStatusChanged(mStatus, true);
                }
                //LoggerUtils.i("准备翻上页，不超过一半"+offset+"--->pHeight:"+pHeight+"--->:"+percent);
            }
        }
        requestLayout();
    }

    private void finishTouchEvent() {
        final int pHeight = getMeasuredHeight();
        final int percent = animation_height;
        final float offset = mSlideOffset;
        boolean changed = false;
        if (Status.CLOSE == mStatus) {
            if (offset <= -percent /2) {
                mSlideOffset = -pHeight - getChildAt(1).getMeasuredHeight();
                mStatus = Status.OPEN;
                changed = true;
            } else {
                mSlideOffset = 0;
            }
        } else if (Status.OPEN == mStatus) {
            if ((offset + pHeight) >= -percent/2) {
                mSlideOffset = 0;
                mStatus = Status.CLOSE;
                changed = true;
            } else {
                mSlideOffset = -pHeight - getChildAt(1).getMeasuredHeight();
            }
        }
        animatorSwitch(offset, mSlideOffset, changed);
    }

    private void animatorSwitch(final float start, final float end, final boolean changed) {
        animatorSwitch(start, end, changed, mDuration);
    }

    private void animatorSwitch(final float start, final float end, final boolean changed, final long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSlideOffset = (float) animation.getAnimatedValue();
                requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (changed && mStatus == Status.OPEN) {
                    checkAndFirstOpenPanel();
                }
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    private void checkAndFirstOpenPanel() {
        if (isFirstShowBehindView) {
            isFirstShowBehindView = false;
            mBehindView.setVisibility(VISIBLE);
        }
    }

    private void ensureTarget() {
        if (mStatus == Status.CLOSE) {
            mTarget = mFrontView;
        } else {
            mTarget = mBehindView;
        }
    }


    protected boolean canChildScrollVertically(int direction) {
        if (mTarget instanceof AbsListView) {
            return canListViewScroll((AbsListView) mTarget);
        } else if (mTarget instanceof FrameLayout ||
                mTarget instanceof RelativeLayout ||
                mTarget instanceof LinearLayout) {
            View child;
            for (int i = 0; i < ((ViewGroup) mTarget).getChildCount(); i++) {
                child = ((ViewGroup) mTarget).getChildAt(i);
                if (child instanceof AbsListView) {
                    return canListViewScroll((AbsListView) child);
                }
            }
        }
        return ViewCompat.canScrollVertically(mTarget, -direction);
    }

    protected boolean canListViewScroll(AbsListView absListView) {
        if (mStatus == Status.OPEN) {
            return absListView.getChildCount() > 0
                    && (absListView.getFirstVisiblePosition() > 0
                    || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
        } else {
            final int count = absListView.getChildCount();
            return count > 0 && (absListView.getLastVisiblePosition() < count - 1
                    || absListView.getChildAt(count - 1).getBottom() > absListView.getMeasuredHeight());
        }
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.offset = mSlideOffset;
        ss.status = mStatus.ordinal();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mSlideOffset = ss.offset;
        mStatus = Status.valueOf(ss.status);
        if (mStatus == Status.OPEN) {
            mBehindView.setVisibility(VISIBLE);
        }
        requestLayout();
    }

    private static class SavedState extends BaseSavedState {

        private float offset;
        private int status;

        SavedState(Parcel source) {
            super(source);
            offset = source.readFloat();
            status = source.readInt();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(offset);
            out.writeInt(status);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public interface onScrollStatusListener{
        /**
         * 监听方法
         * @param status            状态
         * @param isHalf            是否是一半距离
         */
        void onStatusChanged(Status status, boolean isHalf);
    }
    private onScrollStatusListener listener;
    public void setScrollStatusListener(onScrollStatusListener listener){
        this.listener = listener;
    }


}
