package com.zlove.picker.core;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.zlove.picker.core.entity.ConfigProvider;
import com.zlove.picker.core.entity.sources.Camera;
import com.zlove.picker.core.entity.sources.Gallery;
import com.zlove.picker.core.entity.sources.SourcesFrom;
import com.zlove.picker.core.ui.ActivityPickerViewController;
import com.zlove.picker.core.ui.ICustomPickerConfiguration;
import com.zlove.picker.core.ui.ICustomPickerView;

import java.lang.reflect.Method;

public class ProxyTranslator {

    public ConfigProvider processMethod(Method method, Object[] args) {
        SourcesFrom sourcesFrom = streamSourcesFrom(method);
        boolean asFragment = asFragment(method, sourcesFrom);
        Class componentClass = getComponentClass(method, sourcesFrom);

        Context context = getObjectFromMethodParam(method, Context.class, args);
        if (context == null) {
            throw new NullPointerException(method.getName() + " requires just one instance of type: Context, but none.");
        }
        FragmentActivity fragmentActivity = transformContextToFragmentActivity(context);
        ICustomPickerConfiguration runtimeConfiguration = getObjectFromMethodParam(method, ICustomPickerConfiguration.class, args);

        ICustomPickerView pickerView = null;
        try {
            if (asFragment && ICustomPickerView.class.isAssignableFrom(componentClass)) {
                pickerView = (ICustomPickerView) componentClass.newInstance();
            } else if (!asFragment && Activity.class.isAssignableFrom(componentClass)) {
                pickerView = ActivityPickerViewController.getInstance();
            } else {
                throw new IllegalArgumentException("Configration Conflict! The ui component as Activity: " + !asFragment +
                        ", the Class type is: " + componentClass.getSimpleName());
            }
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e2) {
            e2.printStackTrace();
        }
        return new ConfigProvider(componentClass, asFragment, sourcesFrom,
                asFragment ? containerViewId(method, sourcesFrom) : -1,
                fragmentActivity,
                pickerView,
                runtimeConfiguration);
    }

    private boolean asFragment(Method method, SourcesFrom sourcesFrom) {
        switch (sourcesFrom) {
            case CAMERA:
                return method.getAnnotation(Camera.class).openAsFragment();
            case GALLERY:
                return method.getAnnotation(Gallery.class).openAsFragment();
        }
        return false;
    }

    private Class getComponentClass(Method method, SourcesFrom sourcesFrom) {
        switch (sourcesFrom) {
            case CAMERA:
                return method.getAnnotation(Camera.class).componentClazz();
            case GALLERY:
                return method.getAnnotation(Gallery.class).componentClazz();
        }
        return method.getAnnotation(Gallery.class).componentClazz();
    }

    private SourcesFrom streamSourcesFrom(Method method) {
        boolean camera = method.getAnnotation(Camera.class) != null;
        boolean gallery = method.getAnnotation(Gallery.class) != null;
        if (camera && !gallery) {
            return SourcesFrom.CAMERA;
        } else if (gallery && !camera) {
            return SourcesFrom.GALLERY;
        } else if (!camera) {
            throw new IllegalArgumentException("Did you forget to add the @Galley or the @Camera annotation?");
        } else {
            throw new IllegalArgumentException("You should not add two conflicting annotation to this method: @Galley and @Camera.");
        }
    }

    private int containerViewId(Method method, SourcesFrom sourcesFrom) {
        switch (sourcesFrom) {
            case CAMERA:
                return method.getAnnotation(Camera.class).containerViewId();
            case GALLERY:
                return method.getAnnotation(Gallery.class).containerViewId();
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    private <T> T getObjectFromMethodParam(Method method, Class<T> expectedClass, Object[] objectsMethod) {
        int countSameObjectsType = 0;
        T expectedObject = null;
        if (objectsMethod == null) {
            throw new NullPointerException(method.getName() + " requires the Context as argument at least.");
        }

        for (Object objectParam : objectsMethod) {
            if (expectedClass.isAssignableFrom(objectParam.getClass())) {
                expectedObject = (T) objectParam;
                countSameObjectsType++;
            }
        }
        if (countSameObjectsType > 1) {
            throw new IllegalArgumentException(method.getName() + " requires just one instance of type: "
            + expectedClass.getSimpleName() + ", but " + countSameObjectsType);
        }
        return expectedObject;
    }

    private FragmentActivity transformContextToFragmentActivity(Context context) {
        if (context instanceof FragmentActivity) {
            return (FragmentActivity) context;
        } else {
            throw new IllegalArgumentException("the context should be FragmentActivity.");
        }
    }
}
