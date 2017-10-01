package com.dowob.incubationhelper;

import android.content.Context;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

/**
 * Created by wei on 2017/9/10.
 */

public class MyLocationManager {
    private Context context;
    private FusedLocationProviderClient client;
    private LocationRequest request ;
    private LocationCallback callback;
    private OnResultListener onResultListener;
    private Task<Void> task;
    private int requestInterval = 3 * 1000;
    private boolean isRunning = false;

    public MyLocationManager(Context context) {
        this.context = context;
        init();
    }

    public MyLocationManager(Context context, int requestInterval) {
        this.context = context;
        this.requestInterval = requestInterval;
    }

    private void init() {
        client = LocationServices.getFusedLocationProviderClient(context);
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (onResultListener == null) return;
                onResultListener.onResult(locationResult);
            }
        };
        request = new LocationRequest();
        request.setInterval(requestInterval);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }

    public void start() {
        if (isRunning) return;
        isRunning = true;
        task = register();
    }

    public void stop() {
        if (!isRunning) return;
        isRunning = false;
        task = null;
        client.removeLocationUpdates(callback);
    }

    public boolean isRunning() {
        return isRunning;
    }

    private Task<Void> register() {
        return client.requestLocationUpdates(request, callback, Looper.myLooper());
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    public interface OnResultListener {
        void onResult(LocationResult locationResult);
    }
}
