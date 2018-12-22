package com.zlove.image.picker.support.utils;

import android.media.ExifInterface;

import java.io.IOException;

public class ExifInterfaceCompat {

    private static final String TAG = ExifInterfaceCompat.class.getSimpleName();

    public static ExifInterface newInstance(String fileName) throws IOException {
        if (fileName == null)
            throw new NullPointerException("filename should not be null");
        return new ExifInterface(fileName);
    }
}
