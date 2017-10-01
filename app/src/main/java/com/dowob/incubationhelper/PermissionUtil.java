package com.dowob.incubationhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by wei on 2017/9/10.
 */

public class PermissionUtil {

    public static boolean hasGranted(Context context, String permission) {
        int check = ContextCompat.checkSelfPermission(context, permission);
        return check == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasGrantedAccessCoarseLocation(Context context) {
        return hasGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean hasGrantedAccessFineLocation(Context context) {
        return hasGranted(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean shouldShowRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public static boolean shouldShowAccessCoarseLocation(Activity activity) {
        return shouldShowRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean shouldShowAccessFineLocation(Activity activity) {
        return shouldShowRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static void requestAccessCoarseLocation(Activity activity, int requestCode) {
        requestPermissions(activity, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }

    public static void requestAccessFineLocation(Activity activity, int requestCode) {
        requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
    }
}
