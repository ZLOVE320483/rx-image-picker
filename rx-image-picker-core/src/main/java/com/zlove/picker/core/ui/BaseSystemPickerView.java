package com.zlove.picker.core.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.zlove.picker.core.entity.Result;
import com.zlove.picker.core.function.Functions;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

public abstract class BaseSystemPickerView extends Fragment {

    public static final int GALLERY_REQUEST_CODE = 100;
    public static final int CAMERA_REQUEST_CODE = 101;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final PublishSubject<Boolean> attachedSubject = PublishSubject.create();
    protected PublishSubject<Result> publishSubject = PublishSubject.create();
    private final PublishSubject<Integer> canceledSubject = PublishSubject.create();

    protected final Observable<Result> getUriObservable() {

        requestPickImage();
        return publishSubject.takeUntil(canceledSubject);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachedSubject.onNext(true);
        attachedSubject.onComplete();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void requestPickImage() {
        if (!isAdded()) {
            compositeDisposable.add(attachedSubject.subscribe((aBoolean) -> startRequest()));
        } else {
            startRequest();
        }
    }

    public abstract void startRequest();

    public abstract Uri getActivityResultUri(Intent data);

    private void onImagePicked(Uri uri) {
        if (uri != null) {
            publishSubject.onNext(Functions.parseResultNoExtraData(uri));
        }
        publishSubject.onComplete();
        closure();
    }

    private void closure() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    protected boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT > +Build.VERSION_CODES.M) {
                String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
                requestPermissions(permissions, 0);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRequest();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                case CAMERA_REQUEST_CODE:
                    onImagePicked(getActivityResultUri(data));
                    break;
                default:
                    break;
            }
        } else {
            canceledSubject.onNext(requestCode);
        }
    }
}
