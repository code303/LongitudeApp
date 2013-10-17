package net.code303.longitude;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by toni on 17.10.13.
 */
public class HttpUpdatePositionTask extends AsyncTask<URL, Integer, String> {

    private final Activity activity;

    public HttpUpdatePositionTask(Activity activity) {
        this.activity = activity;
    }

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
        Toast.makeText(activity.getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    }

}
