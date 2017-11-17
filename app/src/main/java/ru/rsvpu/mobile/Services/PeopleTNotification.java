package ru.rsvpu.mobile.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ru.rsvpu.mobile.Activity.PeopleTActivity;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.SettingsHelper;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by aleksej
 * on 09.11.2017.
 */

public class PeopleTNotification extends BroadcastReceiver {
    private int id = 0;
    private final String LOG_ARGS = "PeopleTBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_ARGS, "onReceiver People-T");

        sendNotification(context);


    }


    private void sendNotification(Context context) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent repeatingIntent = new Intent(context, PeopleTActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(context, 100, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        SettingsHelper helper = new SettingsHelper(context);

        String NOTIFICATION_CHANNEL_ID = "people_t_notification";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setContentTitle(helper.getPeopleTTitle())
                .setSmallIcon(R.drawable.ic_notification_people_t_48)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher, null))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText("студенческий театр Люди-Т")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(helper.getPeopleTMessage()));

        if (notificationManager != null) {
            notificationManager.notify(id++, notification.build());
        }
    }
}
