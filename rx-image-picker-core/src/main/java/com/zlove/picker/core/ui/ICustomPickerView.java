package com.zlove.picker.core.ui;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;

import com.zlove.picker.core.entity.Result;

import io.reactivex.Observable;

public interface ICustomPickerView {

    void display(FragmentActivity fragmentActivity, @IdRes int viewContainer, ICustomPickerConfiguration configuration);

    Observable<Result> pickImage();

}
