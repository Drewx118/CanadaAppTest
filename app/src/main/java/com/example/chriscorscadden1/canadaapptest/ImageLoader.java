package com.example.chriscorscadden1.canadaapptest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Matrix;
import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class ImageLoader {

    // TAG for ImageLoader used for log messages
    private static final String TAG = "ImageLoader";
    // memoryCache used to store image url and bitmaps in memory cache
    MemoryCache memoryCache = new MemoryCache();
    // fileCache used to get images stored on the mobile device
    FileCache fileCache;
    // imageViews stores imageView and url of the image to be loaded
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    // executorService manages the threads that download the image from the internet
    ExecutorService executorService;
    // Handler to display images in UI thread
    Handler handler = new Handler();

    // Constructor initalises fileCache and creates the thread pool
    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    // Takes url and imageView and then displays the images
    public void DisplayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            queuePhoto(url, imageView);
        }
    }

    // Adds the photo to be loaded in the thread pool
    private void queuePhoto(String url, ImageView imageView) {
        // Creates a new PhotoToLoad object
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        // Runs PhotosLoader in a new thread
        executorService.submit(new PhotosLoader(p));
    }

    // Gets the bitmap of the images by downloading them using a HTTP client if it can't be found on the file cache
    private Bitmap getBitmap(String url) {
        // Gets the image file in the file cache
        File f = fileCache.getFile(url);
        // Resizes the bitmap file
        Bitmap b = decodeFile(f);

        // Returns the bitmap if we found it in the file cache
        if (b != null)
            return b;

        // Downloads the image from the url
        try {
            Bitmap bitmap = null;

            try{
                //Create an HTTP client
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();
                //Perform the request and check the status code
                if(statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    // Input stream from the URL
                    InputStream is = entity.getContent();
                    // Output stream to the image file in the file cache
                    OutputStream os = new FileOutputStream(f);
                    // Copies the data from the input stream to the image file in the file cache
                    Utils.CopyStream(is, os);
                    // Close the input and output streams
                    os.close();
                    is.close();
                } else {
                    Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
                }
            } catch(Exception ex) {
                Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
            }
            // Resizes the bitmap file
            bitmap = decodeFile(f);
            // Returns the image bitmap
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    // Decodes image and scales it to fit the appropriate size for the app and returns the resize bitmap
    private Bitmap decodeFile(File f) {
        try {
            // Creates new file input stream
            FileInputStream stream = new FileInputStream(f);
            // Decodes the bitmap from the stream
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            // Closes the stream
            stream.close();
            // Find the width and height of the bitmap
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            // The scale for the new image width
            float scaleWidth = ((float) 300) / width;
            // The Scale for the new image height
            float scaleHeight = ((float) 300) / height;
            // Create a matrix for image manipulation
            Matrix matrix = new Matrix();
            // Resize the bitmap
            matrix.postScale(scaleWidth, scaleHeight);
            // Recreate the bitmap
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix, false);
            // Returns the resize bitmap
            return resizedBitmap;

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // PhotoToLoad class that stores the data for the image we are loading into the app
    private class PhotoToLoad {

        // url stores the image url
        public String url;
        // imageView stores the image view
        public ImageView imageView;

        // Constructor that initialises the url and imageView
        public PhotoToLoad(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }
    }

    // Photosloader class that runs a thread for each item
    class PhotosLoader implements Runnable {
        // Photo that will be loaded
        PhotoToLoad photoToLoad;

        // Constructor that initialises the photo to be loaded
        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    // Determines if the imageView is being reused
    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        // bitmap stores the bitmap for the image
        Bitmap bitmap;
        //  photoToLoad stores the data for the image we are loading
        PhotoToLoad photoToLoad;

        // Constructor that initalises the bitmap and photoToLoad
        public BitmapDisplayer(Bitmap bitmap, PhotoToLoad photoToLoad) {
            this.bitmap = bitmap;
            this.photoToLoad = photoToLoad;
        }

        // Sets the imageView to the image's bitmap
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
        }
    }

    // Clears the memory and file cache
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}