package com.zlove.image.picker.support.utils;

import android.content.Context;

public class UIUtils {

    public static int spanCount(Context context, int gridExpectedSize) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        float expected = Float.valueOf(screenWidth) / Float.valueOf(gridExpectedSize);
        int spanCount = Math.round(expected);
        if (spanCount == 0) {
            spanCount = 1;
        }
        return spanCount;
    }
}
