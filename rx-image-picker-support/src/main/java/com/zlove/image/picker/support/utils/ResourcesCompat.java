package com.zlove.image.picker.support.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.os.Build.VERSION.SDK_INT;

public class ResourcesCompat {

    @ColorInt
    @SuppressWarnings("deprecation")
    public static int getColor(@NonNull Resources res, @ColorRes int id, @Nullable Resources.Theme theme)
            throws Resources.NotFoundException {
        if (SDK_INT >= 23) {
            return res.getColor(id, theme);
        } else {
            return res.getColor(id);
        }
    }

    @Nullable
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(@NonNull Resources res, @DrawableRes int id,
                                       @Nullable Resources.Theme theme) throws Resources.NotFoundException {
        if (SDK_INT >= 21) {
            return res.getDrawable(id, theme);
        } else {
            return res.getDrawable(id);
        }
    }
}
