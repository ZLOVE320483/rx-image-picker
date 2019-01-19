package bytedance.com.image.picker.style.wechat;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;

import com.zlove.image.picker.support.MimeType;
import com.zlove.image.picker.support.entity.SelectionSpec;

import java.util.Set;

import bytedance.com.image.picker.style.wechat.engine.impl.WechatGlideEngine;

public class WechatConfigrationBuilder {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @IntDef(value = {ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_USER,
            ActivityInfo.SCREEN_ORIENTATION_BEHIND, ActivityInfo.SCREEN_ORIENTATION_SENSOR,
            ActivityInfo.SCREEN_ORIENTATION_NOSENSOR, ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER, ActivityInfo.SCREEN_ORIENTATION_LOCKED})
    @interface ScreenOrientation {}

    private final SelectionSpec mSelectionSpec;

    public WechatConfigrationBuilder(Set<MimeType> mimeTypes, boolean mediaTypeExclusive) {
        mSelectionSpec = SelectionSpec.getNewCleanInstance(new WechatGlideEngine());
        mSelectionSpec.mimeTypeSet = mimeTypes;
        mSelectionSpec.mediaTypeExclusive = mediaTypeExclusive;
        mSelectionSpec.orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public WechatConfigrationBuilder countable(boolean countable) {
        mSelectionSpec.countable = countable;
        return this;
    }

    public WechatConfigrationBuilder maxSelectable(int maxSelectable) {
        if (maxSelectable < 1)
            throw new IllegalArgumentException("maxSelectable must be greater than or equal to one");
        if (mSelectionSpec.maxImageSelectable > 0 || mSelectionSpec.maxVideoSelectable > 0)
            throw new IllegalStateException("already set maxImageSelectable and maxVideoSelectable");
        mSelectionSpec.maxSelectable = maxSelectable;
        return this;
    }

    public WechatConfigrationBuilder spanCount(int spanCount) {
        if (spanCount < 1)
            throw new IllegalArgumentException("spanCount cannot be less than 1");
        mSelectionSpec.spanCount = spanCount;
        return this;
    }

    public SelectionSpec build() {
        if (mSelectionSpec.themeId == R.style.Theme_AppCompat_Light)
            mSelectionSpec.themeId = R.style.Wechat;

        return mSelectionSpec;
    }
}
