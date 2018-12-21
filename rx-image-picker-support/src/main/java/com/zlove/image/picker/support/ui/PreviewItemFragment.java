package com.zlove.image.picker.support.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zlove.image.picker.support.entity.Item;

public class PreviewItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";

    public static PreviewItemFragment newInstance(Item item) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }

}
