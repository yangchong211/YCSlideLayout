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
- 项目地址：https://github.com/yangchong211/YCShopDetailLayout
- [apk下载链接](https://github.com/yangchong211/YCShopDetailLayout/tree/master/image)


### 02.效果展示
- ![slide.gif](https://upload-images.jianshu.io/upload_images/4432347-43e7e30096b6e322.gif?imageMogr2/auto-orient/strip)


#### 2.1 使用SlideLayout效果
- ![image](https://upload-images.jianshu.io/upload_images/4432347-cae4a780c2c0a988.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



#### 2.2 使用SlideAnimLayout带有加载动画效果
- ![image](https://upload-images.jianshu.io/upload_images/4432347-d4f966b7750d1ece.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- ![image](https://upload-images.jianshu.io/upload_images/4432347-f7a0e3647aad672e.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



### 03.如何使用
#### 3.0 如何引入到项目中
- 如下所示
```
implementation 'cn.yc:YCSlideLib:1.1.2'
```

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
    ```
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
    ```


### 06.部分代码逻辑
#### 6.1 如何实现ScrollView在最顶部或者最底部的时候，不消费事件
- 具体逻辑在dispatchTouchEvent分发事件中，当滑动到顶部或者底部的时候，则直接让父View消费事件。其他情况是自己是将事件会向上返还给View的父节点。
    ```
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                //如果滑动到了最底部，就允许继续向上滑动加载下一页，否者不允许
                //如果子节点不希望父进程拦截触摸事件，则为true。
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - downX;
                float dy = ev.getY() - downY;
                boolean allowParentTouchEvent;
                if (Math.abs(dy) > Math.abs(dx)) {
                    if (dy > 0) {
                        //位于顶部时下拉，让父View消费事件
                        allowParentTouchEvent = isTop();
                    } else {
                        //位于底部时上拉，让父View消费事件
                        allowParentTouchEvent = isBottom();
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
    ```


#### 6.2 如何实现商品页和详情页之间的滑动，如何处理上拉加载控件的动画效果
- SlideAnimLayout有三个子ChildView：一个是商品页layout，一个是上拉加载动画layout，一个是详情页layout
- 通过onInterceptTouchEvent进行事件拦截后，在onTouchEvent方法中对触摸信息做进一步处理可以实现竖直方向的滑动
    - 当商品页ScrollView滑动到底部时，则直接让父View消费事件，该父View也就是SlideAnimLayout
    - 在onInterceptTouchEvent中，当打开详情页后(也就是CLOSE状态)，向下拉动，当y轴滑动位移绝对值大于触摸移动的像素距离，并且当y轴滑动位移大于0，则拦截事件分发自己消费事件
    - 在onInterceptTouchEvent中，当关闭详情页后(也就是OPEN状态)，向上拉动，当y轴滑动位移绝对值大于触摸移动的像素距离，并且当y轴滑动位移小于0，则拦截事件分发自己消费事件
    - 当处在商品页时，向上拉动；或者处于详情页时，向下拉动，在拉动过程中去改变mSlideOffset值，并且调用requestLayout()方法去绘制
    - 在屏幕区域滑动两个面板只需要改变两个面板在y轴方向的位移（有正负方向）即可。滑动的标尺是控件相对于Top的移动，且所有的位移计算都是基于该标尺。在切换面板时只需要知道对应的offset值即可……
- 如何处理上拉加载控件的动画效果
    - 添加一个listener监听，可以监听到状态，以及是否达到一半距离，主要是和offset比较，当到达一半距离的时候，这个时候用属性动画将箭头view旋转180度即可实现。
    - 既然要监听滑动距离，则首先要获取该加载控件的高度animHeight，那么在哪里获取比较合适呢？可以在onFinishInflate()方法中，用post形式获取控件高度。
- 那么如何使滑动生效，并且看上去比较连贯
    - 自定义布局中有非常重要的两个环节onMeasure(测量)和onLayout(布局)。测量决定了View的所占的大小，布局决定了View所处的位置。实现滑动的关键思路就在这里，我们在onLayout方法中根据通过onInterceptTouchEvent、onTouchEvent得到的滑动信息进行计算而得到布局的位置信息，并把这个位置信息设置到子View上面即可实现滑动。
- 滑动后松开手指如何实现滚动效果
    - 也就是说，当处在商品页时，向上拉动，拉动位移大于一半时，松开手指，则直接滑动到下一页详情页页面
    - 具体逻辑在finishTouchEvent方法中，它主要是记录offset值，以及close或open状态下视图的高度，还有是否发生切换变化
    - 最后开启动画，在动画过程中添加动画update的监听，在该方法中去requestLayout()控件，这样就达到滚动效果了。动画滚动结束后，如果是open状态并且是第一次显示，则设置详情页控件可见。
- 如何使滚动效果比较自然，或者如何调整滚动时长
    - 可以自定义设置时间，直接在布局中设置……



### 07.参考案例
- 感谢下面大佬的开源案例
- https://github.com/jeasonlzy/VerticalSlideView
- https://github.com/hexianqiao3755/GoodsInfoPage
- https://github.com/cnbleu/SlideDetailsLayout



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












