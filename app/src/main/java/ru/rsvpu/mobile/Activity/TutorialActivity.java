package ru.rsvpu.mobile.Activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.Calendar;

import me.relex.circleindicator.CircleIndicator;
import ru.rsvpu.mobile.Fragments.FragmentPage;
import ru.rsvpu.mobile.MainActivity;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.CustomView.PresentationViewPager;
import ru.rsvpu.mobile.Services.DayAlarmReceiver;
import ru.rsvpu.mobile.Services.DayAlarmReceiver2;
import ru.rsvpu.mobile.Services.DayAlarmReceiver3;
import ru.rsvpu.mobile.Services.DayAlarmReceiver4;
import ru.rsvpu.mobile.Services.DayAlarmReceiver5;
import ru.rsvpu.mobile.Services.EveningAlarmReceiver;
import ru.rsvpu.mobile.items.SettingsHelper;

import static java.lang.Thread.sleep;

/**
 * Created by aleksej on 02.11.2017
 */

public class TutorialActivity extends AppCompatActivity {

    PresentationViewPager viewPager;
    PagerAdapter pagerAdapter;
    CircleIndicator indicator;
    TextView btn_back, btn_forward, titleTutorial;

    static final int COUNT_PAGE = 5;
    private final String LOG_ARGS = "TutorialActivity";
    public static boolean showFirst = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        initView();
        titleTutorial.setAlpha(0f);
        titleTutorial.setTranslationY(200);
        titleTutorial.animate().setStartDelay(200).alpha(1f).translationY(0).setDuration(2000).start();
//        vk_btn.setOnClickListener(v -> {
//            VKSdk.login(this, sMyScope);
//        });

        new Thread(() -> {
            try {
                sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {

                viewPager.setCurrentItem(1, true);
            });
        }).start();
        btn_back.setOnClickListener(v -> {
            viewPager.setDurationScroll(PresentationViewPager.PRESENTATION_MODE_SCROLL_DURATION);
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        });

        btn_forward.setOnClickListener(v -> {
            viewPager.setDurationScroll(PresentationViewPager.PRESENTATION_MODE_SCROLL_DURATION);
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        });

        viewPager.setOnTouchListener((v, event) -> {
            viewPager.setDurationScroll(PresentationViewPager.DEFAULT_SCROLL_DURATION);
            return false;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(LOG_ARGS, "onPageSelected: " + String.valueOf(position));
                switch (position) {

                    case 0:
                        btn_back.setVisibility(View.INVISIBLE);
                        break;
                    case COUNT_PAGE - 1:
                        btn_forward.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        btn_back.setVisibility(View.VISIBLE);
                        btn_forward.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    void initView() {
//        vk_btn = findViewById(R.id.activity_tutorial_vk_btn);
        btn_back = findViewById(R.id.activity_tutorial_text_back);
        btn_forward = findViewById(R.id.activity_tutorial_text_forward);
        btn_back.setVisibility(View.INVISIBLE);

        viewPager = findViewById(R.id.activity_viewPager_slide);
        viewPager.setDurationScroll(PresentationViewPager.PRESENTATION_MODE_SCROLL_DURATION);

        indicator = findViewById(R.id.activity_tutorial_circle);
        pagerAdapter = new myFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);
        pagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());
        titleTutorial = findViewById(R.id.activity_tutorial_title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                Log.d(LOG_ARGS, String.valueOf(res));
                startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("firstStart", 1));
                finish();
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization

                Log.e(LOG_ARGS, String.valueOf(error.errorMessage));
                Log.e(LOG_ARGS, String.valueOf(error.errorReason));
                Log.e(LOG_ARGS, String.valueOf(error));
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        Intent intent = new Intent(context, EveningAlarmReceiver.class);

        PendingIntent pIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarm != null) {
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);
        }
    }

    public static void setAlarmPair(Context context) {

        Intent intents[] = {new Intent(context, DayAlarmReceiver.class),
                new Intent(context, DayAlarmReceiver2.class),
                new Intent(context, DayAlarmReceiver3.class),
                new Intent(context, DayAlarmReceiver4.class),
                new Intent(context, DayAlarmReceiver5.class)
        };

        PendingIntent pIntents[] = {
                PendingIntent.getBroadcast(context, 100, intents[0], PendingIntent.FLAG_UPDATE_CURRENT),
                PendingIntent.getBroadcast(context, 100, intents[1], PendingIntent.FLAG_UPDATE_CURRENT),
                PendingIntent.getBroadcast(context, 100, intents[2], PendingIntent.FLAG_UPDATE_CURRENT),
                PendingIntent.getBroadcast(context, 100, intents[3], PendingIntent.FLAG_UPDATE_CURRENT),
                PendingIntent.getBroadcast(context, 100, intents[4], PendingIntent.FLAG_UPDATE_CURRENT)
        };

        Calendar calendar = Calendar.getInstance();
        SettingsHelper helper = new SettingsHelper(context);
        for (int i = 0; i < 5; i++) {

            if (helper.getGroupCourse() == 1 || helper.getGroupCourse() == 2) {
                calendar.set(Calendar.HOUR_OF_DAY, DayAlarmReceiver.hours12[i]);
                calendar.set(Calendar.MINUTE, DayAlarmReceiver.minutes12[i]);
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, DayAlarmReceiver.hours345[i]);
                calendar.set(Calendar.MINUTE, DayAlarmReceiver.minutes345[i]);
            }

            AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (alarm != null) {
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntents[i]);
            }
        }

    }

    class myFragmentPageAdapter extends FragmentPagerAdapter {

        myFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentPage.newInstance(position);
        }

        @Override
        public int getCount() {
            return COUNT_PAGE;
        }
    }
}
