package com.dowob.incubationhelper;

import android.content.Context;
import android.os.Vibrator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wei on 2017/10/1.
 */

public class VibratingController {
    private Context context;
    private Vibrator vibrator;
    private boolean isRunning = false;
    private Timer timer;

    public VibratingController(Context context) {
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                vibrator.vibrate(500);
            }
        },0, 1000);
    }

    public void stop() {
        if (!isRunning) return;
        if (timer != null) timer.cancel();
        isRunning = false;
    }
}
