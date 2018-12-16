package com.zlove.image.picker.support.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;

import com.zlove.image.picker.support.entity.Album;

public class AlbumLoader extends CursorLoader {

    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            COLUMN_COUNT
    };

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id", "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT
    };

    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + ") GROUP BY bucket_id";

    private static final String BUCKET_ORDER_BY = "datetaken DESC";

    private AlbumLoader(@NonNull Context context,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, BUCKET_ORDER_BY);
    }

    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    public static CursorLoader newInstance(Context context) {
        String selection = SELECTION_FOR_SINGLE_MEDIA_TYPE;
        String[] selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
        return new AlbumLoader(context, selection, selectionArgs);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);
        int totalCount = 0;
        String allAlbumCoverPath = "";
        if (albums != null) {
            while (albums.moveToNext()) {
                totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT));
            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
            }
        }
        allAlbum.addRow(new Object[]{Album.ALBUM_ID_ALL,
                Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumCoverPath, String.valueOf(totalCount)});

        return new MergeCursor(new Cursor[]{allAlbum, albums});
    }
}
