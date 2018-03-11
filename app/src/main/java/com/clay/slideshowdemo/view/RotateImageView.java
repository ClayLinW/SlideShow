package com.clay.slideshowdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by admin on 2017/9/15.
 */

public class RotateImageView extends ImageView {

    private Matrix mMatrix; //作用矩阵
    private Camera mCamera;

    private float mDegree; //翻转角度
    private Bitmap mShowBmp;
    private Bitmap mNewBmp;
    private int centerX, centerY; //图片中心点

    public RotateImageView(Context context) {
        super(context);
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData();
        mMatrix = new Matrix();
        mCamera = new Camera();
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initData() {
        mShowBmp = ((BitmapDrawable) this.getDrawable()).getBitmap();
        mMatrix = new Matrix();
        mCamera = new Camera();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (mNewBmp == null) {
            mNewBmp = createBmp(mShowBmp, width, height);
            centerX = mNewBmp.getWidth() / 2;
            centerY = mNewBmp.getHeight() / 2;
        }
        mCamera.save();
        //绕Y轴翻转
        mCamera.rotateY(mDegree);
        //设置camera作用矩阵
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        //设置翻转中心点
        mMatrix.preTranslate(-this.centerX, -this.centerY);
        mMatrix.postTranslate(this.centerX, this.centerY);
        Paint paint = new Paint();
        // 透明度
        paint.setAlpha((int) (255 - Math.abs(mDegree) * 6));
        // 灰色
        ColorMatrix colorMatrix = new ColorMatrix();
        float grey = 1f - Math.abs((float) mDegree / 15f);
        if (grey < 0) {
            grey = 0;
        }
        colorMatrix.setSaturation(grey);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(mNewBmp, mMatrix, paint);
    }

    public Bitmap createBmp(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBmp = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newBmp;
    }

    /**
     * 设置旋转角度
     *
     * @param degree
     */
    public void setDegree(float degree) {
        mDegree = degree;
        invalidate();
    }

}
