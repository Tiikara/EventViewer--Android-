package com.example.eventviewer.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MainActivity extends ActionBarActivity {

    private WebView webView;

    private String testJson = "{\n" +
            "    events:\n" +
            "    [\n" +
            "      [  \"г.Хабаровск, ул. Ленина, 20\", \"Это ходка!!!11\" ],\n" +
            "      [  \"г.Хабаровск, ул. Истомина, 42\", \"Это ходка2!!!11\" ],\n" +
            "      [  \"г.Хабаровск, пер. Дзержинского, 21\", \"Это ходка3!!!11\" ]\n" +
            "    ]\n" +
            "}";

    private DataEvent dataEvents[];
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new SplashScreenFragment();

        fragmentTransaction.add(R.id.splash_screen_content, fragment);
        fragmentTransaction.commit();

        setupWebView();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setupWebView(){
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(this, "Android");
        webView.loadUrl("file:///android_asset/map.html");
    }

    public void onPageLoaded()
    {
        try {
            JSONObject jsonMain = new JSONObject(testJson);

            JSONArray eventsJson = jsonMain.getJSONArray("events");

            dataEvents = new DataEvent[eventsJson.length()];

            for(int i=0;i<eventsJson.length();i++)
            {
                dataEvents[i] = new DataEvent();

                JSONArray eventJson = eventsJson.getJSONArray(i);

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address="+ eventJson.getString(0).replace(" ", "%20") +"&sensor=false");
                try {
                    HttpResponse response = httpClient.execute(httpPost);

                    String line = "";
                    String recieveMsg = "";

                    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(response.getEntity().getContent()) );

                    while((line = bufferedReader.readLine()) != null)
                        recieveMsg += line;

                    JSONObject jsonGeo = new JSONObject(recieveMsg);

                    JSONObject jsonLocation = jsonGeo.getJSONArray("results").getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location");

                    dataEvents[i].x = jsonLocation.getDouble("lat");
                    dataEvents[i].y = jsonLocation.getDouble("lng");
                } catch (Exception e) {}

                dataEvents[i].eventDescr = eventJson.getString(1);
            }
        } catch (JSONException e) {
            System.out.println(e.toString());
        }

        String query = "";

        for(int i=0;i<dataEvents.length;i++)
        {
            query += "addMarker("+ Double.toString(dataEvents[i].x) +","+ Double.toString(dataEvents[i].y) +", "+ Integer.toString(i) +");";
        }

        webView.loadUrl("javascript:" + query);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_right,R.animator.slide_in_right);
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    public void ShowDescr(int id)
    {
        Intent intentDescr = new Intent(MainActivity.this, ActivityDescrEvent.class);
        intentDescr.putExtra("descr", dataEvents[id].eventDescr);
        MainActivity.this.startActivity(intentDescr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

}
