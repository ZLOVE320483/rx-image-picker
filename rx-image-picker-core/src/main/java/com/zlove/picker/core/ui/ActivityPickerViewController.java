package com.zlove.picker.core.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.zlove.picker.core.entity.Result;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ActivityPickerViewController implements ICustomPickerView {

    private ActivityPickerViewController() {}

    private static ActivityPickerViewController instance;

    public static ActivityPickerViewController getInstance() {
        if (instance == null) {
            synchronized (ActivityPickerViewController.class) {
                if (instance == null) {
                    instance = new ActivityPickerViewController();
                }
            }
        }
        return instance;
    }

    private PublishSubject<Result> publishSubject = PublishSubject.create();
    private Class<? extends Activity> activityClass;

    public void setActivityClass(Class<? extends Activity> activityClass) {
        this.activityClass = activityClass;
    }

    public void resetSubject() {
        publishSubject = PublishSubject.create();
    }

    @Override
    public void display(FragmentActivity fragmentActivity, int viewContainer, ICustomPickerConfiguration configuration) {
        resetSubject();
        fragmentActivity.startActivity(new Intent(fragmentActivity, activityClass));
    }

    @Override
    public Observable<Result> pickImage() {
        return publishSubject;
    }

    public void emitResult(Result result) {
        publishSubject.onNext(result);
    }

    public void emitResults(List<Result> results) {
        for (Result result : results) {
            emitResult(result);
        }
    }

    private void emitError(Throwable e) {
        publishSubject.onError(e);
    }

    private void endResultEmitAndReset() {
        publishSubject.onComplete();
        resetSubject();
    }


}
