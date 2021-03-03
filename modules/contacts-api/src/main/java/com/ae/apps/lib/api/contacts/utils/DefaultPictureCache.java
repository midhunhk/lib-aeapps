package com.ae.apps.lib.api.contacts.utils;

import android.graphics.Bitmap;

import androidx.collection.LruCache;

/**
 * An internal helper class to cache decoded Bitmaps by the ContactsApiGateway
 */
public class DefaultPictureCache implements PictureCache {

    private static final int LRU_MAX_SIZE = 8;
    private final LruCache<String, Bitmap> cache;

    private DefaultPictureCache(final int maxSize){
        cache = new LruCache<>(maxSize);
    }

    public static DefaultPictureCache newInstance(){
        return new DefaultPictureCache(LRU_MAX_SIZE);
    }

    public void insert(String key, Bitmap picture){
        cache.put(key, picture);
    }

    public Bitmap retrieve(String key) {
        return cache.get(key);
    }

    public void remove(String key){
        cache.remove(key);
    }
}
