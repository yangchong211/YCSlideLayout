package com.yc.ycshopdetaillayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * <pre>
 *     @author yangchong
 *     blog  :
 *     time  : 2018/6/6
 *     desc  : 当WebView在最顶部或者最底部的时候，不消费事件
 *     revise:
 * </pre>
 */
public class VerticalRecyclerView extends RecyclerView {

    private float downX;
    private float downY;
    /** 第一个可见的item的位置 */
    private int firstVisibleItemPosition;
    /** 第一个的位置 */
    private int[] firstPositions;
    /** 最后一个可见的item的位置 */
    private int lastVisibleItemPosition;
    /** 最后一个的位置 */
    private int[] lastPositions;
    private boolean isTop;
    private boolean isBottom;

    public VerticalRecyclerView(Context context) {
        this(context, null);
    }

    public VerticalRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            } else if (layoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                    firstPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                staggeredGridLayoutManager.findFirstVisibleItemPositions(firstPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                firstVisibleItemPosition = findMin(firstPositions);
            }
        } else {
            throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                //如果滑动到了最底部，就允许继续向上滑动加载下一页，否者不允许
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - downX;
                float dy = ev.getY() - downY;
                boolean allowParentTouchEvent;
                if (Math.abs(dy) > Math.abs(dx)) {
                    if (dy > 0) {
                        //位于顶部时下拉，让父View消费事件
                        allowParentTouchEvent = isTop = firstVisibleItemPosition == 0 && getChildAt(0).getTop() >= 0;
                    } else {
                        //位于底部时上拉，让父View消费事件
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        allowParentTouchEvent = isBottom = visibleItemCount > 0 && (lastVisibleItemPosition) >= totalItemCount - 1 && getChildAt(getChildCount() - 1).getBottom() <= getHeight();
                    }
                } else {
                    //水平方向滑动
                    allowParentTouchEvent = true;
                }
                getParent().requestDisallowInterceptTouchEvent(!allowParentTouchEvent);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);

    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value >= max) {
                max = value;
            }
        }
        return max;
    }

    private int findMin(int[] firstPositions) {
        int min = firstPositions[0];
        for (int value : firstPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public boolean isTop() {
        return isTop;
    }

    public boolean isBottom() {
        return isBottom;
    }

    public void goTop() {
        scrollToPosition(0);
    }
}
