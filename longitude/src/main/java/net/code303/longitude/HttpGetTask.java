package net.code303.longitude;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetTask extends AsyncTask<URL, Integer, byte[]> {

    private final Activity activity;

    public HttpGetTask(Activity originActivity) {
        activity = originActivity;
    }

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
        TextView textView = (TextView) activity.findViewById(R.id.textView);
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