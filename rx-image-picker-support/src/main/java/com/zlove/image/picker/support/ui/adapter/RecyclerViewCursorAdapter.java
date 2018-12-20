package com.zlove.image.picker.support.ui.adapter;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Cursor cursor;
    private int mRowIDColumn;

    public RecyclerViewCursorAdapter(Cursor cursor) {
        setHasStableIds(true);
        swapCursor(cursor);
    }

    protected abstract void onBindViewHolder(VH vh, Cursor cursor);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (!isDataValid(cursor)) {
            throw new IllegalStateException("Cannot bind view holder when cursor is in invalid state.");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to bind view holder");
        }

        onBindViewHolder(holder, cursor);
    }

    @Override
    public int getItemViewType(int position) {
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to get item view type.");
        }
        return getItemViewType(position, cursor);
    }

    protected abstract int getItemViewType(int position, Cursor cursor);

    @Override
    public int getItemCount() {
        if (isDataValid(cursor)) {
            return cursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (!isDataValid(cursor)) {
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to get an item id");
        }

        return cursor.getLong(mRowIDColumn);
    }

    private void swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return;
        }
        if (newCursor != null) {
            cursor = newCursor;
            mRowIDColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            cursor = null;
            mRowIDColumn = -1;
        }
    }

    private boolean isDataValid(Cursor cursor) {
        return cursor != null && !cursor.isClosed();
    }
}
