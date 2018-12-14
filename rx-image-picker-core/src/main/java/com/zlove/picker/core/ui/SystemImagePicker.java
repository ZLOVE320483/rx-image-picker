package com.zlove.picker.core.ui;

import android.content.Context;

import com.zlove.picker.core.entity.Result;
import com.zlove.picker.core.entity.sources.Camera;
import com.zlove.picker.core.entity.sources.Gallery;

import io.reactivex.Observable;

public interface SystemImagePicker {

    @Gallery
    Observable<Result> openGallery(Context context);

    @Camera
    Observable<Result> openCamera(Context context);
}
