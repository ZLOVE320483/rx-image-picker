package com.zlove.picker.core;

import com.zlove.picker.core.entity.ConfigProvider;
import com.zlove.picker.core.scheduler.RxImagePickerSchedulers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class ProxyProviders implements InvocationHandler {

    private final ConfigProcessor rxImagePickerProcessor = new ConfigProcessor(new RxImagePickerSchedulers());
    private final ProxyTranslator proxyTranslator = new ProxyTranslator();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return Observable.defer(() -> {
            ConfigProvider configProvider = proxyTranslator.processMethod(method, args);
            new ImagePickerDisplayer(configProvider).display();
            Observable observable = rxImagePickerProcessor.process(configProvider);
            Class methodType = method.getReturnType();
            if (methodType == Observable.class) {
                return Observable.just(observable);
            }
            if (methodType == Single.class) {
                return Observable.just(Single.fromObservable(observable));
            }
            if (methodType == Maybe.class) {
                return Observable.just(Maybe.fromSingle(Single.fromObservable(observable)));
            }
            if (methodType == Flowable.class) {
                return Observable.just(observable.toFlowable(BackpressureStrategy.MISSING));
            }
            throw new RuntimeException(method.getName() + "needs to return one of the next reactive types: observable, single, maybe or flowable");
        }).blockingFirst();
    }
}
