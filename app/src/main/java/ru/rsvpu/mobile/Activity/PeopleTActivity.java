package ru.rsvpu.mobile.Activity;

import android.animation.Animator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import ru.rsvpu.mobile.CustomView.PresentationViewPager;
import ru.rsvpu.mobile.Fragments.FragmentPagePeopleT;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.Services.EveningAlarmReceiver;
import ru.rsvpu.mobile.Services.PeopleTNotification;
import ru.rsvpu.mobile.items.ItemNewsPeopleT;
import ru.rsvpu.mobile.items.MyNetwork;
import ru.rsvpu.mobile.items.SettingsHelper;
import ru.rsvpu.mobile.items.TabletHelper;
import ru.rsvpu.mobile.items.var;

public class PeopleTActivity extends AppCompatActivity {

    PresentationViewPager viewPager;
    private int COUNT_PAGE = 0;

    ProgressBar progressBar;

    public static LinkedList<ItemNewsPeopleT> newsList = new LinkedList<>();
    static FirebaseDatabase firebaseDatabase;
    static DatabaseReference mRef;
    private String LOG_ARGS = "Tutorial_Activity";

    public static void getDate(Context context) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();
        if (MyNetwork.isWorking(context))
            mRef.child("NewsPeopleT").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newsList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.getValue(ItemNewsPeopleT.class).isVisible()) {
                            newsList.addFirst(data.getValue(ItemNewsPeopleT.class));
                        }
                    }

                    //set alarm
                    if (newsList.size() == 0)
                        return;

                    ItemNewsPeopleT item = newsList.get(0);

                    if (new SettingsHelper(context).getCheckedPeople()) {
                        setAlarm(context,
                                item.getNotification1(),
                                item.getNotification2(),
                                item.getNotificationMessage(),
                                item.getNotificationTitle(),
                                false);
                    }

                    new SettingsHelper(context)
                            .savePeopleTDataForNotification(item.getNotification1(),
                                    item.getNotification2(),
                                    item.getNotificationTitle(),
                                    item.getNotificationMessage());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        else if (new SettingsHelper(context).checkContains(var.SETTINGS_alarm_people_date1)) {
            SettingsHelper settingsHelper = new SettingsHelper(context);
            if (settingsHelper.getCheckedPeople())
                setAlarm(context,
                        settingsHelper.getPeopleDate1(),
                        settingsHelper.getPeopleDate2(),
                        settingsHelper.getPeopleTMessage(),
                        settingsHelper.getPeopleTTitle(),
                        false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_t);
        initView();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        mRef.child("NewsPeopleT").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newsList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getValue(ItemNewsPeopleT.class).isVisible())
                        newsList.addFirst(data.getValue(ItemNewsPeopleT.class));
                }

                COUNT_PAGE = newsList.size();
                viewPager.setAdapter(new viewPagerAdapter(getSupportFragmentManager()));
                progressBar.setVisibility(View.GONE);
                viewPager.setCurrentItem(newsList.size() - 1);
                viewPager.setY(+200f);
                viewPager.animate().alpha(1f).y(0f).setDuration(1000).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        viewPager.setDurationScroll(500);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewPager.clearAnimation();
                        viewPager.setCurrentItem(0, true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();

                //set alarm
                if(newsList.size() == 0)
                    return;

                ItemNewsPeopleT item = newsList.get(0);

                if (new SettingsHelper(getApplicationContext()).getCheckedPeople())
                    setAlarm(getApplicationContext(),
                            item.getNotification1(),
                            item.getNotification2(),
                            item.getNotificationMessage(),
                            item.getNotificationTitle(),
                            false);


                new SettingsHelper(getApplicationContext())
                        .savePeopleTDataForNotification(item.getNotification1(),
                                item.getNotification2(),
                                item.getNotificationTitle(),
                                item.getNotificationMessage());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void initView() {
        Toolbar toolbar = findViewById(R.id.activity_people_toolbar);
        toolbar.setTitle("Театр Люди-Т");
//        toolbar.setLogo(R.drawable.ic_people_t_96);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.activity_people_viewpager);
        viewPager.setAlpha(0f);
        progressBar = findViewById(R.id.activity_people_progress);

//        Log.d(LOG_ARGS,)

    }

    public static void setAlarm(Context context, String date1, String date2, String message, String title, boolean stop) {
        String[] d1 = date1.split("::");
        String[] d2 = date2.split("::");

        int hours1 = Integer.parseInt(d1[0]);
        int hours2 = Integer.parseInt(d2[0]);

        int minutes1 = Integer.parseInt(d1[1]);
        int minutes2 = Integer.parseInt(d2[1]);

        int day1 = Integer.parseInt(d1[2]);
        int day2 = Integer.parseInt(d2[2]);

        int month1 = Integer.parseInt(d1[3]);
        int month2 = Integer.parseInt(d2[3]);

        int year1 = Integer.parseInt(d1[4]);
        int year2 = Integer.parseInt(d2[4]);


        Log.d("People-T", date1 + "\n" + date2 + "\n" + title + "\n" + message);

        Log.d("People-T d1", hours1 + ":" + minutes1 + " " + day1 + "." + month1 + "." + year1);
        Log.d("People-T d2", hours2 + ":" + minutes2 + " " + day2 + "." + month2 + "." + year2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours1);
        calendar.set(Calendar.MINUTE, minutes1);
        calendar.set(Calendar.DAY_OF_MONTH, day1);
        calendar.set(Calendar.MONTH, month1);
        calendar.set(Calendar.YEAR, year1);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, hours2);
        calendar1.set(Calendar.MINUTE, minutes2);
        calendar1.set(Calendar.DAY_OF_MONTH, day2);
        calendar1.set(Calendar.MONTH, month2);
        calendar1.set(Calendar.YEAR, year2);

        Log.d("PeopleT1", String.valueOf(System.currentTimeMillis() - calendar.getTimeInMillis()));
        Log.d("PeopleT2", String.valueOf(System.currentTimeMillis() - calendar1.getTimeInMillis()));

        Intent intent = new Intent(context, PeopleTNotification.class);

        Intent intent1 = new Intent(context, PeopleTNotification.class);

        PendingIntent pIntent = PendingIntent.getBroadcast(context, 200, intent, !stop ? PendingIntent.FLAG_ONE_SHOT : 0);
        PendingIntent pIntent1 = PendingIntent.getBroadcast(context, 101, intent1, !stop ? PendingIntent.FLAG_ONE_SHOT : 0);

        AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarm != null) {
            if (!stop) {
                if (System.currentTimeMillis() - calendar.getTimeInMillis() <= 0)
                    alarm.set(AlarmManager.RTC, calendar.getTimeInMillis(), pIntent);
                if (System.currentTimeMillis() - calendar1.getTimeInMillis() <= 0)
                    alarm.set(AlarmManager.RTC, calendar1.getTimeInMillis(), pIntent1);
            } else {
                alarm.cancel(pIntent);
                alarm.cancel(pIntent1);
            }
        }
    }

    class viewPagerAdapter extends FragmentPagerAdapter {

        viewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentPagePeopleT.newInstance(position);
        }

        @Override
        public int getCount() {
            return COUNT_PAGE;
        }
    }

    private void setTheme() {
        if (TabletHelper.isTablet(getApplicationContext())) {
            setTheme(R.style.AppTheme_Dialog);
        }
    }
}
