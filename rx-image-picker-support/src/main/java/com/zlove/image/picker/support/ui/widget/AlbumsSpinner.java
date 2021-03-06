package com.zlove.image.picker.support.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.zlove.image.picker.support.R;
import com.zlove.image.picker.support.entity.Album;
import com.zlove.image.picker.support.utils.Platform;

public class AlbumsSpinner {
    protected static final int MAX_SHOWN_COUNT = 6;

    protected CursorAdapter mAdapter;
    protected TextView mSelected;
    protected ListPopupWindow mListPopupWindow;
    protected AdapterView.OnItemSelectedListener mOnItemSelectedListener;

    public AlbumsSpinner() {
    }

    public AlbumsSpinner(Context context) {
        mListPopupWindow = new ListPopupWindow(context, null, R.attr.listPopupWindowStyle);
        mListPopupWindow.setModal(true);
        float density = context.getResources().getDisplayMetrics().density;
        mListPopupWindow.setContentWidth((int) (216 * density));
        mListPopupWindow.setHorizontalOffset((int) ((16 * density)));
        mListPopupWindow.setVerticalOffset((int) (-48 * density));

        mListPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            onItemSelected(context, position);
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(parent, view, position, id);
            }
        });
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public void setSelection(Context context, int position) {
        mListPopupWindow.setSelection(position);
        onItemSelected(context, position);
    }


    protected void onItemSelected(Context context, int position) {
        mListPopupWindow.dismiss();
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        Album album = Album.valueOf(cursor);
        String displayName = album.getDisplayName(context);
        if (mSelected.getVisibility() == View.VISIBLE) {
            mSelected.setText(displayName);
        } else {
            if (Platform.hasICS()) {
                mSelected.setAlpha(0.0f);
                mSelected.setVisibility(View.VISIBLE);
                mSelected.setText(displayName);
                mSelected.animate().alpha(1.0f).setDuration(context.getResources().getInteger(android.R.integer.config_longAnimTime)).start();
            } else {
                mSelected.setVisibility(View.VISIBLE);
                mSelected.setText(displayName);
            }

        }
    }

    public void setAdapter(CursorAdapter adapter) {
        mListPopupWindow.setAdapter(adapter);
        mAdapter = adapter;
    }

    public void setSelectedTextView(TextView textView) {
        mSelected = textView;
        // tint dropdown arrow icon
        Drawable[] drawables = mSelected.getCompoundDrawables();
        Drawable right = drawables[2];
        TypedArray ta = mSelected.getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.album_element_color});
        int color = ta.getColor(0, 0);
        ta.recycle();
        right.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        mSelected.setVisibility(View.GONE);
        mSelected.setOnClickListener(v -> {
            int itemHeight = v.getResources().getDimensionPixelSize(R.dimen.album_item_height);
            if (mAdapter.getCount() > MAX_SHOWN_COUNT) {
                mListPopupWindow.setHeight(itemHeight * MAX_SHOWN_COUNT);
            } else {
                mListPopupWindow.setHeight(itemHeight * mAdapter.getCount());
            }
            mListPopupWindow.show();
        });
    }

    public void setPopupAnchorView(View view) {
        mListPopupWindow.setAnchorView(view);
    }
}
