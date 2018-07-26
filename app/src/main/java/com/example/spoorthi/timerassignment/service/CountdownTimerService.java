package com.example.spoorthi.timerassignment.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.spoorthi.timerassignment.R;
import com.example.spoorthi.timerassignment.activities.TimerActivity;
import com.example.spoorthi.timerassignment.storage.LocalSharedPreferences;

import java.util.concurrent.TimeUnit;

import static com.example.spoorthi.timerassignment.keyconstants.KeyList.IS_PAUSED;
import static com.example.spoorthi.timerassignment.keyconstants.KeyList.IS_RESET;
import static com.example.spoorthi.timerassignment.keyconstants.KeyList.PAUSE_TIME;
import static com.example.spoorthi.timerassignment.keyconstants.KeyList.START_TIME;

public class CountdownTimerService extends Service
{
    public static final String TIME_INFO = "time_info";

    private CounterClass timer;

    private String pauseVal = "";

    private long pauseTime;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean isPaused = intent.getBooleanExtra(IS_PAUSED,false);
        boolean isReset = intent.getBooleanExtra(IS_RESET,false);
        Log.e("isPaused ",""+isPaused);

        if(isPaused)
        {
            timer.cancel();
            LocalSharedPreferences localSharedPreferences = LocalSharedPreferences.getInstance(this);
            localSharedPreferences.savePauseData(PAUSE_TIME,pauseTime);
            localSharedPreferences.setTimerPaused(IS_PAUSED,true);

            updateNotification(pauseVal);

        }
        else if(isReset)
        {
            timer.cancel();

            Intent timerInfoIntent = new Intent(TIME_INFO);
            timerInfoIntent.putExtra("VALUE", "00:00:30");
            LocalBroadcastManager.getInstance(CountdownTimerService.this).sendBroadcast(timerInfoIntent);
        }
        else {
            long startTime = intent.getLongExtra(START_TIME,30000);

            Log.e("startTime ",""+startTime);

            timer = new CounterClass(30000, 1000);
            timer.start();

            updateNotification("Countdown started");
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();

        LocalSharedPreferences localSharedPreferences = LocalSharedPreferences.getInstance(this);
        localSharedPreferences.setTimerPaused(IS_PAUSED,false);

        Intent timerInfoIntent = new Intent(TIME_INFO);
        timerInfoIntent.putExtra("VALUE", "Stopped");
        LocalBroadcastManager.getInstance(CountdownTimerService.this).sendBroadcast(timerInfoIntent);
    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println(hms);
            pauseVal = hms;
            pauseTime = millis;
            updateNotification(hms);
            Intent timerInfoIntent = new Intent(TIME_INFO);
            timerInfoIntent.putExtra("VALUE", hms);
            LocalBroadcastManager.getInstance(CountdownTimerService.this).sendBroadcast(timerInfoIntent);
        }

        @Override
        public void onFinish() {
            Intent timerInfoIntent = new Intent(TIME_INFO);
            timerInfoIntent.putExtra("VALUE", "Completed");

            LocalSharedPreferences localSharedPreferences = LocalSharedPreferences.getInstance(getApplicationContext());
            localSharedPreferences.setTimerPaused(IS_PAUSED,false);

            LocalBroadcastManager.getInstance(CountdownTimerService.this).sendBroadcast(timerInfoIntent);
            //stopSelf();
        }
    }

    private void updateNotification(String hms)
    {
        Intent notificationIntent = new Intent(this, TimerActivity.class);
        @SuppressLint("WrongConstant")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(hms)
                .setContentIntent(pendingIntent).build();

        startForeground(101, notification);
    }
}
