package com.zlove.picker.core.function;

import android.net.Uri;

import com.zlove.picker.core.entity.Result;

public class Functions {

    public static Result parseResultNoExtraData(Uri uri) {
        return new Result.Builder(uri).build();
    }
}
