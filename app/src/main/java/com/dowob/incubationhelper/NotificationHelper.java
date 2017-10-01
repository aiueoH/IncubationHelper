package com.dowob.incubationhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;


/**
 * Created by wei on 2017/9/11.
 */

public class NotificationHelper {

    private Context context;
    private NotificationManager manager;
    private final int id = 0;

    public NotificationHelper(Context context) {
        this.context = context;
        this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void start() {
        update(0, 0);
    }

    public void update(long time, float distance) {
        String text = String.format("%ss, %sm", time, distance);
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_pool_black_24dp)
                .setContentTitle("Incubation")
                .setContentText(text)
                .setOngoing(true);
        manager.notify(id, builder.build());
    }

    public void stop() {
        manager.cancel(id);
    }
}
