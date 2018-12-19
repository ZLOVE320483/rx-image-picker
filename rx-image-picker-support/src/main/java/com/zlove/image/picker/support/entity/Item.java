package com.zlove.image.picker.support.entity;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.zlove.image.picker.support.MimeType;

public class Item implements Parcelable {

    public static final long ITEM_ID_CAPTURE = -1;
    public static final String ITEM_DISPLAY_NAME_CAPTURE = "Capture";

    private final long id;
    private final String mimeType;
    public final Uri contentUri;
    private final long size;
    public final long duration;

    public Item(long id, String mimeType, long size, long duration) {
        this.id = id;
        this.mimeType = mimeType;
        Uri contentUri;
        if (isImage()) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (isVideo()) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = MediaStore.Files.getContentUri("external");
        }
        this.contentUri = ContentUris.withAppendedId(contentUri, id);
        this.size = size;
        this.duration = duration;
    }

    private Item(Parcel source) {
        id = source.readLong();
        mimeType = source.readString();
        contentUri = source.readParcelable(Uri.class.getClassLoader());
        size = source.readLong();
        duration = source.readLong();
    }

    public final boolean isCapture() {
        return id == ITEM_ID_CAPTURE;
    }

    public final boolean isImage() {
        if (TextUtils.isEmpty(mimeType)) {
            return false;
        }
        return mimeType.equals(MimeType.JPEG.toString())
                || mimeType.equals(MimeType.PNG.toString())
                || mimeType.equals(MimeType.GIF.toString())
                || mimeType.equals(MimeType.BMP.toString())
                || mimeType.equals(MimeType.WEBP.toString());
    }

    public final boolean isGif() {
        if (TextUtils.isEmpty(mimeType)) {
            return false;
        }
        return mimeType.equals(MimeType.GIF.toString());
    }

    public final boolean isVideo() {
        if (TextUtils.isEmpty(mimeType)) {
            return false;
        }
        return mimeType.equals(MimeType.MPEG.toString())
                || mimeType.equals(MimeType.MP4.toString())
                || mimeType.equals(MimeType.QUICKTIME.toString())
                || mimeType.equals(MimeType.THREEGPP.toString())
                || mimeType.equals(MimeType.THREEGPP2.toString())
                || mimeType.equals(MimeType.MKV.toString())
                || mimeType.equals(MimeType.WEBM.toString())
                || mimeType.equals(MimeType.TS.toString())
                || mimeType.equals(MimeType.AVI.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(mimeType);
        dest.writeParcelable(contentUri, 0);
        dest.writeLong(size);
        dest.writeLong(duration);
    }

    private static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }
        Item other = (Item) obj;
        return (id == other.id
                && (mimeType != null && mimeType.equals(other.mimeType) ||  TextUtils.isEmpty(mimeType) && TextUtils.isEmpty(other.mimeType))
                && (contentUri != null && contentUri == other.contentUri || contentUri == null && other.contentUri == null)
                && size == other.size
                && duration == other.duration);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + java.lang.Long.valueOf(id).hashCode();
        if (mimeType != null) {
            result = 31 * result + mimeType.hashCode();
        }
        result = 31 * result + contentUri.hashCode();
        result = 31 * result + java.lang.Long.valueOf(size).hashCode();
        result = 31 * result + java.lang.Long.valueOf(duration).hashCode();
        return result;
    }
}
