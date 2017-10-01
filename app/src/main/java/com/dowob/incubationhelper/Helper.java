package com.dowob.incubationhelper;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by wei on 2017/10/1.
 */

public class Helper {
    private static volatile Helper instance;

    public static Helper getInstance(Context context) {
        if (instance == null) {
            synchronized (Helper.class) {
                if (instance == null) {
                    instance = new Helper(context);
                }
            }
        }
        return instance;
    }

    private Context context;
    private MyLocationManager myLocationManager;
    private Location lastLocation;
    private List<OnUpdateListener> onUpdateListeners = new CopyOnWriteArrayList<>();
    private float distance;
    private long starTime;
    private float distanceThreshold = 700;
    private float durationThreshold = 210;
    private Timer timer;
    private boolean isRunning = false;
    private boolean isTimeGoal = false;
    private VibratingController vibratingController;

    private Helper(Context context) {
        this.context = context;
        this.vibratingController = new VibratingController(context);

        myLocationManager = new MyLocationManager(context);
        myLocationManager.setOnResultListener(locationResult -> {
            Location currentLocation = locationResult.getLastLocation();
            if (lastLocation == null) lastLocation = currentLocation;
            float delta = currentLocation.distanceTo(lastLocation);
            distance += delta;
            lastLocation = locationResult.getLastLocation();
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            String s = String.format("%s, %s", latitude, longitude);
        });
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;
        starTime = System.currentTimeMillis();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override public void run() { onUpdate(); }
        }, 0, 1000);
        myLocationManager.start();
        isTimeGoal = false;
    }

    public void stop() {
        if (!isRunning) stop();
        myLocationManager.stop();
        timer.cancel();
        vibratingController.stop();
        isRunning = false;
    }

    public float getDistance() {
        return distance;
    }

    public long getDuration() {
        if (isRunning) return System.currentTimeMillis() - starTime;
        else return 0;
    }

    public void addOnUpdateListener(OnUpdateListener onUpdateListener) {
        onUpdateListeners.add(onUpdateListener);
    }

    public void removeOnUpdateListener(OnUpdateListener onUpdateListener) {
        onUpdateListeners.remove(onUpdateListener);
    }

    public void setDistanceThreshold(float distanceThreshold) {
        this.distanceThreshold = distanceThreshold;
    }

    public void setDurationThreshold(float durationThreshold) {
        this.durationThreshold = durationThreshold;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void onUpdate() {

        long time = getDuration() / 1000;
        if (time > durationThreshold) { onTimeGoal(); }
        if (distance > distanceThreshold) { onDistanceGoal(); }

        for (OnUpdateListener onUpdateListener : onUpdateListeners) {
            onUpdateListener.onUpdate();
        }
    }

    private void onDistanceGoal() {
        vibratingController.start();
    }

    private void onTimeGoal() {
        if (isTimeGoal) return;
        isTimeGoal = true;
        vibratingController.start();
        String packageName = "com.nianticlabs.pokemongo";
        Intent intent = context
                .getPackageManager()
                .getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public interface OnUpdateListener {
        void onUpdate();
    }
}
