package ru.rsvpu.mobile.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.rsvpu.mobile.MainActivity;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.DateUtil;
import ru.rsvpu.mobile.items.GetTimeTableForNotif;
import ru.rsvpu.mobile.items.SettingsHelper;
import ru.rsvpu.mobile.items.TimeTableOneDay;
import ru.rsvpu.mobile.items.TimeTableOneLesson;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by aleksej
 * on 07.11.2017.
 */

public class DayAlarmReceiver extends BroadcastReceiver {
    int currentPair;
    List<TimeTableOneDay> timeTableOneDays = new ArrayList<>();
    TimeTableOneLesson nextLesson = null;
    String LOG_ARGS;
    long timeStart;
    int hours, minutes;
    int id = Integer.MAX_VALUE - 1 - currentPair;
    private final String NOTIFICATION_CHANNEL_ID = "my_notification_channel_" + id;

    public static final int[] minutes12 = {35 - 5, 20 - 5, 35 - 5, 20 - 5, 0, 50 - 5};
    public static final int[] hours12 = {9, 11, 13, 15, 17, 18};

    public static final int[] minutes345 = {35 - 5, 20 - 5, 5 - 5, 20 - 5, 0, 50 - 5};
    public static final int[] hours345 = {9, 11, 13, 15, 17, 18};

    void setPair() {
        currentPair = 1;
    }

    void setLogArgs() {
        LOG_ARGS = "Pair 1 receiver";
    }

    private void setHoursMinutes(Context context) {
        SettingsHelper helper = new SettingsHelper(context);
        int course = helper.getGroupCourse();
        if (course == 1 || course == 2) {
            minutes = minutes12[currentPair - 1];
            hours = hours12[currentPair - 1];
        } else {
            minutes = minutes345[currentPair - 1];
            hours = hours345[currentPair - 1];
        }

    }

    private void setTimeStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        timeStart = calendar.getTimeInMillis();
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        setPair();
        setLogArgs();
        Log.d(LOG_ARGS, LOG_ARGS);
        setHoursMinutes(context);
        setTimeStart();
        Log.d(LOG_ARGS, "Time: " + String.valueOf((System.currentTimeMillis() - timeStart)));
        Log.d(LOG_ARGS, "15 min: " + String.valueOf((1000 * 60 * 15)));
        Log.d(LOG_ARGS, "15 is over? : " + String.valueOf((System.currentTimeMillis() - timeStart) > (1000 * 60 * 15)));

        if ((System.currentTimeMillis() - timeStart) < (1000 * 60 * 15)) {
            try {
                timeTableOneDays = new GetTimeTableForNotif().execute(context).get();
                for (TimeTableOneDay oneDay : timeTableOneDays) {
                    if (oneDay.date.equals(DateUtil.generateToday())) {
                        nextLesson = oneDay.lessons[currentPair];
                        break;
                    }
                }
                String titleForMessage = generateTitle();
                if (titleForMessage != null) {
                    sendNotification(context, titleForMessage);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendNotification(Context context, String title) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent repeatingIntent = new Intent(context, MainActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(context, 100, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        SettingsHelper helper = new SettingsHelper(context);

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
                .setContentTitle("Следующая пара в " + nextLesson.timeStart)
                .setSmallIcon(R.drawable.ic_next_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher, null))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(new SettingsHelper(context).getSettings().getName())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title));

        if (notificationManager != null && title != null) {
            notificationManager.notify(id, notification.build());
        }
    }

    private String generateTitle() {
        String titleForMessage;
        if (nextLesson.lessonsName.size() == 1) {
            if (nextLesson.lessonsName.get(0).equals("-")) {
                return null;
            } else {
                titleForMessage = nextLesson.typeOfLesson.get(0) + "\n";
                titleForMessage += nextLesson.lessonsName.get(0) + "\nКабинет: " + nextLesson.classrooms.get(0).name;
                if (nextLesson.numberOfGroup.get(0) != null) {
                    titleForMessage += " " + nextLesson.numberOfGroup.get(0);
                }
                titleForMessage += "\n" + nextLesson.teachers.get(0).name;
            }
        } else {
            titleForMessage = nextLesson.typeOfLesson.get(0) + "\n";
            titleForMessage += nextLesson.lessonsName.get(0) + "\nКабинет: " + nextLesson.classrooms.get(0).name;
            titleForMessage += " у " + nextLesson.numberOfGroup.get(0);
            titleForMessage += "\n" + nextLesson.teachers.get(0).name + "\n";

            titleForMessage += nextLesson.typeOfLesson.get(1) + "\n";
            titleForMessage += nextLesson.lessonsName.get(1) + "\nКабинет: " + nextLesson.classrooms.get(1).name;
            titleForMessage += " у " + nextLesson.numberOfGroup.get(1) + "\n";
            titleForMessage += "\n" + nextLesson.teachers.get(1).name;
        }

        return titleForMessage;
    }


}
