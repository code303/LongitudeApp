package net.code303.longitude;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config = Config.getInstance();


        Intent intent = new Intent(this, UpdateService.class);
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onBtnSendRequestClicked(View view) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.append("Request sent.");

        URL url = null;
        try {
            //url = new URL("http://www.google.de/");
            //url = new URL("http://raspberrypi:8000/get");
            String urlString = ((EditText)findViewById(R.id.editText)).getText().toString() + "/get";
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpGetTask task = new HttpGetTask(this);
        task.execute(url);
    }

    public void onBtnGetCurrentPositionClicked(View view) {
        TextView textViewLat = (TextView) findViewById(R.id.textViewLatitude);
        textViewLat.append("Request sent.");
        TextView textViewLon = (TextView) findViewById(R.id.textViewLongitude);
        textViewLon.append("Request sent.");

        // get current Position
        double lat = 51.1234;
        double lon = 13.2222;

        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        List <String> matchingProviders = locationManager.getAllProviders();

        long minTime = new Date().getTime() - 120 * 1000;
        long bestTime = 0;
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;

        for (String provider: matchingProviders) {
           Location location = locationManager.getLastKnownLocation(provider);
            //Location location = locationManager.getLastKnownLocation("gps");
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy)) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                }
                else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime){
                    bestResult = location;
                    bestTime = time;
                }
            }
        }
        if (bestResult != null) {
            lat = bestResult.getLatitude();
            lon = bestResult.getLongitude();
        } else {
            lat = 0.0;
            lon = 0.0;
        }

        textViewLat.setText(String.valueOf(lat));
        textViewLon.setText(String.valueOf(lon));

    }

    public void onBtnGetUpdateServerClicked(View view) {
        TextView textViewLatitude = (TextView) findViewById(R.id.textViewLatitude);
        Double latitude = Double.parseDouble(textViewLatitude.getText().toString());
        TextView textViewLongitude = (TextView) findViewById(R.id.textViewLongitude);
        Double longitude = Double.parseDouble(textViewLongitude.getText().toString());

        updateServer("Toni", latitude, longitude);

    }

    public void updateServer(String userName, double latitude, double longitude) {
        Toast.makeText(this.getApplicationContext(), "Updating...", Toast.LENGTH_SHORT).show();
        URL url = null;
        try {
            //url = new URL("http://www.google.de/");
            String query = userName + "%23" + latitude + "%23" + longitude;
            //url = new URL("http://raspberrypi:8000/set?" + query);
            url = new URL(((EditText)findViewById(R.id.editText)).getText().toString() + "/set?" + query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //HttpUpdatePositionTask task = new HttpUpdatePositionTask(this);
        HttpUpdatePositionTask task = new HttpUpdatePositionTask();
        task.execute(url);
    }

    public void onBtnDisplayOnMapsClicked(View view) {
        TextView textViewLatitude = (TextView) findViewById(R.id.textViewLatitude);
        Double latitude = Double.parseDouble(textViewLatitude.getText().toString());
        TextView textViewLongitude = (TextView) findViewById(R.id.textViewLongitude);
        Double longitude = Double.parseDouble(textViewLongitude.getText().toString());
        displayOnMaps("Toni",latitude,longitude);
    }

    // Display on map
    private void displayOnMaps(String userName, double latitude, double longitude) {
        try {
        String uri = "geo:"+ latitude + "," + longitude;
        startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
        }
        catch (Exception ex){Toast.makeText(getApplicationContext(), "Ups - Maps nicht verfügbar", Toast.LENGTH_SHORT).show();}
    }
}