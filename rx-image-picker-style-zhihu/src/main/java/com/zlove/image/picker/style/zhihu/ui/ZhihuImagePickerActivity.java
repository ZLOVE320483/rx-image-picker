package com.zlove.image.picker.style.zhihu.ui;

import android.support.v7.app.AppCompatActivity;

import com.zlove.picker.core.ui.ActivityPickerViewController;

public class ZhihuImagePickerActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PREVIEW = 23;

    public void closure() {
        ActivityPickerViewController.getInstance().endResultEmitAndReset();
        finish();
    }

}
