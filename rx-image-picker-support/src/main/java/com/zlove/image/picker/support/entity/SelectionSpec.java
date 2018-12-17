package com.zlove.image.picker.support.entity;

import android.content.pm.ActivityInfo;
import android.support.annotation.StyleRes;

import com.zlove.image.picker.support.MimeType;
import com.zlove.image.picker.support.R;
import com.zlove.image.picker.support.engine.ImageEngine;
import com.zlove.image.picker.support.filter.Filter;
import com.zlove.picker.core.ui.ICustomPickerConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SelectionSpec implements ICustomPickerConfiguration {

    public Set<MimeType> mimeTypeSet = new HashSet<>();
    public boolean mediaTypeExclusive;
    public boolean showSingleMediaType;

    @StyleRes
    public int themeId;

    public int orientation;
    public boolean countable;
    public int maxSelectable;
    public int maxImageSelectable;
    public int maxVideoSelectable;
    public ArrayList<Filter> filters;
    public boolean capture;
    public CaptureStrategy captureStrategy;
    public int spanCount;
    public int gridExpectedSize;
    public float thumbnailScale;
    public ImageEngine imageEngine;

    private SelectionSpec() {
        reset();
    }

    private void reset() {
        if (InstanceHolder.imageEngineHolder == null) {
            throw new NullPointerException(
                    "the default imageEngine can't be null, please init it by the SelectionSpec.getNewCleanInstance(imageEngine)");
        }
        this.imageEngine = InstanceHolder.imageEngineHolder;
                mimeTypeSet = MimeType.ofImage();
        mediaTypeExclusive = true;
        showSingleMediaType = false;
        themeId = R.style.Theme_AppCompat_Light;
        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        countable = false;
        maxSelectable = 1;
        maxImageSelectable = 0;
        maxVideoSelectable = 0;
        filters = null;
        capture = false;
        captureStrategy = null;
        spanCount = 3;
        gridExpectedSize = 0;
        thumbnailScale = 0.5f;
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && (maxSelectable == 1 || maxImageSelectable == 1 && maxVideoSelectable == 1);
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public boolean onlyShowImages() {
        return showSingleMediaType && MimeType.ofImage().containsAll(mimeTypeSet);
    }

    public boolean onlyShowVideos() {
        return showSingleMediaType && MimeType.ofVideo().containsAll(mimeTypeSet);
    }

    @Override
    public void onDisplay() {
        instance = this;
    }

    @Override
    public void onFinished() {
        instance = new SelectionSpec();
    }

    private static class InstanceHolder {
        public static SelectionSpec INSTANCE;
        public static ImageEngine imageEngineHolder;
    }

    public static SelectionSpec instance;

    public static SelectionSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static void setInstance(SelectionSpec selectionSpec) {
        InstanceHolder.INSTANCE = selectionSpec;
    }

    public static SelectionSpec getNewCleanInstance(ImageEngine imageEngine) {
        if (imageEngine == null) {
            throw new IllegalArgumentException("the param imageEngine can't be null.");
        }
        setDefaultImageEngine(imageEngine);
        return new SelectionSpec();
    }

    public static void setDefaultImageEngine(ImageEngine imageEngine) {
        InstanceHolder.imageEngineHolder = imageEngine;
    }
}
