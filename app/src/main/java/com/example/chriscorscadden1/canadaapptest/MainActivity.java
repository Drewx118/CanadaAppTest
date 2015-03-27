package com.example.chriscorscadden1.canadaapptest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private CanadaFacts facts;
    private ListView listview;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FactFetcher fetcher = new FactFetcher();
        fetcher.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void failedLoadingFacts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Failed to load Facts. Have a look at LogCat.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private class FactFetcher extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "FactFetcher";
        private static final String SERVER_URL = "https://dl.dropboxusercontent.com/u/746330/facts.json";

        @Override
        protected Void doInBackground(Void... params) {
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
                        facts = gson.fromJson(reader, CanadaFacts.class);
                        content.close();
                    } catch (Exception ex) {
                        Log.e(TAG, "Failed to parse JSON due to: " + ex);
                        failedLoadingFacts();
                    }
                } else {
                    Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
                    failedLoadingFacts();
                }
            } catch(Exception ex) {
                Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
                failedLoadingFacts();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java

            adapter = new ListViewAdapter(MainActivity.this, facts);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
        }
    }
}
