package com.zlove.picker.core;

import com.zlove.picker.core.ui.SystemImagePicker;

import java.lang.reflect.Proxy;

public class RxImagePicker {

    public static SystemImagePicker create() {
        return create(SystemImagePicker.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> classProviders) {
        ProxyProviders proxyProviders = new ProxyProviders();
        return (T) Proxy.newProxyInstance(classProviders.getClassLoader(), new Class[]{proxyProviders.getClass()}, proxyProviders);
    }
}
