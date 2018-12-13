package com.zlove.picker.core;

import com.zlove.picker.core.entity.ConfigProvider;
import com.zlove.picker.core.scheduler.IRxImagePickerSchedulers;
import com.zlove.picker.core.ui.ActivityPickerViewController;

import io.reactivex.Observable;

public class ConfigProcessor {

    private final IRxImagePickerSchedulers schedulers;

    public ConfigProcessor(IRxImagePickerSchedulers schedulers) {
        this.schedulers = schedulers;
    }

    public Observable<?> process(ConfigProvider  configProvider) {
        return Observable.just(0)
                .flatMap(integer -> {
                    if (!configProvider.asFragment) {
                        return ActivityPickerViewController.getInstance().pickImage();
                    }
                    switch (configProvider.sourcesFrom) {
                        case CAMERA:
                        case GALLERY:
                            return configProvider.pickerView.pickImage();
                    }
                    return null;
                })
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui());
    }

}
