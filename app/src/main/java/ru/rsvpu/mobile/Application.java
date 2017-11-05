package ru.rsvpu.mobile;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import java.util.Calendar;
import java.util.TimeZone;

import ru.rsvpu.mobile.Activity.TutorialActivity;
import ru.rsvpu.mobile.Services.AlarmReceiver;

/**
 * Created by aleksej
 * on 02.11.2017.
 */

public class Application extends android.app.Application {

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(@Nullable VKAccessToken oldToken, @Nullable VKAccessToken newToken) {
            Log.d("Application", "VKTokenListener");

            if (newToken == null) {
                Intent intent = new Intent(Application.this, TutorialActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application", "onCreate");
//        setRecurringAlarm();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }

}
