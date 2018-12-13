package com.zlove.picker.core.scheduler;

import io.reactivex.Scheduler;

public interface IRxImagePickerSchedulers {

    Scheduler ui();

    Scheduler io();
}
