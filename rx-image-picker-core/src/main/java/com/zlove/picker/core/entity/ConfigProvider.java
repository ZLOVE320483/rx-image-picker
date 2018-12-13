package com.zlove.picker.core.entity;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;

import com.zlove.picker.core.entity.sources.SourcesFrom;
import com.zlove.picker.core.ui.ICustomPickerConfiguration;
import com.zlove.picker.core.ui.ICustomPickerView;

public class ConfigProvider {

    public Class componentClazz;
    public boolean asFragment;
    public SourcesFrom sourcesFrom;

    @IdRes
    public int viewContainerId;

    public FragmentActivity fragmentActivity;
    public ICustomPickerView pickerView;
    public ICustomPickerConfiguration configuration;

    public ConfigProvider(Class componentClazz, boolean asFragment,
                          SourcesFrom sourcesFrom, int viewContainerId,
                          FragmentActivity fragmentActivity, ICustomPickerView pickerView,
                          ICustomPickerConfiguration configuration) {
        this.componentClazz = componentClazz;
        this.asFragment = asFragment;
        this.sourcesFrom = sourcesFrom;
        this.viewContainerId = viewContainerId;
        this.fragmentActivity = fragmentActivity;
        this.pickerView = pickerView;
        this.configuration = configuration;
    }
}
