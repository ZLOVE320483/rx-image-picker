package bytedance.com.image.picker.style.wechat.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 99);
        } else {
            displayPickerView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayPickerView();
        } else {
            closure();
        }
    }

    private void displayPickerView() {
        WechatImagePickerFragment fragment = new WechatImagePickerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container, fragment)
                .commit();

        fragment.pickImage().subscribe(result -> ActivityPickerViewController.getInstance().emitResult(result),
                throwable -> ActivityPickerViewController.getInstance().emitError(throwable),
                () -> closure());
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
