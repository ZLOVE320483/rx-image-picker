package com.zlove.image.picker.support.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class MediaStoreCompat {

    public static boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}
