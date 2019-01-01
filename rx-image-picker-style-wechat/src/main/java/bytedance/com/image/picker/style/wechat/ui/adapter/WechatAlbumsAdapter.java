package bytedance.com.image.picker.style.wechat.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.zlove.image.picker.support.entity.Album;
import com.zlove.image.picker.support.entity.SelectionSpec;

import java.io.File;

import bytedance.com.image.picker.style.wechat.R;

public class WechatAlbumsAdapter extends CursorAdapter {

    private Drawable mPlaceholder;

    public WechatAlbumsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
    }

    public WechatAlbumsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, SelectionSpec.getInstance().themeId);
        return LayoutInflater.from(context)
                .cloneInContext(contextThemeWrapper)
                .inflate(R.layout.wechat_album_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Album album = Album.valueOf(cursor);
        ((TextView) view.findViewById(R.id.album_name)).setText(album.getDisplayName(context));
        ((TextView) view.findViewById(R.id.album_media_count)).setText(String.valueOf(album.count));

        SelectionSpec.getInstance().imageEngine.loadThumbnail(context,
                context.getResources().getDimensionPixelSize(R.dimen.wechat_media_grid_size),
                mPlaceholder,
                view.findViewById(R.id.album_cover),
                Uri.fromFile(new File(album.coverPath)));
    }
}
