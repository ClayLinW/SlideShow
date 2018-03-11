package com.clay.slideshowdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clay.slideshowdemo.view.CustomHorizontalScrollView;
import com.clay.slideshowdemo.view.RotateImageView;

import java.util.HashMap;
import java.util.Map;

public class PictureSlideshowActivity extends AppCompatActivity implements View.OnClickListener {

    RotateImageView[] arrImgs = new RotateImageView[8];
    private CustomHorizontalScrollView chsvScroll;
    private LinearLayout llShifting;
    private LinearLayout dotLayout;         //轮播图跟着滑动的点
    private int mLocation = 0;               //位置居中的条目
    private boolean mPositiveCycle = true;   //循环的方向
    private int mScreenWidth;                // 获取屏幕宽度
    private static final int ROTATE_ANGLE = 15;     //角度
    private int mScreenWidthHalf = 0;
    Map<Integer, Integer> mLeftMap = new HashMap<>();  //保存每条条目中心距离HorizontalScrollView的最左边的距离

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (mLocation == 0) {
                    mPositiveCycle = true;
                } else if (mLocation == arrImgs.length - 1) {
                    mPositiveCycle = false;
                }
                if (mPositiveCycle) {
                    setCurrentCenter(mLocation + 1);
                } else {
                    setCurrentCenter(mLocation - 1);
                }
                mHandler.sendEmptyMessageDelayed(1, 6000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_slideshow);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        chsvScroll = (CustomHorizontalScrollView) findViewById(R.id.hs_scroll);
        llShifting = (LinearLayout) findViewById(R.id.ll_shifting);
        dotLayout = (LinearLayout) findViewById(R.id.dot_layout);

