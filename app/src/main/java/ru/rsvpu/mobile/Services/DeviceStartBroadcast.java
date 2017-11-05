package ru.rsvpu.mobile.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.rsvpu.mobile.Activity.TutorialActivity;

/**
 * Created by aleksej on 05.11.2017.
 */

public class DeviceStartBroadcast extends BroadcastReceiver {
    private final String LOG_ARGS ="DeviceStartBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_ARGS,"Device wakeup set Alarm Manager");
        TutorialActivity.setAlarm(context);
    }
}
