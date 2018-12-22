package com.zlove.image.picker.support.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.zlove.image.picker.support.MimeType;
import com.zlove.image.picker.support.R;
import com.zlove.image.picker.support.entity.IncapableCause;
import com.zlove.image.picker.support.entity.Item;
import com.zlove.image.picker.support.entity.SelectionSpec;
import com.zlove.image.picker.support.filter.Filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class PhotoMetadataUtils {

    private static final String TAG = PhotoMetadataUtils.class.getSimpleName();
    private static final int MAX_WIDTH = 1600;
    private static final String SCHEME_CONTENT = "content";

    public static Point getBitmapSize(Uri uri, Activity activity) {
        ContentResolver resolver = activity.getContentResolver();
        Point imageSize = getBitmapBound(resolver, uri);
        int w = imageSize.x;
        int h = imageSize.y;
        if (PhotoMetadataUtils.shouldRotate(resolver, uri)) {
            w = imageSize.y;
            h = imageSize.x;
        }
        if (h == 0) return new Point(MAX_WIDTH, MAX_WIDTH);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int widthScale = screenWidth / w;
        int heightScale = screenHeight / h;
        if (widthScale > heightScale) {
            return new Point((w * widthScale), (h * heightScale));
        } else {
            return new Point((w * widthScale), (h * heightScale));
        }
    }

    private static Point getBitmapBound(ContentResolver resolver, Uri uri) {
        InputStream inputStream= null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            inputStream = resolver.openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            return new Point(width, height);
        } catch (FileNotFoundException e) {
            return new Point(0, 0);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static String getPath(ContentResolver resolver, Uri uri) {
        if (uri == null) {
            return null;
        }
        if (SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (cursor == null || !cursor.moveToFirst()) {
                    return null;
                } else {
                    return cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return uri.getPath();
    }

    public static IncapableCause isAcceptable(Context context, Item item) {
        if (!isSelectableType(context, item)) {
            return new IncapableCause(context.getString(R.string.error_file_type));
        }

        if (SelectionSpec.instance.filters != null) {
            for (Filter filter : SelectionSpec.instance.filters) {
                IncapableCause incapableCause = filter.filter(context, item);
                if (incapableCause != null) {
                    return incapableCause;
                }
            }
        }
        return null;
    }

    private static boolean isSelectableType(Context context, Item item) {
        if (context == null) {
            return false;
        }

        ContentResolver resolver = context.getContentResolver();
        for (MimeType type : SelectionSpec.instance.mimeTypeSet) {
            if (type.checkType(resolver, item.contentUri)) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldRotate(ContentResolver resolver, Uri uri) {
        ExifInterface exif;
        try {
            exif = ExifInterfaceCompat.newInstance(getPath(resolver, uri));
        } catch (IOException e) {
            Log.e(TAG, "could not read exif info of the image: $uri");
            return false;
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        return orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270;
    }

    public static float getSizeInMB(Long sizeInBytes) {
        return Float.valueOf(new DecimalFormat("0.0").format((sizeInBytes / 1024f / 1024f)));
    }

}
