package bytedance.com.image.picker.style.wechat.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zlove.image.picker.support.ui.widget.CheckView;

public class WechatCheckView extends CheckView {

    private static final float WECHAT_STROKE_WIDTH = 1.5f; // dp
    private static final float WECHAT_STROKE_CONNER = 5f; // dp
    private static final int SIZE = 48; // dp

    private static final int PROOFREAD_SIZE = 8;

    private RectF rect;
    private Rect drawableRect;

    public WechatCheckView(Context context) {
        super(context);
    }

    public WechatCheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WechatCheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mStrokePaint.setStrokeWidth(WECHAT_STROKE_WIDTH * mDensity);

        // draw white stroke
        float dx = mDensity * SIZE / 4;
        float dy = mDensity * SIZE / 4;
        rect = new RectF(2 * dx - PROOFREAD_SIZE, dy - PROOFREAD_SIZE,
                3 * dx + PROOFREAD_SIZE, 2 * dy + PROOFREAD_SIZE);
        drawableRect = new Rect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw outer and inner shadow
        initShadowPaint();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawRoundRectCheckView(canvas, rect, mStrokePaint);
        } else {
            drawRectCheckView(canvas, rect, mStrokePaint);
        }

        // draw content
        if (mCountable) {
            if (mCheckedNum != CheckView.UNCHECKED) {
                initBackgroundPaint();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawRoundRectCheckView(canvas, rect, mBackgroundPaint);
                } else {
                    drawRectCheckView(canvas, rect, mBackgroundPaint);
                }
                initTextPaint();
                String text = String.valueOf(mCheckedNum);
                float baseX = (rect.width() - mTextPaint.measureText(text)) / 2;
                float baseY = (rect.height() - mTextPaint.descent() - mTextPaint.ascent()) / 2;
                canvas.drawText(text, baseX + rect.left, baseY + rect.top, mTextPaint);
            }
        } else {
            if (mChecked) {
                initBackgroundPaint();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawRoundRectCheckView(canvas, rect, mBackgroundPaint);
                } else {
                    drawRectCheckView(canvas, rect, mBackgroundPaint);
                }
                mCheckDrawable.setBounds(drawableRect);
                mCheckDrawable.draw(canvas);
            }
        }

        // enable hint
        if (mEnabled) {
            setAlpha(1.0f);
        } else {
            setAlpha(0.5f);
        }
    }

    private void drawRectCheckView(Canvas canvas, RectF rect, Paint paint) {
        canvas.drawRect(rect, paint);
    }

    @TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
    private void drawRoundRectCheckView(Canvas canvas, RectF rect, Paint paint) {
        canvas.drawRoundRect(rect, WECHAT_STROKE_CONNER, WECHAT_STROKE_CONNER, paint);
    }
}
