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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startService(new Intent(this, UpdateService.class));
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


        (new HttpGetTask()).execute(url);



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

        (new HttpUpdatePositionTask()).execute(url);
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
        catch (Exception ex){Toast.makeText(getApplicationContext(), "oohhh", Toast.LENGTH_SHORT).show();}
    }

    private class HttpGetTask extends AsyncTask<URL, Integer, byte[]> {

        @Override
        protected byte[] doInBackground(URL... urls) {
            byte[] rawBytes = new byte[]{};
            try {
                URL url = null;
                //url = new URL("http://raspberrypi:8000/get");
                url = urls[0];

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream content = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    int nextByte;
                    while((nextByte = content.read()) != -1){
                        outputStream.write(nextByte);
                    }
                    rawBytes = outputStream.toByteArray();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                finally {
                    urlConnection.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return rawBytes;
        }
        @Override
        protected void onPostExecute(byte[] responseBytes) {
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.getEditableText().clear();

            String clearTextResponse = Codec.decr(responseBytes);
            try {
                String[] res = clearTextResponse.split("&");
                for(String personData : res){
                    textView.append(personData.replace('#',' ') + System.getProperty("line.separator"));
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

        }
    }

    private class HttpUpdatePositionTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String result ="";
            try {
                URL url = null;
                //url = new URL("http://raspberrypi:8000/get");
                url = urls[0];

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream content = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        result += s;
                    }
                }
                catch (Exception ex) {
                    String exString = ex.getMessage();
                    ex.printStackTrace();
                }
                finally {
                    urlConnection.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText( getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

    }

}
/*
try {
            //URL url = null;
            url = new URL("http://www.android.com/");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                readStream(in);
            }
            catch (Exception ex) {
                String exString = ex.getMessage();
                ex.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
 */