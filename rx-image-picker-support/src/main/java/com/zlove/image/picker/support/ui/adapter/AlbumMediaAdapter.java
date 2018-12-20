package com.zlove.image.picker.support.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zlove.image.picker.support.R;
import com.zlove.image.picker.support.entity.Album;
import com.zlove.image.picker.support.entity.IncapableCause;
import com.zlove.image.picker.support.entity.Item;
import com.zlove.image.picker.support.entity.SelectionSpec;
import com.zlove.image.picker.support.model.SelectedItemCollection;
import com.zlove.image.picker.support.ui.widget.CheckView;
import com.zlove.image.picker.support.ui.widget.MediaGrid;

public class AlbumMediaAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder> implements MediaGrid.OnMediaGridClickListener {

    private static final int VIEW_TYPE_CAPTURE = 0x01;
    private static final int VIEW_TYPE_MEDIA = 0x02;

    private Context mContext;
    private SelectedItemCollection mSelectedCollection;
    private RecyclerView mRecyclerView;

    private Drawable mPlaceholder;
    private SelectionSpec mSelectionSpec;
    private CheckStateListener mCheckStateListener;
    private OnMediaClickListener mOnMediaClickListener;
    private int mImageResize;

    public int itemLayoutRes = R.layout.media_grid_item;

    public AlbumMediaAdapter(Context context, SelectedItemCollection selectedItemCollection, RecyclerView recyclerView) {
        super(null);
        this.mContext = context;
        this.mSelectedCollection = selectedItemCollection;
        this.mRecyclerView = recyclerView;

        mSelectionSpec = SelectionSpec.getInstance();
        TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{R.attr.item_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CAPTURE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_capture_item, parent, false);
            CaptureViewHolder holder = new CaptureViewHolder(itemView);
            holder.itemView.setOnClickListener(v -> {
                if (itemView.getContext() instanceof OnPhotoCapture) {
                    ((OnPhotoCapture) itemView.getContext()).capture();
                }
            });
            return holder;
        } else if (viewType == VIEW_TYPE_MEDIA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutRes, parent, false);
            return new MediaViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutRes, parent, false);
            return new MediaViewHolder(view);
        }
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        if (holder instanceof CaptureViewHolder) {
            Drawable[] drawables = ((CaptureViewHolder) holder).mHint.getCompoundDrawables();
            TypedArray ta = holder.itemView.getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.capture_textColor});
            int color = ta.getColor(0, 0);
            ta.recycle();

            for (int i = 0; i < drawables.length; i++) {
                Drawable drawable = drawables[i];
                if (drawable != null) {
                    Drawable.ConstantState state = drawable.getConstantState();
                    if (state == null) {
                        continue;
                    }
                    Drawable newDrawable = state.newDrawable().mutate();
                    newDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    newDrawable.setBounds(drawable.getBounds());
                    drawables[i] = newDrawable;
                }
            }
            ((CaptureViewHolder) holder).mHint.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        } else if (holder instanceof MediaViewHolder) {

            Item item = Item.valueOf(cursor);
            ((MediaViewHolder) holder).mMediaGrid.preBindMedia(new MediaGrid.PreBindInfo(
                    getImageResize(((MediaViewHolder) holder).mMediaGrid.getContext()),
                    mPlaceholder,
                    mSelectionSpec.countable,
                    holder
            ));
            ((MediaViewHolder) holder).mMediaGrid.bindMedia(item);
            ((MediaViewHolder) holder).mMediaGrid.setOnMediaGridClickListener(this);
            setCheckStatus(item, ((MediaViewHolder) holder).mMediaGrid);
        }
    }

    private void setCheckStatus(Item item, MediaGrid mediaGrid) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum > 0) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setCheckedNum(checkedNum);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setCheckedNum(checkedNum);
                }
            }
        } else {
            boolean selected = mSelectedCollection.isSelected(item);
            if (selected) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setChecked(true);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setChecked(false);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onThumbnailClicked(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder) {
        mOnMediaClickListener.onMediaClick(null, item, holder.getAdapterPosition());

    }

    @Override
    public void onCheckViewClicked(CheckView checkView, Item item, RecyclerView.ViewHolder holder) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum == CheckView.UNCHECKED) {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged();
                }
            } else {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged();
            }
        } else {
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged();
            } else {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged();
                }
            }
        }
    }

    private void notifyCheckStateChanged() {
        notifyDataSetChanged();
        mCheckStateListener.onUpdate();
    }

    @Override
    public int getItemViewType(int position, Cursor cursor) {
         if (cursor == null) {
             return VIEW_TYPE_MEDIA;
        } else {
            if (Item.valueOf(cursor).isCapture())
                return VIEW_TYPE_CAPTURE;
            else
                return VIEW_TYPE_MEDIA;
        }
    }

    private boolean assertAddSelection(Context context, Item item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }

    public void registerCheckStateListener(CheckStateListener listener) {
        mCheckStateListener = listener;
    }

    public void unregisterCheckStateListener() {
        mCheckStateListener = null;
    }

    public void registerOnMediaClickListener(OnMediaClickListener listener) {
        mOnMediaClickListener = listener;
    }

    public void unregisterOnMediaClickListener() {
        mOnMediaClickListener = null;
    }

    private void refreshSelection() {
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        if (first == -1 || last == -1) {
            return;
        }
        Cursor cursor1 = cursor;
        for (int i = first; i < last; i++) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(first);
            if (holder instanceof MediaViewHolder && cursor1 != null) {
                if (cursor1.moveToPosition(i)) {
                    setCheckStatus(Item.valueOf(cursor1), ((MediaViewHolder) holder).mMediaGrid);
                }
            }
        }
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
                    R.dimen.media_grid_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * mSelectionSpec.thumbnailScale);
        }
        return mImageResize;
    }

    interface CheckStateListener {
        void onUpdate();
    }

    interface OnMediaClickListener {
        void onMediaClick(Album album, Item item, int adapterPosition);
    }

    interface OnPhotoCapture {
        void capture();
    }

    private class MediaViewHolder extends RecyclerView.ViewHolder {

        MediaGrid mMediaGrid;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaGrid = (MediaGrid) itemView;
        }
    }

    private class CaptureViewHolder extends RecyclerView.ViewHolder {

        TextView mHint;

        public CaptureViewHolder(@NonNull View itemView) {
            super(itemView);
            mHint = itemView.findViewById(R.id.hint);
        }
    }

}
