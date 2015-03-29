package com.example.chriscorscadden1.canadaapptest;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private CanadaFacts facts;
    private ListView listview;
    private ListViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the content view of the MainActivity to activity_main.xml
        setContentView(R.layout.activity_main);
        setTitle("");
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
        private ExecutorService executorService;

        @Override
        protected Void doInBackground(Void... params) {
            downloadJson();
            return null;
        }

        // Creates a HTTP client that uses post to download JSON data from SERVER_URL which we parse
        // into a CanadaFacts object.
        private void downloadJson(){
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
                    Log.e(TAG, "Successful status code of 200 at url: " + SERVER_URL);
                    try {
                        //Read the server response and attempt to parse it as JSON
                        Reader reader = new InputStreamReader(content);
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        // Creates CanadaFacts object from the JSON
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
                downloadJson();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(facts != null) {
                // Locates the listview in activity_main.xml
                listview = (ListView) findViewById(R.id.listview);
                // Removes invalid facts from facts
                facts.RemoveInvalidFacts();
                // Sets MainActivity title to facts title
                setTitle(facts.getTitle());
                // Pass the results into ListViewAdapter.java
                adapter = new ListViewAdapter(MainActivity.this, facts);
                // Set the adapter to the ListView
                listview.setAdapter(adapter);
                // Locates the swipe_refresh_layout in activity_main.xml
                swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
                // Creates setOnRefreshListener event listener for swipeRefreshLayout
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                    // Downloads JSON from URL_SERVER again and resets the listview adapter with the
                    // new object after a swipe refresh event
                    @Override
                    public void onRefresh() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                executorService = Executors.newFixedThreadPool(5);
                                executorService.submit(new RedownloadJSON());
                                adapter = new ListViewAdapter(MainActivity.this, facts);
                                listview.setAdapter(adapter);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }, 2000);
                    }
                });
            }
        }

        // Used to redownload JSON file in a new thread
        class RedownloadJSON implements Runnable {

            @Override
            public void run() {
                downloadJson();
            }
        }
    }
}






















