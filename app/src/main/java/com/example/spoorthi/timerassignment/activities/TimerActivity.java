package com.example.spoorthi.timerassignment.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spoorthi.timerassignment.R;
import com.example.spoorthi.timerassignment.service.CountdownTimerService;
import com.example.spoorthi.timerassignment.storage.LocalSharedPreferences;

import java.util.concurrent.TimeUnit;

import static com.example.spoorthi.timerassignment.keyconstants.KeyList.IS_PAUSED;
import static com.example.spoorthi.timerassignment.keyconstants.KeyList.IS_RESET;
import static com.example.spoorthi.timerassignment.keyconstants.KeyList.PAUSE_TIME;
import static com.example.spoorthi.timerassignment.keyconstants.KeyList.START_TIME;

public class TimerActivity extends AppCompatActivity {

    TextView textViewTime;
    private TimerStatusReceiver receiver;

    ImageView startBtn,stopBtn,pauseBtn,resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        textViewTime = (TextView) findViewById(R.id.status_tv);

        startBtn = (ImageView)findViewById(R.id.iv_start_btn);
        stopBtn = (ImageView)findViewById(R.id.iv_stop_btn);
        pauseBtn = (ImageView)findViewById(R.id.iv_pause_btn);
        resetBtn = (ImageView)findViewById(R.id.iv_restart_btn);

        stopBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
        resetBtn.setEnabled(false);

        LocalSharedPreferences localSharedPreferences = LocalSharedPreferences.getInstance(this);

        boolean isTimerPaused = localSharedPreferences.isTimerPaused(IS_PAUSED);

        if(isTimerPaused)
        {
            long remainingTime = localSharedPreferences.getPauseData(PAUSE_TIME);
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(remainingTime),
                    TimeUnit.MILLISECONDS.toMinutes(remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)),
                    TimeUnit.MILLISECONDS.toSeconds(remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime)));
            textViewTime.setText(hms);
        }

        receiver = new TimerStatusReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(CountdownTimerService.TIME_INFO));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public void startTimerService(View view) {

        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        pauseBtn.setEnabled(true);

        Intent intent = new Intent(this, CountdownTimerService.class);
        intent.putExtra(IS_PAUSED,false);
        intent.putExtra(START_TIME,30000);
        intent.putExtra(IS_RESET,false);
        startService(intent);
    }

    public void stopTimerService(View view) {

        startBtn.setEnabled(true);
        resetBtn.setEnabled(false);
        pauseBtn.setEnabled(false);

        Intent intent = new Intent(this, CountdownTimerService.class);
        stopService(intent);
    }

    public void pauseTimerService(View view)
    {
        resetBtn.setEnabled(true);
        Intent intent = new Intent(this, CountdownTimerService.class);
        intent.putExtra(IS_PAUSED,true);
        intent.putExtra(IS_RESET,false);
        startService(intent);
    }

    public void resetTimerService(View view)
    {
        startBtn.setEnabled(true);
        Intent intent = new Intent(this, CountdownTimerService.class);
        intent.putExtra(IS_PAUSED,false);
        intent.putExtra(IS_RESET,true);
        startService(intent);

        textViewTime.setText("00:00:30");
    }


    private class TimerStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(CountdownTimerService.TIME_INFO)) {
                if (intent.hasExtra("VALUE")) {
                    stopBtn.setEnabled(true);
                    pauseBtn.setEnabled(true);
                    textViewTime.setText(intent.getStringExtra("VALUE"));
                }
            }
        }
    }
}
