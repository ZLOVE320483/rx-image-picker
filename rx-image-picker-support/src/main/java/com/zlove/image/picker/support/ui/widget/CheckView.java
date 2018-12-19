package com.zlove.image.picker.support.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.zlove.image.picker.support.R;
import com.zlove.image.picker.support.utils.ResourcesCompat;

public class CheckView extends View {

    public static final int UNCHECKED = Integer.MIN_VALUE;
    public static final int SIZE = 48; // dp
    public static final float STROKE_RADIUS = 11.5f; // dp
    public static final float STROKE_WIDTH = 3.0f; // dp
    public static final float SHADOW_WIDTH = 6.0f; // dp
    public static final float BG_RADIUS = 11.0f; // dp
    public static final float CONTENT_SIZE = 16; // dp

    protected boolean mCountable;
    protected boolean mChecked;
    protected int mCheckedNum;
    protected Paint mStrokePaint;
    protected Paint mBackgroundPaint;
    protected TextPaint mTextPaint;
    protected Paint mShadowPaint;
    protected Drawable mCheckDrawable;
    protected float mDensity;
    protected Rect mCheckRect;
    protected boolean mEnabled = true;

    public Rect getCheckRect() {
        if (mCheckRect == null) {
            int rectPadding = (int) (SIZE * mDensity / 2 - CONTENT_SIZE * mDensity / 2);
            mCheckRect = new Rect(rectPadding, rectPadding, (int) (SIZE * mDensity - rectPadding), (int) ((SIZE * mDensity - rectPadding)));
        }

        return mCheckRect;
    }

    public CheckView(Context context) {
        super(context);
        init(context);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        mDensity = context.getResources().getDisplayMetrics().density;

        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        mStrokePaint.setStrokeWidth(STROKE_WIDTH * mDensity);
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.item_checkCircle_borderColor});
        int defaultColor = ResourcesCompat.getColor(getResources(), R.color.default_item_checkCircle_borderColor, getContext().getTheme());
        int color = ta.getColor(0, defaultColor);
        ta.recycle();
        mStrokePaint.setColor(color);
        mCheckDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_check_white_18dp, context.getTheme());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeSpec = View.MeasureSpec.makeMeasureSpec((int) (SIZE * mDensity), View.MeasureSpec.EXACTLY);
        super.onMeasure(sizeSpec, sizeSpec);
    }

    public void setChecked(boolean checked) {
        if (mCountable) {
            throw new IllegalStateException("CheckView is countable, call setCheckedNum() instead.");
        }
        mChecked = checked;
        invalidate();
    }

    public void setCountable(boolean countable) {
        mCountable = countable;
    }

    public void setCheckedNum(int checkedNum) {
        if (!mCountable) {
            throw new IllegalStateException("CheckView is not countable, call setChecked() instead.");
        }
        if (checkedNum != UNCHECKED && checkedNum <= 0) {
            throw new IllegalArgumentException("checked num can't be negative.");
        }
        mCheckedNum = checkedNum;
        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mEnabled != enabled) {
            mEnabled = enabled;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw outer and inner shadow
        initShadowPaint();
        canvas.drawCircle(SIZE * mDensity / 2, SIZE * mDensity / 2,
                (STROKE_RADIUS + STROKE_WIDTH / 2 + SHADOW_WIDTH) * mDensity, mShadowPaint);

        // draw white stroke
        canvas.drawCircle(SIZE * mDensity / 2, SIZE * mDensity / 2,
                STROKE_RADIUS * mDensity, mStrokePaint);

        // draw content
        if (mCountable) {
            if (mCheckedNum != UNCHECKED) {
                initBackgroundPaint();
                canvas.drawCircle(SIZE * mDensity / 2, SIZE * mDensity / 2,
                        BG_RADIUS * mDensity, mBackgroundPaint);
                initTextPaint();
                String text = String.valueOf(mCheckedNum);
                float baseX = (canvas.getWidth() - mTextPaint.measureText(text)) / 2;
                float baseY = (canvas.getHeight() - mTextPaint.descent() - mTextPaint.ascent()) / 2;
                canvas.drawText(text, baseX, baseY, mTextPaint);
            }
        } else {
            if (mChecked) {
                initBackgroundPaint();
                canvas.drawCircle(SIZE * mDensity / 2, SIZE * mDensity / 2,
                        BG_RADIUS * mDensity, mBackgroundPaint);

                mCheckDrawable.setBounds(getCheckRect());
                mCheckDrawable.draw(canvas);
            }
        }

        // enable hint
        if (mEnabled)
            setAlpha(1.0f);
        else
            setAlpha(0.5f);
    }


    protected void initShadowPaint() {
        if (mShadowPaint == null) {
            mShadowPaint = new Paint();
            mShadowPaint.setAntiAlias(true);
            // all in dp
            float outerRadius = STROKE_RADIUS + STROKE_WIDTH / 2;
            float innerRadius = outerRadius - STROKE_WIDTH;
            float gradientRadius = outerRadius + SHADOW_WIDTH;
            float stop0 = (innerRadius - SHADOW_WIDTH) / gradientRadius;
            float stop1 = innerRadius / gradientRadius;
            float stop2 = outerRadius / gradientRadius;
            float stop3 = 1.0f;
            mShadowPaint.setShader(new RadialGradient(SIZE * mDensity / 2,
                    SIZE * mDensity / 2,
                    gradientRadius * mDensity,
                    new int[]{Color.parseColor("#00000000"), Color.parseColor("#0D000000"), Color.parseColor("#0D000000"), Color.parseColor("#00000000")},
                    new float[]{stop0, stop1, stop2, stop3},
                    Shader.TileMode.CLAMP));
        }
    }

    protected void initBackgroundPaint() {
        if (mBackgroundPaint == null) {
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setAntiAlias(true);
            mBackgroundPaint.setStyle(Paint.Style.FILL);
            TypedArray ta = getContext().getTheme()
                    .obtainStyledAttributes(new int[]{R.attr.item_checkCircle_backgroundColor});
            int defaultColor = ResourcesCompat.getColor(
                    getResources(), R.color.default_item_checkCircle_backgroundColor,
                    getContext().getTheme());
            int color = ta.getColor(0, defaultColor);
            ta.recycle();
            mBackgroundPaint.setColor(color);
        }
    }

    protected void initTextPaint() {
        if (mTextPaint == null) {
            mTextPaint = new TextPaint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setColor(Color.WHITE);
            mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            mTextPaint.setTextSize(12.0f * mDensity);
        }
    }
}
