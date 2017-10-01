package com.dowob.incubationhelper;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_INTERVAL = 5 * 1000;
    private static final int RC_REQUEST_COASIR = 1000;
    private static final int RC_REQUEST_FINE_LOCATION = 1001;

    private NotificationHelper notificationHelper;
    private Button startBtn;
    private Button stopBtn;
    private EditText distanceEditText;
    private EditText durationEditText;
    private Helper helper;
    private Helper.OnUpdateListener onUpdateListener = this::onHelperUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.btn_main_start);
        stopBtn = findViewById(R.id.btn_main_stop);
        durationEditText = findViewById(R.id.editText_main_duration);
        distanceEditText = findViewById(R.id.editText_main_distance);
        startBtn.setOnClickListener(this::onClickStart);
        stopBtn.setOnClickListener(this::onClickStop);

        helper = Helper.getInstance(getApplication());
        helper.addOnUpdateListener(onUpdateListener);
        updateView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.removeOnUpdateListener(onUpdateListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_REQUEST_COASIR) {
            if (PermissionUtil.hasGrantedAccessCoarseLocation(this)) return;
            if (!PermissionUtil.shouldShowAccessCoarseLocation(this)) return;
            alert("Please grant COARSE_LOCATION permission in app's setting.");
        } else if (requestCode == RC_REQUEST_FINE_LOCATION) {
            if (PermissionUtil.hasGrantedAccessFineLocation(this)) return;
            if (!PermissionUtil.shouldShowAccessFineLocation(this)) return;
            alert("Please grant FINE_LOCATION permission in app's setting.");
        }
    }

    private void onHelperUpdate() {
        updateView();
        updateNotification();
    }

    private void updateView() {
        runOnUiThread(() -> {
            startBtn.setEnabled(!helper.isRunning());
            stopBtn.setEnabled(helper.isRunning());
            distanceEditText.setEnabled(!helper.isRunning());
            durationEditText.setEnabled(!helper.isRunning());
        });
    }

    private void updateNotification() {
        if (notificationHelper == null) return;
        long time = helper.getDuration() / 1000;
        float distance = helper.getDistance();
        notificationHelper.update(time, distance);

        String s = String.format("%s, %s", time, distance);
        log(s);
    }

    private void onClickStart(View v) {
        if (helper.isRunning()) return;
        notificationHelper = new NotificationHelper(this);
        long durationThreshold = Long.parseLong(durationEditText.getText().toString());
        float distanceThreshold = Long.parseLong(distanceEditText.getText().toString());
        helper.setDistanceThreshold(distanceThreshold);
        helper.setDurationThreshold(durationThreshold);
        helper.start();
        updateView();
    }

    private void onClickStop(View v) {
        if (!helper.isRunning()) return;
        helper.stop();
        notificationHelper.stop();
        updateView();
    }

    private void log(String s) {
        String tag = this.getClass().getSimpleName();
        Log.d(tag, s);
    }

    private void alert(String s) {
        new AlertDialog.Builder(this)
                .setMessage(s)
                .show();
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
