package com.example.chriscorscadden1.canadaapptest;

import java.io.File;
import android.content.Context;

public class FileCache {

    private File cacheDir;

    // Constructor used to set cacheDir to the cache directory
    public FileCache(Context context) {
        // Find the directory to save cached images
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "CanadaAppTest");
        else
            cacheDir = context.getCacheDir();
        // Checks to see if cache directory exist
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    // Takes a url and gets the filename of the image then creates the new file in the image cache
    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;

    }

    // Clears image cache of downloaded images
    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}