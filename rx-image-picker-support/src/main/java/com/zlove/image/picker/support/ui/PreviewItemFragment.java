package com.zlove.image.picker.support.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.zlove.image.picker.support.R;
import com.zlove.image.picker.support.entity.Item;
import com.zlove.image.picker.support.entity.SelectionSpec;
import com.zlove.image.picker.support.utils.PhotoMetadataUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class PreviewItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";

    public static PreviewItemFragment newInstance(Item item) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Item item = getArguments().getParcelable(ARGS_ITEM);
        if (item == null) {
            return;
        }
        ImageView videoPlayButton = view.findViewById(R.id.video_play_button);
        if (item.isVideo()) {
            videoPlayButton.setVisibility(View.VISIBLE);
            videoPlayButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(item.contentUri, "video/*");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), R.string.error_no_video_activity, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            videoPlayButton.setVisibility(View.GONE);
        }

        ImageViewTouch image = view.findViewById(R.id.image_view);
                image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        Point size = PhotoMetadataUtils.getBitmapSize(item.contentUri, getActivity());
        if (item.isGif()) {
            SelectionSpec.instance.imageEngine.loadGifImage(getContext(), size.x, size.y, image,
                    item.contentUri);
        } else {
            SelectionSpec.instance.imageEngine.loadImage(getContext(), size.x, size.y, image,
                    item.contentUri);
        }
    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.image_view)).resetMatrix();
        }
    }
}
