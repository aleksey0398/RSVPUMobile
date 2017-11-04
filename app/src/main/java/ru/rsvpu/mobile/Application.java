package ru.rsvpu.mobile;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import ru.rsvpu.mobile.Activity.TutorialActivity;

/**
 * Created by aleksej on 02.11.2017.
 *
 */

public class Application extends android.app.Application {

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(@Nullable VKAccessToken oldToken, @Nullable VKAccessToken newToken) {
            Log.d("Application","VKTokenListener");

            if(newToken == null){
                Intent intent = new Intent(Application.this, TutorialActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application","onCreate");
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}
