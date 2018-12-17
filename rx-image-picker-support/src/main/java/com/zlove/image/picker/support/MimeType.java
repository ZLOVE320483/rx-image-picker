package com.zlove.image.picker.support;


import android.content.ContentResolver;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.zlove.image.picker.support.utils.PhotoMetadataUtils;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

public enum MimeType {

    JPEG("image/jpeg", MimeTypeUtils.setOf("jpg", "jpeg")),
    PNG("image/png", MimeTypeUtils.setOf("png")),
    GIF("image/gif", MimeTypeUtils.setOf("gif")),
    BMP("image/x-ms-bmp", MimeTypeUtils.setOf("bmp")),
    WEBP("image/webp", MimeTypeUtils.setOf("webp")),

    // ============== videos ==============
    MPEG("video/mpeg", MimeTypeUtils.setOf("mpeg", "mpg")),
    MP4("video/mp4", MimeTypeUtils.setOf("mp4", "m4v")),
    QUICKTIME("video/quicktime", MimeTypeUtils.setOf("mov")),
    THREEGPP("video/3gpp", MimeTypeUtils.setOf("3gp", "3gpp")),
    THREEGPP2("video/3gpp2", MimeTypeUtils.setOf("3g2", "3gpp2")),
    MKV("video/x-matroska", MimeTypeUtils.setOf("mkv")),
    WEBM("video/webm", MimeTypeUtils.setOf("webm")),
    TS("video/mp2ts", MimeTypeUtils.setOf("ts")),
    AVI("video/avi", MimeTypeUtils.setOf("avi"));

    private String mMimeTypeName;
    private Set<String> mExtensions;

    MimeType(String mimeTypeName, Set<String> extensions) {
        this.mMimeTypeName = mimeTypeName;
        this.mExtensions = extensions;
    }

    @Override
    public String toString() {
        return mMimeTypeName;
    }

    public boolean checkType(ContentResolver resolver, Uri uri) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        if (uri == null) {
            return false;
        }
        String type = map.getExtensionFromMimeType(resolver.getType(uri));
        String path = null;
        boolean pathParsed = false;
        for (String extension : mExtensions) {
            if (extension.equals(type)) {
                return true;
            }
            if (!pathParsed) {
                path = PhotoMetadataUtils.getPath(resolver, uri);
                if (!TextUtils.isEmpty(path)) {
                    path = path.toLowerCase(Locale.US);
                }
                pathParsed = true;
            }
            if (path != null && path.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static Set<MimeType> ofAll() {
        return EnumSet.allOf(MimeType.class);
    }

    public static Set<MimeType> of(MimeType type, MimeType... reset) {
        return EnumSet.of(type, reset);
    }

    public static Set<MimeType> ofImage() {
        return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP);
    }

    public static Set<MimeType> ofVideo() {
        return EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI);
    }
}
