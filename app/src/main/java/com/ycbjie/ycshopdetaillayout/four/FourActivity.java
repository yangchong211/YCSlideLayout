package com.ycbjie.ycshopdetaillayout.four;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ycbjie.slide.LoggerUtils;
import com.ycbjie.ycshopdetaillayout.R;
import com.ycbjie.ycshopdetaillayout.second.ShopMain1Fragment;
import com.ycbjie.slide.SlideAnimLayout;


public class FourActivity extends AppCompatActivity {

    private SlideAnimLayout mSlideDetailsLayout;
    private ShopMain1Fragment shopMainFragment;
    private WebView webView;
    private LinearLayout ll_page_more;
    private ImageView mIvMoreImg;
    private TextView mTvMoreText;
    private TextView tvBarGoods;
    private TextView tvBarDetail;
    private LinearLayout root;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        initView();
        initListener();
        initShopMainFragment();
        initSlideDetailsLayout();
        initWebView();
    }

    private void initView() {
        root = findViewById(R.id.root);
        mSlideDetailsLayout = findViewById(R.id.slideDetailsLayout);
        ll_page_more = findViewById(R.id.ll_page_more);
        webView = findViewById(R.id.wb_view);
        mIvMoreImg = findViewById(R.id.iv_more_img);
        mTvMoreText =  findViewById(R.id.tv_more_text);
        tvBarGoods = findViewById(R.id.tv_bar_goods);
        tvBarDetail = findViewById(R.id.tv_bar_detail);
        scrollView = findViewById(R.id.scrollView);
    }

    private void initListener() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_bar_goods:
                        tvBarGoods.setTextColor(Color.RED);
                        tvBarDetail.setTextColor(Color.BLACK);
                        mSlideDetailsLayout.smoothClose(true);
                        break;
                    case R.id.tv_bar_detail:
                        tvBarGoods.setTextColor(Color.BLACK);
                        tvBarDetail.setTextColor(Color.RED);
                        mSlideDetailsLayout.smoothOpen(true);
                        break;
                    default:
                        break;
                }
            }
        };
        tvBarGoods.setOnClickListener(onClickListener);
        tvBarDetail.setOnClickListener(onClickListener);
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
        mSlideDetailsLayout.setScrollStatusListener(new SlideAnimLayout.onScrollStatusListener() {
            @Override
            public void onStatusChanged(SlideAnimLayout.Status mNowStatus, boolean isHalf) {
                if(mNowStatus== SlideAnimLayout.Status.CLOSE){
                    //打开
                    if(isHalf){
                        mTvMoreText.setText("释放，查看图文详情");
                        mIvMoreImg.animate().rotation(0);
                        LoggerUtils.i("onStatusChanged---CLOSE---释放"+isHalf);
                    }else{//关闭
                        mTvMoreText.setText("继续上拉，查看图文详情");
                        mIvMoreImg.animate().rotation(180);
                        LoggerUtils.i("onStatusChanged---CLOSE---继续上拉"+isHalf);
                    }
                }else{
                    //打开
                    if(isHalf){
                        mTvMoreText.setText("下拉回到商品详情");
                        mIvMoreImg.animate().rotation(0);
                        LoggerUtils.i("onStatusChanged---OPEN---下拉回到商品详情"+isHalf);
                    }else{//关闭
                        mTvMoreText.setText("释放回到商品详情");
                        mIvMoreImg.animate().rotation(180);
                        LoggerUtils.i("onStatusChanged---OPEN---释放回到商品详情"+isHalf);
                    }
                }
            }
        });
        mSlideDetailsLayout.setOnSlideStatusListener(new SlideAnimLayout.OnSlideStatusListener() {
            @Override
            public void onStatusChanged(SlideAnimLayout.Status status) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)
                        root.getLayoutParams();
                if (status == SlideAnimLayout.Status.OPEN) {
                    layoutParams.topMargin = dip2px(FourActivity.this,44.0f);
                    root.setLayoutParams(layoutParams);
                    LoggerUtils.i("setOnSlideStatusListener---OPEN---下拉回到商品详情");
                    //scrollView.scrollTo(0,0);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            //ScrollView滑动到顶部
                            scrollView.fullScroll(ScrollView.FOCUS_UP);
                        }
                    });
                } else {
                    layoutParams.topMargin = dip2px(FourActivity.this,0);
                    root.setLayoutParams(layoutParams);
                    LoggerUtils.i("setOnSlideStatusListener---CLOSE---上拉查看图文详情");
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




    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
