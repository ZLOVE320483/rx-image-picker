package bytedance.com.image.picker.style.wechat.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zlove.image.picker.support.entity.SelectionSpec;
import com.zlove.picker.core.ui.ActivityPickerViewController;

import bytedance.com.image.picker.style.wechat.R;

public class WechatImagePickerActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PREVIEW = 23;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(SelectionSpec.getInstance().themeId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker_wechat);
        requestPermissionAndDisplayGallery();
    }

    private void requestPermissionAndDisplayGallery() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void displayPickerView() {

    }

    public void closure() {
        ActivityPickerViewController.getInstance().endResultEmitAndReset();
        finish();
    }

    @Override
    public void onBackPressed() {
        closure();
    }
}
