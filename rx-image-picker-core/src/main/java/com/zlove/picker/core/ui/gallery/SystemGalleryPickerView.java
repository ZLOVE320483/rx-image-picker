package com.zlove.picker.core.ui.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zlove.picker.core.entity.Result;
import com.zlove.picker.core.ui.BaseSystemPickerView;
import com.zlove.picker.core.ui.ICustomPickerConfiguration;
import com.zlove.picker.core.ui.IGalleryCustomPickerView;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SystemGalleryPickerView extends BaseSystemPickerView implements IGalleryCustomPickerView {

    @Override
    public void display(FragmentActivity fragmentActivity, int viewContainer, ICustomPickerConfiguration configuration) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(getTag());
        if (fragment == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (viewContainer != 0) {
                transaction.add(viewContainer, this, getTag());
            } else {
                transaction.add(this, getTag());
            }
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public Observable<Result> pickImage() {
        publishSubject = PublishSubject.create();
        return getUriObservable();
    }

    @Override
    public void startRequest() {
        if (!checkPermission()) {
            return;
        }
        Intent pictureChooseIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pictureChooseIntent = new Intent(Intent.ACTION_PICK);
            pictureChooseIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        } else {
            pictureChooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
        }
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pictureChooseIntent.setType("image/*");

        startActivityForResult(pictureChooseIntent, BaseSystemPickerView.GALLERY_REQUEST_CODE);
    }

    @Override
    public Uri getActivityResultUri(Intent data) {
        return data.getData();
    }
}
