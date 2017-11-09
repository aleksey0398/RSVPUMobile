package ru.rsvpu.mobile.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import ru.rsvpu.mobile.Activity.TutorialActivity;
import ru.rsvpu.mobile.items.var;

/**
 * Created by aleksej
 * on 05.11.2017.
 */

public class DeviceStartBroadcast extends BroadcastReceiver {
    private final String LOG_ARGS = "DeviceStartBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_ARGS, "Device wakeup set Alarm Manager");
        SharedPreferences settings = context.getSharedPreferences(var.SETTINGS_MAIN, Context.MODE_PRIVATE);
        if (settings.contains(var.SETTINGS_value)) {
            TutorialActivity.setAlarm(context, false);
            TutorialActivity.setAlarmPair(context, false);
        }
    }
}
