package com.chrumck.pjatk.myshoppinglist;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ShopGeofenceService extends IntentService {
    private static final String TAG = "ShopGeofenceService";
    private static final String CHANNEL_ID = "ShopGeofenceServiceChannelId";
    private static final int NOTIFICATION_ID = 1;

    public ShopGeofenceService() {
        super("ShopGeofenceService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, Integer.toString(geofencingEvent.getErrorCode()));
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            String title = getTransitionString(geofenceTransition);

            List<String> shopIds = geofencingEvent.getTriggeringGeofences().stream()
                    .map(Geofence::getRequestId).collect(Collectors.toList());

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(TextUtils.join(", ", shopIds))
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            notificationManager.notify(NOTIFICATION_ID, notification);

        } else {
            Log.e(TAG, "Invalid transition type: " + geofenceTransition);
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered shops:";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited shops:";
            default:
                throw new IllegalArgumentException();
        }
    }
}
