package bytedance.com.image.picker.style.wechat.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zlove.image.picker.support.ui.widget.MediaGrid;

import bytedance.com.image.picker.style.wechat.R;

public class WechatMediaGrid extends MediaGrid {

    public WechatMediaGrid(@NonNull Context context) {
        super(context);
    }

    public WechatMediaGrid(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WechatMediaGrid(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.wechat_media_grid_content;
    }
}
