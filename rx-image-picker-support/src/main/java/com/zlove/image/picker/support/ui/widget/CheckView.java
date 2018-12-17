package com.zlove.image.picker.support.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CheckView extends View {

    public static final int UNCHECKED = Integer.MIN_VALUE;
    public static final int SIZE = 48; // dp
    public static final float STROKE_RADIUS = 11.5f; // dp
    public static final float STROKE_WIDTH = 3.0f; // dp
    public static final float SHADOW_WIDTH = 6.0f; // dp
    public static final float BG_RADIUS = 11.0f; // dp
    public static final float CONTENT_SIZE = 16; // dp

    public CheckView(Context context) {
        super(context);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
