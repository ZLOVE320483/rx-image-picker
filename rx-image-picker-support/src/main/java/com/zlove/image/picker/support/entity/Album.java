package com.zlove.image.picker.support.entity;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.zlove.image.picker.support.R;
import com.zlove.image.picker.support.loader.AlbumLoader;

public class Album implements Parcelable {

    public static final String ALBUM_ID_ALL = "-1";

    public final String id;
    public final String coverPath;
    private final String mDisplayName;
    public long count;
    public final boolean isAll;
    public final boolean isEmpty;

    public Album(String id, String coverPath, String albumName, long count) {
        this.id = id;
        this.coverPath = coverPath;
        this.mDisplayName = albumName;
        this.count = count;
        isAll = ALBUM_ID_ALL.equals(id);
        isEmpty = count == 0;
    }

    public Album(Parcel source) {
        id = source.readString();
        coverPath = source.readString();
        mDisplayName = source.readString();
        count = source.readLong();
        isAll = ALBUM_ID_ALL.equals(id);
        isEmpty = count == 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(coverPath);
        dest.writeString(mDisplayName);
        dest.writeLong(count);
    }

    public void addCaptureCount() {
        count++;
    }

    public String getDisplayName(Context context) {
        if (isAll) {
            return context.getString(R.string.album_name_all);
        } else {
            return mDisplayName;
        }
    }

    public static final Parcelable.Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public static Album valueOf(Cursor cursor) {
        return new Album(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                cursor.getLong(cursor.getColumnIndex(AlbumLoader.COLUMN_COUNT))
        );
    }
}
