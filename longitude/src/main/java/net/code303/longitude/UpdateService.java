package net.code303.longitude;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by toni on 06.10.13.
 */
public class UpdateService extends Service {
    // constant
    //public static final long NOTIFY_INTERVAL = 10 * 60 * 1000; // 10 minutes
    public static final long NOTIFY_INTERVAL = 5 * 1000; // 5 sec

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

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    Toast.makeText(getApplicationContext(), getDateTime(),
                            Toast.LENGTH_SHORT).show();

                    //MainActivity.updateServer("Toni", latitude, longitude);
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

    }
}
