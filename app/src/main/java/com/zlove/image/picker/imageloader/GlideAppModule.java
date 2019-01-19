package com.zlove.image.picker.imageloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class GlideAppModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context,
                diskCacheFolderName(context),
                diskCacheSizeBytes()))
                .setMemoryCache(new LruResourceCache(memoryCacheSizeBytes()));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    /**
     * set the memory cache size, unit is the {@link Byte}.
     */
    private int memoryCacheSizeBytes() {
        return 1024 * 1024 * 20; // 20 MB
    }

    /**
     * set the disk cache size, unit is the {@link Byte}.
     */
    private int diskCacheSizeBytes() {
        return 1024 * 1024 * 512; // 512 MB
    }

    /**
     * set the disk cache folder's name.
     */
    private String diskCacheFolderName(Context context) {
        return ContextCompat.getCodeCacheDir(context).getPath() + "/rximagepicker";
    }
}
