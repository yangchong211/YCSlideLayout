package com.ycbjie.slide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
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

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211/YCShopDetailLayout
 *     time  : 2018/6/6
 *     desc  : SlideAnimLayout
 *     revise:
 * </pre>
 */
public class SlideAnimLayout extends ViewGroup {

    private ValueAnimator animator;

    public enum Status {
        /**
         * 关闭
         */
        CLOSE,
        /**
         * 打开
         */
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
    private int animHeight;


    public SlideAnimLayout(Context context) {
        this(context, null);
    }

    public SlideAnimLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAnimLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("CustomViewStyleable")
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout, defStyleAttr, 0);
        mDuration = a.getInt(R.styleable.SlideLayout_duration, DEFAULT_DURATION);
        mDefaultPanel = a.getInt(R.styleable.SlideLayout_default_panel, 0);
        a.recycle();
        //在我们认为用户正在滚动之前，触摸可以移动的像素距离
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }


    /**
     * 打开商详页
     * @param smooth
     */
    public void smoothOpen(boolean smooth) {
        if (mStatus != Status.OPEN) {
            mStatus = Status.OPEN;
            //控件的高度+动画布局
            final float height = -getMeasuredHeight() - animHeight;
            LoggerUtils.i("SlideLayout---smoothOpen---"+height);
            animatorSwitch(0, height, true, smooth ? mDuration : 0);
        }
    }


    /**
     * 关闭商详页
     * @param smooth
     */
    public void smoothClose(boolean smooth) {
        if (mStatus != Status.CLOSE) {
            mStatus = Status.CLOSE;
            final float height = -getMeasuredHeight();
            LoggerUtils.i("SlideLayout---smoothClose---"+height);
            animatorSwitch(height, 0, true, smooth ? mDuration : 0);
        }
    }


    /**
     * 这个方法是在结束inflate之后才会执行
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (1 >= childCount) {
            throw new RuntimeException("SlideDetailsLayout only accept childs more than 1!!");
        }
        LoggerUtils.i("获取子节点的个数"+childCount);
        mFrontView = getChildAt(0);
        mAnimView = getChildAt(1);
        mBehindView = getChildAt(2);
        mAnimView.post(new Runnable() {
            @Override
            public void run() {
                animHeight = mAnimView.getHeight();
                LoggerUtils.i("获取控件高度"+animHeight);
            }
        });
        if(mDefaultPanel == 1){
            post(new Runnable() {
                @Override
                public void run() {
                    //默认效果
                    smoothOpen(false);
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setScrollStatusListener(null);
        setOnSlideStatusListener(null);
        if (animator!=null){
            animator.cancel();
            animator = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽高
        final int pWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int pHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(pWidth, MeasureSpec.EXACTLY);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(pHeight, MeasureSpec.EXACTLY);
        View child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);
            //当控件是Gone的时候，不进行测量
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            //当孩子控件是动画控件时，则特殊处理
            if(getChildAt(i) == mAnimView){
                child.measure(0,0);
                int measuredHeight = child.getMeasuredHeight();
                int makeMeasureSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);
                LoggerUtils.i("onMeasure获取控件高度"+measuredHeight);
                measureChild(child, childWidthMeasureSpec, makeMeasureSpec);
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
            if (child.getVisibility() == GONE) {
                continue;
            }
            LoggerUtils.i("onLayout，offset---"+offset);
            int measuredHeight = getChildAt(1).getMeasuredHeight();
            if (child == mBehindView) {
                top = b + offset + measuredHeight ;
                bottom = top + b - t + measuredHeight;
                LoggerUtils.i("onLayout，mBehindView---"+top+"-----"+bottom);
            }else if(child == mAnimView){
                top = b + offset;
                bottom = top - t + child.getMeasuredHeight();
                LoggerUtils.i("onLayout，mAnimView---"+top+"-----"+bottom);
            } else {
                top = t + offset;
                bottom = b + offset;
                LoggerUtils.i("onLayout，other---"+top+"-----"+bottom);
            }
            child.layout(l, top, r, bottom);
        }
    }


    /**
     * 分发事件:事件分发的对象是事件。注意，事件分发是向下传递的，也就是父到子的顺序。
     * 根据内部拦截状态，向其child或者自己分发事件
     *
     * @param ev
     * @return
     */
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
            case MotionEvent.ACTION_DOWN:
                mInitMotionX = ev.getX();
                mInitMotionY = ev.getY();
                shouldIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final float x = ev.getX();
                final float y = ev.getY();
                final float xDiff = x - mInitMotionX;
                final float yDiff = y - mInitMotionY;
                boolean close = mStatus == Status.CLOSE && yDiff > 0;
                boolean open = mStatus == Status.OPEN && yDiff < 0;
                if (!canChildScrollVertically((int) yDiff)) {
                    final float xDiffers = Math.abs(xDiff);
                    final float yDiffers = Math.abs(yDiff);
                    if (yDiffers > mTouchSlop && yDiffers >= xDiffers && !(close || open)) {
                        shouldIntercept = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
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
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE: {
                //获取滑动点y轴的位移
                final float y = ev.getY();
                final float yDiff = y - mInitMotionY;
                boolean childScrollVertically = canChildScrollVertically(((int) yDiff));
                //在关闭状态并且滑动位移小于等于0时
                boolean isDiffZero = yDiff<=0 && Status.OPEN == mStatus;
                boolean isAnimOpen = Status.OPEN == mStatus && yDiff>=animHeight;
                boolean isAnimClose = Status.CLOSE == mStatus && Math.abs(yDiff)>=animHeight;
                if (childScrollVertically  || isDiffZero) {
                    wantTouch = false;
                }else if(isAnimOpen|| isAnimClose){
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
            default:
                break;
        }
        return wantTouch;
    }


    /**
     * 设置方法是触摸滑动的时候
     * @param offset                        offset
     */
    private void processTouchEvent(final float offset) {
        if (Math.abs(offset) < mTouchSlop) {
            return;
        }
        final float oldOffset = mSlideOffset;
        if (mStatus == Status.CLOSE) {
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
                mSlideOffset = pHeight- animHeight + offset;
            }
            if (mSlideOffset == oldOffset) {
                return;
            }
        }

        if (Status.CLOSE == mStatus) {
            if (offset <= -animHeight/2) {
                 LoggerUtils.i("准备翻下页，已超过一半");
                if(listener!=null){
                    listener.onStatusChanged(mStatus, true);
                }
            } else {
                LoggerUtils.i("准备翻下页，不超过一半");
                if(listener!=null){
                    listener.onStatusChanged(mStatus, false);
                }
            }
        } else if (Status.OPEN == mStatus) {
            if ((offset ) >= animHeight/2) {
                if(listener!=null){
                    listener.onStatusChanged(mStatus, false);
                }
                LoggerUtils.i("准备翻上页，已超过一半:offset:"+offset+"--->pHeight:"+"--->:"+animHeight);
            } else {
                if(listener!=null){
                    listener.onStatusChanged(mStatus, true);
                }
                LoggerUtils.i("准备翻上页，不超过一半"+offset+"--->pHeight:"+"--->:"+animHeight);
            }
        }
        requestLayout();
    }


    /**
     * 结束触摸
     */
    private void finishTouchEvent() {
        final int pHeight = getMeasuredHeight();
        LoggerUtils.i("finishTouchEvent------pHeight---"+pHeight);
        final float offset = mSlideOffset;
        boolean changed = false;
        if (Status.CLOSE == mStatus) {
            if (offset <= -animHeight /2) {
                mSlideOffset = -pHeight - animHeight;
                mStatus = Status.OPEN;
                changed = true;
            } else {
                mSlideOffset = 0;
            }
            LoggerUtils.i("finishTouchEvent----CLOSE--mSlideOffset---"+mSlideOffset);
        } else if (Status.OPEN == mStatus) {
            if ((offset + pHeight) >= -animHeight/2) {
                mSlideOffset = 0;
                mStatus = Status.CLOSE;
                changed = true;
            } else {
                mSlideOffset = -pHeight - animHeight;
            }
            LoggerUtils.i("finishTouchEvent----OPEN-----"+mSlideOffset);
        }
        animatorSwitch(offset, mSlideOffset, changed);
    }


    /**
     * 共同调用的方法
     */
    private void animatorSwitch(final float start, final float end, final boolean changed) {
        animatorSwitch(start, end, changed, mDuration);
    }

    private void animatorSwitch(final float start, final float end,
                                final boolean changed, final long duration) {
        animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSlideOffset = (float) animation.getAnimatedValue();
                LoggerUtils.i("animatorSwitch----onAnimationUpdate-----"+mSlideOffset);
                requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (changed) {
                    if (mStatus == Status.OPEN && isFirstShowBehindView) {
                        isFirstShowBehindView = false;
                        mBehindView.setVisibility(VISIBLE);
                    }
                    if (onSlideStatusListener!=null){
                        onSlideStatusListener.onStatusChanged(mStatus);
                    }
                }
            }
        });
        animator.setDuration(duration);
        animator.start();
    }


    private void ensureTarget() {
        if (mStatus == Status.CLOSE) {
            mTarget = mFrontView;
        } else {
            mTarget = mBehindView;
        }
    }


    /**
     * 是否可以滑动，direction为负数时表示向下滑动，反之表示向上滑动。
     * @param direction                         direction
     * @return
     */
    protected boolean canChildScrollVertically(int direction) {
        if (mTarget instanceof AbsListView) {
            return canListViewScroll((AbsListView) mTarget);
        } else if (mTarget instanceof FrameLayout || mTarget instanceof RelativeLayout ||
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

        public static final Creator<SavedState> CREATOR = new Creator<SlideAnimLayout.SavedState>() {
            @Override
            public SlideAnimLayout.SavedState createFromParcel(Parcel in) {
                return new SlideAnimLayout.SavedState(in);
            }

            @Override
            public SlideAnimLayout.SavedState[] newArray(int size) {
                return new SlideAnimLayout.SavedState[size];
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

    private OnSlideStatusListener onSlideStatusListener;
    public interface OnSlideStatusListener {
        void onStatusChanged(Status status);
    }
    public void setOnSlideStatusListener(OnSlideStatusListener listener){
        this.onSlideStatusListener = listener;
    }

}
