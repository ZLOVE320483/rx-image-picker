package com.zlove.picker.core;

import com.zlove.picker.core.entity.ConfigProvider;
import com.zlove.picker.core.ui.ActivityPickerViewController;
import com.zlove.picker.core.ui.ICustomPickerConfiguration;

public class ImagePickerDisplayer {

    private ConfigProvider configProvider;

    public ImagePickerDisplayer(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    public void display() {
        configProvider.configuration.onDisplay();
        if (!configProvider.asFragment) {
            displayPickerViewAsActivity(configProvider.configuration);
        } else {
            displayPickerViewAsFragment(configProvider.configuration);
        }
    }

    private void displayPickerViewAsActivity(ICustomPickerConfiguration configuration) {
        ActivityPickerViewController activityHolder = ActivityPickerViewController.getInstance();
        activityHolder.setActivityClass(configProvider.componentClazz);
        activityHolder.display(configProvider.fragmentActivity, configProvider.viewContainerId, configuration);
    }

    private void displayPickerViewAsFragment(ICustomPickerConfiguration configuration) {
        configProvider.pickerView.display(configProvider.fragmentActivity, configProvider.viewContainerId, configuration);
    }
}
