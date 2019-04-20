package com.ycbjie.ycshopdetaillayout.first;

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
import android.widget.LinearLayout;

import com.ycbjie.ycshopdetaillayout.R;
import com.ycbjie.slide.SlideLayout;

public class FirstActivity extends AppCompatActivity {

    private SlideLayout mSlideDetailsLayout;
    private ShopMainFragment shopMainFragment;
    private WebView webView;
    private LinearLayout mLlDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        initView();
        initShopMainFragment();
        initSlideDetailsLayout();
        initWebView();
    }

    private void initView() {
        mSlideDetailsLayout = findViewById(R.id.slideDetailsLayout);
        webView = findViewById(R.id.wb_view);
        mLlDetail = findViewById(R.id.ll_detail);
    }


    private void initShopMainFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(shopMainFragment==null){
            shopMainFragment = new ShopMainFragment();
            fragmentTransaction
                    .replace(R.id.fl_shop_main, shopMainFragment)
                    .commit();
        }else {
            fragmentTransaction.show(shopMainFragment);
        }
    }

    private void initSlideDetailsLayout() {
        mSlideDetailsLayout.setOnSlideDetailsListener(new SlideLayout.OnSlideDetailsListener() {
            @Override
            public void onStatusChanged(SlideLayout.Status status) {
                if (status == SlideLayout.Status.OPEN) {
                    //当前为图文详情页
                    Log.e("FirstActivity","下拉回到商品详情");
                    shopMainFragment.changBottomView(true);
                } else {
                    //当前为商品详情页
                    Log.e("FirstActivity","继续上拉，查看图文详情");
                    shopMainFragment.changBottomView(false);
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
