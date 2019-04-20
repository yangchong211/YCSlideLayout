# 商品详情页上拉查看详情
#### 目录介绍
- 01.该库介绍
- 02.效果展示
- 03.如何使用
- 04.注意要点
- 05.优化问题
- 06.部分代码逻辑
- 07.参考案例


### 01.该库介绍
- 模仿淘宝、京东、考拉等商品详情页分页加载的UI效果。可以嵌套RecyclerView、WebView、ViewPager、ScrollView等等。



### 02.效果展示
- ![image](https://github.com/yangchong211/YCShopDetailLayout/blob/master/image/slide.gif)

#### 2.1 使用SlideLayout效果
- ![image](https://github.com/yangchong211/YCShopDetailLayout/blob/master/image/1.jpg)



#### 2.2 使用SlideAnimLayout带有加载动画效果
- ![image](https://github.com/yangchong211/YCShopDetailLayout/blob/master/image/2.jpg)
- ![image](https://github.com/yangchong211/YCShopDetailLayout/blob/master/image/3.jpg)



### 03.如何使用
- 直接引用：implementation 'cn.yc:YCSlideLib:1.0.4'


#### 3.1 第一种，直接上拉加载分页【SlideLayout有两个子ChildView】
- SlideDetailsLayout有两个子ChildView：一个是商品页layout，一个是详情页layout
- 在布局中
    ```
    <com.ycbjie.slide.SlideLayout
        android:id="@+id/slideDetailsLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:default_panel="front"
        app:duration="200"
        app:percent="0.1">

        <!--商品布局-->
        <FrameLayout
            android:id="@+id/fl_shop_main"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>

        <!--分页详情webView布局-->
        <include layout="@layout/include_shop_detail"/>


    </com.ycbjie.slide.SlideLayout>
    ```
- 在代码中
    ```
    mSlideDetailsLayout.setOnSlideDetailsListener(new SlideLayout.OnSlideDetailsListener() {
        @Override
        public void onStatusChanged(SlideLayout.Status status) {
            if (status == SlideLayout.Status.OPEN) {
                //当前为图文详情页
                Log.e("FirstActivity","下拉回到商品详情");
            } else {
                //当前为商品详情页
                Log.e("FirstActivity","继续上拉，查看图文详情");
            }
        }
    });

    //关闭商详页
    mSlideDetailsLayout.smoothClose(true);
    //打开详情页
    mSlideDetailsLayout.smoothOpen(true);
    ```


#### 3.2 第一种，上拉加载有动画效果，然后展示分页【SlideAnimLayout有三个子ChildView】
- SlideAnimLayout有三个子ChildView：一个是商品页layout，一个是上拉加载动画layout，一个是详情页layout
- 在布局中
    ```
       <com.ycbjie.slide.SlideAnimLayout
            android:id="@+id/slideDetailsLayout"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            app:default_panel="front"
            app:duration="200"
            app:percent="0.1">

            <!--商品布局-->
            <FrameLayout
                android:id="@+id/fl_shop_main2"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>

            <!--上拉加载动画布局-->
            <LinearLayout
                android:id="@+id/ll_page_more"
                android:orientation="vertical"
                android:background="@color/colorAccent"
                android:layout_width="match_parent"
                android:layout_height="wrap_content">
                <ImageView
                    android:id="@+id/iv_more_img"
                    android:layout_width="40dp"
                    android:layout_height="40dp"
                    android:rotation="180"
                    android:layout_gravity="center_horizontal"
                    android:src="@mipmap/icon_details_page_down_loading" />
                <TextView
                    android:id="@+id/tv_more_text"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_horizontal"
                    android:layout_marginBottom="25dp"
                    android:gravity="center"
                    android:text="测试动画，继续上拉，查看图文详情"
                    android:textSize="13sp" />
            </LinearLayout>

            <!--分页详情webView布局-->
            <include layout="@layout/include_shop_detail"/>


        </com.ycbjie.slide.SlideAnimLayout>
    ```
- 在代码中
    ```
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

    //关闭商详页
    mSlideDetailsLayout.smoothClose(true);
    //打开详情页
    mSlideDetailsLayout.smoothOpen(true);
    ```



### 04.注意要点
- 针对SlideDetailsLayout仅获取子节点中的前两个View
    - 其中第一个作为Front，即商品页；第二个作为Behind，即图文详情WebView页面。具体看代码：
    ```
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (1 >= childCount) {
            throw new RuntimeException("SlideDetailsLayout only accept child more than 1!!");
        }
        mFrontView = getChildAt(0);
        mBehindView = getChildAt(1);
        if(mDefaultPanel == 1){
            post(new Runnable() {
                @Override
                public void run() {
                    //默认是关闭状态的
                    smoothOpen(false);
                }
            });
        }
    }
    ```
- 针对SlideAnimLayout仅获取子节点中三个View，且第二个为动画节点View
    - 其中第一个作为Front，即商品页；第二个作为anim，即上拉动画view。第三个作为Behind，即图文详情WebView页面。具体看代码：
    ```
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
                    //默认是关闭状态的
                    smoothOpen(false);
                }
            });
        }
    }
    ```



### 05.优化问题
- 异常情况保存状态
    ```
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
    ```
- 当页面销毁的时候，移除listener监听，移除动画资源



### 06.部分代码逻辑




### 07.参考案例
- https://github.com/jeasonlzy/VerticalSlideView
- https://github.com/hexianqiao3755/GoodsInfoPage



### 08.其他更多
#### 01.关于博客汇总链接
- 1.[技术博客汇总](https://www.jianshu.com/p/614cb839182c)
- 2.[开源项目汇总](https://blog.csdn.net/m0_37700275/article/details/80863574)
- 3.[生活博客汇总](https://blog.csdn.net/m0_37700275/article/details/79832978)
- 4.[喜马拉雅音频汇总](https://www.jianshu.com/p/f665de16d1eb)
- 5.[其他汇总](https://www.jianshu.com/p/53017c3fc75d)



#### 02.关于我的博客
- 我的个人站点：www.yczbj.org，www.ycbjie.cn
- github：https://github.com/yangchong211
- 知乎：https://www.zhihu.com/people/yczbj/activities
- 简书：http://www.jianshu.com/u/b7b2c6ed9284
- csdn：http://my.csdn.net/m0_37700275
- 喜马拉雅听书：http://www.ximalaya.com/zhubo/71989305/
- 开源中国：https://my.oschina.net/zbj1618/blog
- 泡在网上的日子：http://www.jcodecraeer.com/member/content_list.php?channelid=1
- 邮箱：yangchong211@163.com
- 阿里云博客：https://yq.aliyun.com/users/article?spm=5176.100- 239.headeruserinfo.3.dT4bcV
- segmentfault头条：https://segmentfault.com/u/xiangjianyu/articles
- 掘金：https://juejin.im/user/5939433efe88c2006afa0c6e














