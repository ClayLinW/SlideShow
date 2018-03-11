package com.clay.slideshowdemo;

import android.content.Context;

/**
 * Created by clay on 2018/3/10.
 */

public class Utils {

    /**
     * dp转换成像素
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2Px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dp * density + 0.5f);
        return px;
    }
}
