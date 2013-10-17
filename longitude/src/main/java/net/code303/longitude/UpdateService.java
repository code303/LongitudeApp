package net.code303.longitude;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by toni on 06.10.13.
 */
public class UpdateService extends Service {
    // constant
    //public static final long NOTIFY_INTERVAL = 10 * 60 * 1000; // 10 minutes
    public static final long NOTIFY_INTERVAL = Config.getInstance().getUpdateInterval() *60*1000;//5 * 1000; // 5 sec

    // run on another Thread to avoid crash
    private Handler handler = new Handler();
    // timer handling
    private Timer timer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //public void  OnStartCommand(Intent intent, int flags, int startId) {
    @Override
    public void onCreate() {
        // cancel if already existed
        if(timer != null) {
            timer.cancel();
        } else {
            // recreate new
            timer = new Timer();
        }
        // schedule task
        timer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    public void updateServer(String userName, double latitude, double longitude) {
        //Toast.makeText(getApplicationContext(), "Updating...", Toast.LENGTH_SHORT).show();
        URL url = null;
        Config config = Config.getInstance();
        try {
            //url = new URL("http://www.google.de/");
            String query = userName + "%23" + latitude + "%23" + longitude;
            String server = config.getServer();
            int port = config.getPort();
            url = new URL(server+ ":" + port + "/set?" + query);
            //Toast.makeText(getApplicationContext(), "URL: " + url.toString(), Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpUpdatePositionTask task = new HttpUpdatePositionTask();
        task.execute(url);
    }


    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    //Toast.makeText(getApplicationContext(), getDateTime(), Toast.LENGTH_SHORT).show();

                    double lat = 51.0;
                    double lon = 12.0;
                    Location currentPosition = getCurrentPosition();
                    if (currentPosition != null) {
                        lat = currentPosition.getLatitude();
                        lon = currentPosition.getLongitude();
                    }

                    updateServer(Config.getInstance().getUser(), lat, lon);
                }

            });
        }

        public Location getCurrentPosition() {
            try {
                LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                List<String> matchingProviders = locationManager.getAllProviders();

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
                    return bestResult;
                } else {
                    return null;
                }
            } catch (Exception ex) {
                Log.e("LONGITUDE","Exception while getting current position.");
                return null;
            }
        }


        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

    }
}
