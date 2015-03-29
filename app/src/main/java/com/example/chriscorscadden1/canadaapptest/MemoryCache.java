package com.example.chriscorscadden1.canadaapptest;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

    private static final String TAG = "MemoryCache";

    // Last argument true for LRU ordering
    private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));

    // Current allocated size
    private long size = 0;

    // Max memory in bytes
    private long limit = 1000000;

    public MemoryCache() {
        // Use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    // Sets the limit on the memory available to store the images
    public void setLimit(long limit) {
        this.limit = limit;
        Log.i(TAG, "MemoryCache will use up to " + this.limit / 1024. / 1024. + "MB");
    }

    // Returns image bitmap if it is stored in the memory cache
    public Bitmap get(String id) {
        try {
            if (!cache.containsKey(id))
                return null;
            return cache.get(id);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Puts bitmap into the memory cache
    public void put(String id, Bitmap bitmap) {
        try {
            if (cache.containsKey(id))
                size -= getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    // Checks the current size of the memory used in the cache
    private void checkSize() {
        Log.i(TAG, "cache size=" + size + " length=" + cache.size());
        // If memory cache size is greater than the limit we remove items till we are under the limit
        if (size > limit) {
            // Least recently accessed item will be the first one iterated
            Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (size <= limit)
                    break;
            }
            Log.i(TAG, "Clean cache. New size " + cache.size());
        }
    }

    // Clears the memory cache
    public void clear() {
        try {
            cache.clear();
            size = 0;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    // Returns the size of the bitmap in bytes
    long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
