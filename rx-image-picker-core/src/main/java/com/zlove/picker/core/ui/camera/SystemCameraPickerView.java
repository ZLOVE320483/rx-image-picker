package com.zlove.picker.core.ui.camera;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zlove.picker.core.entity.Result;
import com.zlove.picker.core.ui.BaseSystemPickerView;
import com.zlove.picker.core.ui.ICameraCustomPickerView;
import com.zlove.picker.core.ui.ICustomPickerConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SystemCameraPickerView extends BaseSystemPickerView implements ICameraCustomPickerView {

    private Uri cameraPictureUrl;

    @Override
    public void display(FragmentActivity fragmentActivity, @IdRes int viewContainer, ICustomPickerConfiguration configuration) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(getTag());
        if (fragment == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (viewContainer != 0) {
                transaction.add(viewContainer, this, getTag());
            } else {
                transaction.add(this,getTag());
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
        cameraPictureUrl = createImageUri();
        Intent pictureChooseIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pictureChooseIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl);

        startActivityForResult(pictureChooseIntent, BaseSystemPickerView.CAMERA_REQUEST_CODE);

    }

    @Override
    public Uri getActivityResultUri(Intent data) {
        return cameraPictureUrl;
    }

    private Uri createImageUri() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues contentValues = new ContentValues();
        String timeStamp = new SimpleDateFormat("", Locale.getDefault()).format(new Date());
        contentValues.put(MediaStore.Images.Media.TITLE, timeStamp);
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }
}
