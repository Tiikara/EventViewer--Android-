package com.example.eventviewer.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    private WebView webView;

    private String testJson = "{\n" +
            "    countEvents: 3,\n" +
            "    events:\n" +
            "    [\n" +
            "      [  48.4808300, 135.0927800, \"Это ходка!!!11\" ],\n" +
            "      [  49.4808300, 134.0927800, \"Это ходка2!!!11\" ],\n" +
            "      [  47.4808300, 134.0927800, \"Это ходка3!!!11\" ]\n" +
            "    ]\n" +
            "}";

    private DataEvent dataEvents[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

            int countEvents = jsonMain.getInt("countEvents");

            dataEvents = new DataEvent[countEvents];

            JSONArray eventsJson = jsonMain.getJSONArray("events");

            for(int i=0;i<countEvents;i++)
            {
                JSONArray eventJson = eventsJson.getJSONArray(i);

                dataEvents[i] = new DataEvent();
                dataEvents[i].x = eventJson.getDouble(0);
                dataEvents[i].y = eventJson.getDouble(1);
                dataEvents[i].eventDescr = eventJson.getString(2);
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
    }

    public void ShowDescr(int id)
    {
        Intent myIntent = new Intent(MainActivity.this, ActivityDescrEvent.class);
        myIntent.putExtra("descr", dataEvents[id].eventDescr); //Optional parameters
        MainActivity.this.startActivity(myIntent);
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
