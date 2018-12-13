package com.zlove.picker.core.entity.sources;

import android.support.annotation.IdRes;

import com.zlove.picker.core.ui.gallery.SystemGalleryPickerView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Gallery {
    Class<?> componentClazz() default SystemGalleryPickerView.class;
    boolean openAsFragment() default true;

    @IdRes
    int containerViewId() default 0;
}
