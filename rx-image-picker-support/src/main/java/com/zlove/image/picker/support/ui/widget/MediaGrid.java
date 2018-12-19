package com.zlove.image.picker.support.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zlove.image.picker.support.R;
import com.zlove.image.picker.support.entity.Item;
import com.zlove.image.picker.support.entity.SelectionSpec;

public class MediaGrid extends SquareFrameLayout implements View.OnClickListener {

    ImageView mThumbnail;
    CheckView mCheckView;
    ImageView mGifTag;
    TextView mVideoDuration;

    Item media;
    PreBindInfo mPreBindInfo;
    OnMediaGridClickListener mListener;

    public MediaGrid(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MediaGrid(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MediaGrid(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(getLayoutRes(), this, true);

        mThumbnail = findViewById(R.id.media_thumbnail);
        mCheckView = findViewById(R.id.check_view);
        mGifTag = findViewById(R.id.gif);
        mVideoDuration = findViewById(R.id.video_duration);

        mThumbnail.setOnClickListener(this);
        mCheckView.setOnClickListener(this);
    }

    public int getLayoutRes() {
        return R.layout.media_grid_content;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (v == mThumbnail) {
                mListener.onThumbnailClicked(mThumbnail, media, mPreBindInfo.mViewHolder);
            } else if (v == mCheckView) {
                mListener.onCheckViewClicked(mCheckView, media, mPreBindInfo.mViewHolder);
            }
        }
    }

    public void preBindMedia(PreBindInfo info) {
        mPreBindInfo = info;
    }

    public void bindMedia(Item item) {
        media = item;
        setGifTag();
        initCheckView();
        setImage();
        setVideoDuration();
    }

    private void setGifTag() {
        if (media.isGif()) {
            mGifTag.setVisibility(VISIBLE);
        } else {
            mGifTag.setVisibility(View.GONE);
        }
    }

    private void initCheckView() {
        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable);
    }

    public void setCheckEnabled(boolean enabled) {
        mCheckView.setEnabled(enabled);
    }

    public void setCheckedNum(int checkedNum) {
        mCheckView.setCheckedNum(checkedNum);
    }

    public void setChecked(boolean checked) {
        mCheckView.setChecked(checked);
    }

    private void setImage() {
        if (media.isGif()) {
            SelectionSpec.instance.imageEngine.loadGifThumbnail(getContext(), mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, media.contentUri);
        } else {
            SelectionSpec.instance.imageEngine.loadThumbnail(getContext(), mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, media.contentUri);
        }
    }

    private void setVideoDuration() {
        if (media.isVideo()) {
            mVideoDuration.setVisibility(View.VISIBLE);
            mVideoDuration.setText(DateUtils.formatElapsedTime(media.duration / 1000));
        } else {
            mVideoDuration.setVisibility(View.GONE);
        }
    }

    public void setOnMediaGridClickListener(OnMediaGridClickListener listener) {
        mListener = listener;
    }

    interface OnMediaGridClickListener {
        void onThumbnailClicked(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder);
        void onCheckViewClicked(CheckView checkView, Item item, RecyclerView.ViewHolder holder);
    }

    static class PreBindInfo {

        public int mResize;
        public Drawable mPlaceholder;
        public boolean mCheckViewCountable;
        public RecyclerView.ViewHolder mViewHolder;

        public PreBindInfo(int resize, Drawable placeholder,
                           boolean checkViewCountable,
                           RecyclerView.ViewHolder viewHolder) {
            this.mResize = resize;
            this.mPlaceholder = placeholder;
            this.mCheckViewCountable = checkViewCountable;
            this.mViewHolder = viewHolder;
        }
    }
}
