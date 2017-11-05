package ru.rsvpu.mobile.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.rsvpu.mobile.MainActivity;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.Container;
import ru.rsvpu.mobile.items.DateUtil;
import ru.rsvpu.mobile.items.SettingsHelper;
import ru.rsvpu.mobile.items.TimeTableOneDay;
import ru.rsvpu.mobile.items.TimeTableOneLesson;
import ru.rsvpu.mobile.items.myNetwork;
import ru.rsvpu.mobile.items.var;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by aleksej
 * on 05.11.2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    final String LOG_ARGS = "AlarmReceiver";
    static int id = 0;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    String tomorrow = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_ARGS, "onReceiver");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent repeatingIntent = new Intent(context, MainActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(context, 100, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = null;
        try {
            SettingsHelper helper = new SettingsHelper(context);
            if (helper.getSettings().getAttr().equals("nothing")) {
                title = "Чтобы получать уведомления о занятии на следущий день, зайдите в настройки и выберите группу, преподавателя или аудиторию";
            } else {
                title = new GetTimeTable().execute(context).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setContentTitle("Расписание на завтра " + tomorrow)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(new SettingsHelper(context).getSettings().getName())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title));

        if (notificationManager != null && title != null) {
            notificationManager.notify(id++, notification.build());
        }
    }


    @SuppressLint("StaticFieldLeak")
    class GetTimeTable extends AsyncTask<Context, Void, String> {
        String resultString;
        boolean connection = false;
        boolean network = false;
        boolean urlValid = false;
        Context context;

        @Override
        protected String doInBackground(Context... contexts) {
//            Log.d(LOG_ARGS, "doInBackground");
            context = contexts[0];
            network = myNetwork.isWorking(contexts[0]);
            if (network) {
                urlValid = myNetwork.checkURL();
            }

            if (network && urlValid) {
                try {
                    URL url = new URL(generateRequest(context));
                    resultString = getJson(url);
                    connection = true;
//                System.out.println(resultString);
                } catch (Exception e) {
                    e.printStackTrace();
                    connection = false;
                }
            } else {
                connection = false;
            }
            List<TimeTableOneDay> list = new ArrayList<>();
            if (connection) {
                new SettingsHelper(context).saveTimeTable(resultString);
                list = generateListFromJson(resultString);
            }
            if (!network) {
                resultString = new SettingsHelper(context).getLastSavedTimeTable();
                if (resultString.equals(SettingsHelper.NO_SAVED_TIME_TABLE)) {
                    //nothing
                    list = null;
                } else {
                    list = generateListFromJson(resultString);
                }
            } else {
                if (!urlValid) {
//                    Toast.makeText(getActivity(), "Сервер недоступен, загружаем последнее сохранённое расписание", Toast.LENGTH_SHORT).show();
                    resultString = new SettingsHelper(context).getLastSavedTimeTable();
                    if (resultString.equals(SettingsHelper.NO_SAVED_TIME_TABLE)) {
//                        Toast.makeText(getActivity(), "Сохранённого расписания не найдено", Toast.LENGTH_SHORT).show();
                        list = null;
                    } else {
                        list = generateListFromJson(resultString);
                    }
                }
            }

            return generateTitleNotification(list);
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
        }
    }

    String generateTitleNotification(List<TimeTableOneDay> timeTableOneDays) {

        if (timeTableOneDays == null) {
            return null;
        }

        String title = "";
        TimeTableOneDay nextOneDay = null;
        int i = 0;
        for (TimeTableOneDay oneDay : timeTableOneDays) {
            if (oneDay.date.equals(DateUtil.generateToday())) {
                nextOneDay = timeTableOneDays.get(++i);
                break;
            }
            i++;
        }
        if (nextOneDay == null) {
            return null;
        }

        tomorrow = nextOneDay.date;

        boolean holiday = true;

        for (TimeTableOneLesson oneLesson : nextOneDay.lessons) {
            if (!oneLesson.lessonsName.get(0).equals("-")) {
                holiday = false;
            }
        }
        if (holiday) {
            return "У тебя завтра выходной!";
        }

        int currentPair = 0;
        for (TimeTableOneLesson oneLesson : nextOneDay.lessons) {
            currentPair++;
            if (oneLesson.lessonsName.size() == 1) {
                title += oneLesson.timeStart + ".";
                title += currentPair + "." + oneLesson.lessonsName.get(0) + "\n\n";
            } else {
                title += oneLesson.timeStart + ".";
                title += currentPair + "." + oneLesson.lessonsName.get(0) + "\n";
                title += "\t" + oneLesson.lessonsName.get(0) + "\n\n";
            }
        }

        return title;
    }

    String generateRequest(Context context) {
        String url = var.url;

        SettingsHelper helper = new SettingsHelper(context);
        Container saved_container = helper.getSettings();
        String value = saved_container.getValue();
        int typeGroup = helper.getTypeOfGroup();
        switch (saved_container.getAttr()) {
            case "gr":
                url += "v_gru=" + value + "&";
                break;
            case "prep":
                url += "v_prep=" + value + "&";
                break;
            case "aud":
                url += "v_aud=" + value + "&";
                break;
        }
        url += "v_date=" + DateUtil.generateToday() + "&";
        url += "type=" + (typeGroup == 0 ? "ochnoe" : "zaochnoe");
        Log.d("Generate Request", url);
        return url;
    }

    String getJson(URL url) throws IOException {
        HttpURLConnection urlConnection;
        BufferedReader reader;

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    List<TimeTableOneDay> generateListFromJson(String resultString) {
        Type listOfTimeTable = new TypeToken<List<TimeTableOneDay>>() {
        }.getType();
        return new Gson().fromJson(resultString, listOfTimeTable);
    }

}
