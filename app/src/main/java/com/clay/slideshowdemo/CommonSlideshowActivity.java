package com.clay.slideshowdemo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class CommonSlideshowActivity extends AppCompatActivity {

    //轮播滚动图
    private ViewPager vpHomePage;       //滚动轮播图
    private LinearLayout dot_layout;     //轮播图跟着滑动的点
    private List<Integer> hpViwePager = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 1:
                    vpHomePage.setCurrentItem(vpHomePage.getCurrentItem() + 1);
                    handler.sendEmptyMessageDelayed(1, 4000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_slideshow);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        vpHomePage = (ViewPager) findViewById(R.id.vp_homePage);       //滚动图
        dot_layout = (LinearLayout) findViewById(R.id.dot_layout);
    }

    private void initData() {
        hpViwePager.add(R.drawable.beauty_a);
        hpViwePager.add(R.drawable.beauty_b);
        hpViwePager.add(R.drawable.beauty_c);
        hpViwePager.add(R.drawable.beauty_d);

        setViewPagerData();
    }

    private void initListener() {
        //轮播图改变监听
        vpHomePage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.e("Activity", "position: " + position);
                updateIntroAndDot();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 设置viewPager的数据
     */
    private void setViewPagerData() {
        initDots();
        vpHomePage.setAdapter(new MyPagerAdapter(hpViwePager, this));
        int maxHalf = Integer.MAX_VALUE / 2;
        int value = maxHalf % hpViwePager.size();
        vpHomePage.setCurrentItem(maxHalf - value);
        handler.sendEmptyMessageDelayed(1, 4000);
        updateIntroAndDot();
    }

    /**
     * 初始化view pager的点
     */
    private void initDots() {
        for (int i = 0; i < hpViwePager.size(); i++) {
            View view = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dp2Px(this, 14), Utils.dp2Px(this, 6));
            if (i != 0) {
                params.leftMargin = Utils.dp2Px(this, 5);
            }
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.selector_dot);
            dot_layout.addView(view);
        }
    }

    /**
     * 更新点
     */
    private void updateIntroAndDot() {
        int currentPage = vpHomePage.getCurrentItem() % hpViwePager.size();
        for (int i = 0; i < dot_layout.getChildCount(); i++) {
            dot_layout.getChildAt(i).setEnabled(i == currentPage);
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        private Context mContext;
        private List<Integer> hpViwePager;

        public MyPagerAdapter(List<Integer> hpViwePager, Context context) {
            mContext = context;
            this.hpViwePager = hpViwePager;
        }

        /**
         * 返回多少page
         */
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        /**
         * true: 表示不去创建，使用缓存  false:去重新创建
         * view： 当前滑动的view
         * object：将要进入的新创建的view，由instantiateItem方法创建
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 类似于BaseAdapger的getView方法
         * 用了将数据设置给view
         * 由于它最多缓存3个界面，不需要viewHolder
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView slideshowImg = new ImageView(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            slideshowImg.setScaleType(ImageView.ScaleType.FIT_XY);
            slideshowImg.setImageResource(hpViwePager.get(position % hpViwePager.size()));
            slideshowImg.setLayoutParams(layoutParams);

            container.addView(slideshowImg);//一定不能少，将view加入到viewPager中
            return slideshowImg;
        }

        /**
         * 销毁page
         * position： 当前需要消耗第几个page
         * object:当前需要消耗的page
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //			super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }
}
