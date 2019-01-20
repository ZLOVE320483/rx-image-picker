package com.zlove.image.picker.system;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.zlove.image.picker.R;
import com.zlove.image.picker.imageloader.GlideApp;
import com.zlove.picker.core.RxImagePicker;
import com.zlove.picker.core.ui.SystemImagePicker;

import io.reactivex.disposables.Disposable;

public class SystemActivity extends AppCompatActivity {

    private SystemImagePicker defaultImagePicker;
    private ImageView ivPickedImage;
    private Disposable cameraDisposable;
    private Disposable galleryDisposable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        ivPickedImage = findViewById(R.id.imageView);
        initRxImagePicker();

        findViewById(R.id.fabPickCamera).setOnClickListener(v -> pickCamera());

        findViewById(R.id.fabGallery).setOnClickListener(v -> pickGallery());
    }


    private void initRxImagePicker() {
        defaultImagePicker = RxImagePicker.create();
    }

    private void pickGallery() {
        galleryDisposable = defaultImagePicker.openGallery(this)
                .subscribe(result -> onPickUriSuccess(result.uri));
    }

    private void pickCamera() {
        cameraDisposable = defaultImagePicker.openCamera(this)
                .subscribe((result) -> onPickUriSuccess(result.uri));
    }


    private void onPickUriSuccess(Uri uri) {
        GlideApp.with(this)
                .load(uri)
                .into(ivPickedImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraDisposable != null && !cameraDisposable.isDisposed()) {
            cameraDisposable.dispose();
        }
        if (galleryDisposable != null && !galleryDisposable.isDisposed()) {
            galleryDisposable.dispose();
        }
    }
}
