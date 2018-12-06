package ru.rsvpu.mobile.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import ru.rsvpu.mobile.activities.PeopleTActivity;
import ru.rsvpu.mobile.activities.TutorialActivity;
import ru.rsvpu.mobile.items.Var;

/**
 * Created by aleksej
 * on 05.11.2017.
 */

public class DeviceStartBroadcast extends BroadcastReceiver {
    private final String LOG_ARGS = "DeviceStartBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_ARGS, "Device wakeup set Alarm Manager");
        SharedPreferences settings = context.getSharedPreferences(Var.SETTINGS_MAIN, Context.MODE_PRIVATE);
        if (settings.contains(Var.SETTINGS_value)) {
            TutorialActivity.setAlarm(context, false);
            TutorialActivity.setAlarmPair(context, false);
        }
        PeopleTActivity.getDate(context);
    }
}
