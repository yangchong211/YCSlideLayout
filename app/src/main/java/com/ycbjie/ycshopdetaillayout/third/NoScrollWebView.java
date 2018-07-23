package com.ycbjie.ycshopdetaillayout.third;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;



public class NoScrollWebView extends WebView {

    public NoScrollWebView(Context context) {
        super(context);
        initView();
    }

    public NoScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    public NoScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }

}
