package com.zlove.image.picker.support.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.zlove.image.picker.support.R;
import com.zlove.image.picker.support.entity.IncapableCause;
import com.zlove.image.picker.support.entity.Item;
import com.zlove.image.picker.support.entity.SelectionSpec;
import com.zlove.image.picker.support.ui.widget.CheckView;
import com.zlove.image.picker.support.utils.PhotoMetadataUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SelectedItemCollection {
    public static final String STATE_SELECTION = "state_selection";
    public static final String STATE_COLLECTION_TYPE = "state_collection_type";
    /**
     * Empty collection
     */
    public static final int COLLECTION_UNDEFINED = 0x00;
    /**
     * Collection only with images
     */
    public static final int COLLECTION_IMAGE = 0x01;
    /**
     * Collection only with videos
     */
    public static final int COLLECTION_VIDEO = 0x01 << 1;
    /**
     * Collection with images and videos.
     */
    public static final int COLLECTION_MIXED = COLLECTION_IMAGE | COLLECTION_VIDEO;

    private final Context mContext;

    private Set<Item> mItems;
    private int collectionType = COLLECTION_UNDEFINED;

    public Bundle dataWithBundle;
    private boolean isEmpty;

    public SelectedItemCollection(Context context) {
        this.mContext = context;
    }

    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        bundle.putInt(STATE_COLLECTION_TYPE, collectionType);
        return bundle;
    }

    public boolean isEmpty() {
        return mItems == null || mItems.isEmpty();
    }

    public void onCreate(Bundle bundle) {
        if (bundle == null) {
            mItems = new LinkedHashSet<>();
        } else {
            ArrayList<Item> saved = bundle.getParcelableArrayList(STATE_SELECTION);
            mItems = new LinkedHashSet<>(saved);
            collectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
        }
    }

    public void setDefaultSelection(List<Item> uris) {
        mItems.addAll(uris);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        outState.putInt(STATE_COLLECTION_TYPE, collectionType);
    }

    public boolean add(Item item) {
        if (typeConflict(item)) {
            throw new IllegalArgumentException("Can't select images and videos at the same time.");
        }
        boolean added = mItems.add(item);
        if (added) {
            if (collectionType == COLLECTION_UNDEFINED) {
                if (item.isImage()) {
                    collectionType = COLLECTION_IMAGE;
                } else if (item.isVideo()) {
                    collectionType = COLLECTION_VIDEO;
                }
            } else if (collectionType == COLLECTION_IMAGE) {
                if (item.isVideo()) {
                    collectionType = COLLECTION_MIXED;
                }
            } else if (collectionType == COLLECTION_VIDEO) {
                if (item.isImage()) {
                    collectionType = COLLECTION_MIXED;
                }
            }
        }
        return added;
    }

    public boolean remove(Item item) {
        boolean removed = mItems.remove(item);
        if (removed) {
            if (mItems.size() == 0) {
                collectionType = COLLECTION_UNDEFINED;
            } else {
                if (collectionType == COLLECTION_MIXED) {
                    refineCollectionType();
                }
            }
        }
        return removed;
    }

    public void overwrite(ArrayList<Item> items, int collectionType) {
        if (items.size() == 0) {
            this.collectionType = COLLECTION_UNDEFINED;
        } else {
            this.collectionType = collectionType;
        }
        mItems.clear();
        mItems.addAll(items);
    }


    public List<Item> asList() {
        return new ArrayList<>(mItems);
    }

    public List<Uri> asListOfUri() {
        List<Uri> uris = new ArrayList<>();
        for (Item item : mItems) {
            uris.add(item.contentUri);
        }
        return uris;
    }

    public boolean isSelected(Item item) {
        return mItems.contains(item);
    }

    @SuppressLint("ResourceType")
    public IncapableCause isAcceptable(Item item) {
        if (maxSelectableReached()) {
            int maxSelectable = currentMaxSelectable();
            String cause;
            try {
                cause = mContext.getResources().getQuantityString(R.string.error_over_count, maxSelectable, maxSelectable);
            } catch (Resources.NotFoundException e) {
                cause =  mContext.getString(R.string.error_over_count, maxSelectable);
            }

            return new IncapableCause(cause);
        } else if (typeConflict(item)) {
            return new IncapableCause(mContext.getString(R.string.error_type_conflict));
        }

        return PhotoMetadataUtils.isAcceptable(mContext, item);
    }

    public boolean maxSelectableReached() {
        return mItems.size() == currentMaxSelectable();
    }

    // depends
    private int currentMaxSelectable()  {
        SelectionSpec spec = SelectionSpec.getInstance();
        if (spec.maxSelectable > 0) {
            return spec.maxSelectable;
        } else if (collectionType == COLLECTION_IMAGE) {
            return spec.maxImageSelectable;
        } else if (collectionType == COLLECTION_VIDEO) {
            return spec.maxVideoSelectable;
        } else {
            return spec.maxSelectable;
        }
    }

    private void refineCollectionType() {
        boolean hasImage = false;
        boolean hasVideo = false;
        for (Item i : mItems) {
            if (i.isImage() && !hasImage) hasImage = true;
            if (i.isVideo() && !hasVideo) hasVideo = true;
        }
        if (hasImage && hasVideo) {
            collectionType = COLLECTION_MIXED;
        } else if (hasImage) {
            collectionType = COLLECTION_IMAGE;
        } else if (hasVideo) {
            collectionType = COLLECTION_VIDEO;
        }
    }

    /**
     * Determine whether there will be conflict media types. A user can only select images and videos at the same time
     * while [SelectionSpec.mediaTypeExclusive] is set to false.
     */
    public boolean typeConflict(Item item) {
        return SelectionSpec.getInstance().mediaTypeExclusive
                && (item.isImage() && (collectionType == COLLECTION_VIDEO
                || collectionType == COLLECTION_MIXED)
                || item.isVideo()
                && (collectionType == COLLECTION_IMAGE
                || collectionType == COLLECTION_MIXED));
    }

    public int count() {
        return mItems.size();
    }

    public int checkedNumOf(Item item) {
        int index = new ArrayList<>(mItems).indexOf(item);
        if (index == -1) {
           return CheckView.UNCHECKED;
        } else {
            return index + 1;
        }
    }
}
