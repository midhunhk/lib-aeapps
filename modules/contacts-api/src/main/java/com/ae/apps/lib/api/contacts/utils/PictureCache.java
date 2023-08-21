package com.ae.apps.lib.api.contacts.utils;

import android.graphics.Bitmap;

/**
 * An interface that specifies a cache for pictures
 */
public interface PictureCache {

    /**
     * Inserts a picture with a key
     *
     * @param key the key
     * @param picture the picture to insert
     */
    void insert(String key, Bitmap picture);

    /**
     * Retrieve a picture associated with the key
     *
     * @param key the key
     * @return the picture stored with this key
     */
    Bitmap retrieve(String key);

    /**
     * Remove an item from the cache
     *
     * @param key the key
     */
    void remove(String key);
}
