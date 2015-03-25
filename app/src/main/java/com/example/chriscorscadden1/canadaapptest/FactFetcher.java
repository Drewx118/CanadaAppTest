package com.example.chriscorscadden1.canadaapptest;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chriscorscadden1 on 25/03/2015.
 */
public class FactFetcher extends AsyncTask {
    private static final String TAG = "FactFetcher";
    private static final String SERVER_URL = "https://dl.dropboxusercontent.com/u/746330/facts.json";

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            //Create an HTTP client
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SERVER_URL);

            //Perform the request and check the status code
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                try {
                    //Read the server response and attempt to parse it as JSON
                    Reader reader = new InputStreamReader(content);

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    List<Fact> facts = new ArrayList<Fact>();
                    facts = Arrays.asList(gson.fromJson(reader, Fact[].class));
                    content.close();

                    handlePostsList(facts);
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to parse JSON due to: " + ex);
                    failedLoadingPosts();
                }
            } else {
                Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
                failedLoadingPosts();
            }
        } catch(Exception ex) {
            Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
            failedLoadingPosts();
        }
        return null;
    }
}
