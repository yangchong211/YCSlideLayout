package com.yc.ycshopdetaillayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yc.slide.VerticalScrollView;
import com.ycbjie.ycshopdetaillayout.R;

public class FiveActivity extends AppCompatActivity {

    private VerticalScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_main);
        scrollView = findViewById(R.id.scrollView);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollView.scrollTo(0,0);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        //ScrollView滑动到顶部
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
            }
        });
    }
}