        arrImgs[0] = (RotateImageView) findViewById(R.id.riv_first);
        arrImgs[1] = (RotateImageView) findViewById(R.id.riv_second);
        arrImgs[2] = (RotateImageView) findViewById(R.id.riv_third);
        arrImgs[3] = (RotateImageView) findViewById(R.id.riv_fourth);
        arrImgs[4] = (RotateImageView) findViewById(R.id.riv_fifth);
        arrImgs[5] = (RotateImageView) findViewById(R.id.riv_sixth);
        arrImgs[6] = (RotateImageView) findViewById(R.id.riv_seventh);
        arrImgs[7] = (RotateImageView) findViewById(R.id.riv_eighth);
    }

    private void initData() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenWidthHalf = mScreenWidth / 2;
        // 设置每个条目的宽为屏幕的一半
        for (int i = 0; i < arrImgs.length; i++) {
            ViewGroup.LayoutParams layoutParams = arrImgs[i].getLayoutParams();
            layoutParams.width = mScreenWidthHalf;
            arrImgs[i].setLayoutParams(layoutParams);
        }
        //设置llShifting的leftPadding和rightPadding值为屏幕宽度的1/4，使RotateImageView居中显示
        int shifting = mScreenWidth / 4;
        llShifting.setPadding(shifting, 0, shifting, 0);
        //记录每个RotateImageView的中心到其父view的左边的距离，是为了以后滑动时计算每个RotateImageView的旋转角度
        for (int i = 0; i < arrImgs.length; i++) {
            int left = arrImgs[i].getLeft();
            //因为view在oncreate中还没绘制，所以无法获取arrImgs[i].getLeft的值，
            //可通过View.Post(Runnable)等到绘制完成后获取arrImgs[i].getLeft
            //但在此项目中，arrImgs[i].getLeft是可预知的，因为已设置了每个arrImgs[i]的宽度和其在屏幕中的显示位置，
            mLeftMap.put(i, mScreenWidthHalf * (i + 1));
        }
        //初始化条目所在的位置点
        initDots();
        //设置每个RotateImageView的旋转角度
        setRotateAngle(0);
        //设置第一个RotateImageView居中
        setCurrentCenter(0);
    }

    private void initListener() {
        arrImgs[0].setOnClickListener(this);
        arrImgs[1].setOnClickListener(this);
        arrImgs[2].setOnClickListener(this);
        arrImgs[3].setOnClickListener(this);
        arrImgs[4].setOnClickListener(this);
        arrImgs[5].setOnClickListener(this);
        arrImgs[6].setOnClickListener(this);
        arrImgs[7].setOnClickListener(this);

        // 滚动状态监听
        chsvScroll.setScrollStatusListener(new CustomHorizontalScrollView.ScrollStatusListener() {
            @Override
            public void onScrollChanged(CustomHorizontalScrollView.ScrollStatus scrollStatus) {
                //手势滚动时不要进行自动轮播
                mHandler.removeMessages(1);
                //滚动停止,然后计算每个arrImgs[i]的中心到屏幕的mScreenWidthHalf的距离，对比得到最小值，从而求出滑动停止后需要居中不旋转角度的arrImgs[mLocation]
                if (scrollStatus == CustomHorizontalScrollView.ScrollStatus.IDLE) {
                    int scrollX = chsvScroll.getScrollX();
                    int position = 0;
                    //计算屏幕中心距离CustomHorizontalScrollView左边的距离
                    int stopCenterScroll = mScreenWidthHalf + scrollX;
                    int distanceCenterMin = Math.abs(mLeftMap.get(0) - stopCenterScroll);
                    for (int i = 1; i < arrImgs.length; i++) {
                        int left = mLeftMap.get(i);
                        int value = Math.abs(stopCenterScroll - left);
                        if (value < distanceCenterMin) {
                            position = i;
                            distanceCenterMin = value;
                        }
                    }
                    setCurrentCenter(position);
                    //滚动停止后继续进行自动轮播
                    mHandler.sendEmptyMessageDelayed(1, 6000);
                }
            }
        });

        //滚动监听
        chsvScroll.setScrollViewListener(new CustomHorizontalScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(View chsv, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                setRotateAngle(scrollX);
            }
        });
    }

    /**
     * 初始化跟条目相对应的点
     */
    private void initDots() {
        for (int i = 0; i < arrImgs.length; i++) {
            View view = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dp2Px(this, 14), Utils.dp2Px(this, 6));
            if (i != 0) {
                params.leftMargin = Utils.dp2Px(this, 5);
            }
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.selector_dot);
            dotLayout.addView(view);
        }
        mHandler.sendEmptyMessageDelayed(1, 6000);
    }

    /**
     * 更新点
     */
    private void updateIntroAndDot(int position) {
        for (int i = 0; i < dotLayout.getChildCount(); i++) {
            dotLayout.getChildAt(i).setEnabled(i == position);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.riv_first:
                setCurrentCenter(0);
                Toast.makeText(this, "position : 0", Toast.LENGTH_SHORT).show();
                break;
            case R.id.riv_second:
                setCurrentCenter(1);
                Toast.makeText(this, "position : 1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.riv_third:
                setCurrentCenter(2);
                Toast.makeText(this, "position : 2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.riv_fourth:
                setCurrentCenter(3);
                Toast.makeText(this, "position : 3", Toast.LENGTH_SHORT).show();
                break;
            case R.id.riv_fifth:
                setCurrentCenter(4);
                Toast.makeText(this, "position : 4", Toast.LENGTH_SHORT).show();
                break;
            case R.id.riv_sixth:
                setCurrentCenter(5);
                Toast.makeText(this, "position : 5", Toast.LENGTH_SHORT).show();
                break;
            case R.id.riv_seventh:
                setCurrentCenter(6);
                Toast.makeText(this, "position : 6", Toast.LENGTH_SHORT).show();
                break;
            case R.id.riv_eighth:
                setCurrentCenter(7);
                Toast.makeText(this, "position : 7", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 设置每个RotateImageView的旋转角度
     *
     * @param scrollX
     */
    public void setRotateAngle(int scrollX) {
        for (int i = 0; i < arrImgs.length; i++) {
            //计算旋转角度 = (HorizontalScrollView的x轴方向偏移量 + 屏幕宽度的一半 - RotateImageView的中心距父view的最左边距离) * 旋转角度 / 屏幕宽度的一半
            int degree = (scrollX + mScreenWidthHalf - mLeftMap.get(i)) * ROTATE_ANGLE / mScreenWidthHalf;
            arrImgs[i].setDegree(degree);
        }
    }

    /**
     * 设置滚动停止后需要居中的position以及更新点
     *
     * @param position
     */
    public void setCurrentCenter(int position) {
        //记录当前条目
        mLocation = position;
        //计算控件居正中时距离左侧屏幕的距离
        int middleLeftPosition = (mScreenWidth - arrImgs[position].getWidth()) / 2;
        //需要显示在正中间位置的position需要向左偏移的距离
        int left = arrImgs[position].getLeft();
        int offset = left - middleLeftPosition;
        //让水平的滚动视图按照执行的x的偏移量进行移动
        chsvScroll.smoothScrollTo(offset, 0);
        //更新点
        updateIntroAndDot(position);
    }
}