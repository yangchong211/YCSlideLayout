package com.ycbjie.ycshopdetaillayout.second;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ycbjie.ycshopdetaillayout.R;
import com.ycbjie.ycshopdetaillayoutlib.SlideDetailsAnimLayout;


public class SecondActivity extends AppCompatActivity {

    private SlideDetailsAnimLayout mSlideDetailsLayout;
    private ShopMain1Fragment shopMainFragment;
    private WebView webView;
    private LinearLayout ll_page_more;
    private ImageView mIvMoreImg;
    private TextView mTvMoreText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        initView();
        initShopMainFragment();
        initSlideDetailsLayout();
        initWebView();
    }

    private void initView() {
        mSlideDetailsLayout = findViewById(R.id.slideDetailsLayout);
        ll_page_more = findViewById(R.id.ll_page_more);
        webView = findViewById(R.id.wb_view);
        mIvMoreImg = findViewById(R.id.iv_more_img);
        mTvMoreText =  findViewById(R.id.tv_more_text);

    }


    private void initShopMainFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(shopMainFragment==null){
            shopMainFragment = new ShopMain1Fragment();
            fragmentTransaction
                    .replace(R.id.fl_shop_main2, shopMainFragment)
                    .commit();
        }else {
            fragmentTransaction.show(shopMainFragment);
        }
    }

    private void initSlideDetailsLayout() {
        ll_page_more.measure(0,0);
        mSlideDetailsLayout.setPageAnimationHeight(ll_page_more.getMeasuredHeight());
        mSlideDetailsLayout.requestLayout();
        mSlideDetailsLayout.setScrollStatusListener(new SlideDetailsAnimLayout.onScrollStatusListener() {
            @Override
            public void onStatusChanged(SlideDetailsAnimLayout.Status mNowStatus,boolean isHalf) {
                if(mNowStatus==SlideDetailsAnimLayout.Status.CLOSE){
                    if(isHalf){//打开
                        mTvMoreText.setText("释放，查看图文详情");
                        mIvMoreImg.animate().rotation(0);
                    }else{//关闭
                        mTvMoreText.setText("继续上拉，查看图文详情");
                        mIvMoreImg.animate().rotation(180);
                    }
                }else{
                    if(isHalf){//打开
                        mTvMoreText.setText("下拉回到商品详情");
                        mIvMoreImg.animate().rotation(0);
                    }else{//关闭
                        mTvMoreText.setText("释放回到商品详情");
                        mIvMoreImg.animate().rotation(180);
                    }
                }
            }
        });
    }


    @SuppressLint({"ObsoleteSdkInt", "SetJavaScriptEnabled"})
    private void initWebView() {
        final WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            new Object() {
                void setLoadWithOverviewMode(boolean overview) {
                    settings.setLoadWithOverviewMode(overview);
                }
            }.setLoadWithOverviewMode(true);
        }

        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("https://www.jianshu.com/p/d745ea0cb5bd");
            }
        });
    }


}
