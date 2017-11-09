package ru.rsvpu.mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vk.sdk.VKSdk;

import ru.rsvpu.mobile.Activity.TutorialActivity;
import ru.rsvpu.mobile.Fragments.FragmentAds;
import ru.rsvpu.mobile.Fragments.FragmentNews;
import ru.rsvpu.mobile.Fragments.FragmentSettings;
import ru.rsvpu.mobile.Fragments.FragmentTimeTable;
import ru.rsvpu.mobile.Services.SendToServerService;
import ru.rsvpu.mobile.items.Container;
import ru.rsvpu.mobile.items.DateUtil;
import ru.rsvpu.mobile.items.SettingsHelper;
import ru.rsvpu.mobile.items.var;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private MenuItem timeTableMenuItem;
    Fragment fragments[] = {new FragmentTimeTable(), new FragmentNews(), new FragmentAds(), new FragmentSettings()};
    String currentFragment = "one";
    int transition = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
    private final String LOG_ARGS = "Main_Activity";

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_ARGS, "OnResume");
        if (!VKSdk.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), TutorialActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendStatisticToFirebase();

        if (!VKSdk.isLoggedIn()) {
            startActivity(new Intent(this, TutorialActivity.class));
            finish();
        }
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        setTitleTimeTable();

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);

        if (getIntent().getIntExtra("firstStart", -1) == 1) {
            navigationView.setSelectedItemId(R.id.menu_navigation_setting);
            currentFragment = "four";
            fragmentManager.beginTransaction().add(R.id.main_container, fragments[3], "four").setTransition(transition).commit();
            startService(new Intent(this, SendToServerService.class));
        } else {
            transaction.add(R.id.main_container, fragments[0], "one").commit();
        }

        navigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.menu_navigation_time:

                    if (fragmentManager.findFragmentByTag("one") != null) {
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("one")).setTransition(transition).commit();
                        if (var.changeGroup) {
                            FragmentTimeTable fragmentTimeTable = (FragmentTimeTable) fragments[0];
                            fragmentTimeTable.refreshTimeTable();
                        }
                    } else {
                        fragmentManager.beginTransaction().add(R.id.main_container, fragments[0], "one").setTransition(transition).commit();
                    }

                    if (fragmentManager.findFragmentByTag(currentFragment) != null && !currentFragment.equals("one")) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(currentFragment)).setTransition(transition).commit();
                    }
                    currentFragment = "one";

                    timeTableMenuItem = item;
                    break;

                case R.id.menu_navigation_news:

                    if (fragmentManager.findFragmentByTag("two") != null) {
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("two")).setTransition(transition).commit();
                    } else {
                        fragmentManager.beginTransaction().add(R.id.main_container, fragments[1], "two").setTransition(transition).commit();
                    }

                    if (fragmentManager.findFragmentByTag(currentFragment) != null && !currentFragment.equals("two")) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(currentFragment)).setTransition(transition).commit();
                    }
                    currentFragment = "two";
                    break;

                case R.id.menu_navigation_ads:

                    if (fragmentManager.findFragmentByTag("three") != null) {
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("three")).setTransition(transition).commit();
                    } else {
                        fragmentManager.beginTransaction().add(R.id.main_container, fragments[2], "three").setTransition(transition).commit();
                    }

                    if (fragmentManager.findFragmentByTag(currentFragment) != null && !currentFragment.equals("three")) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(currentFragment)).setTransition(transition).commit();
                    }
                    currentFragment = "three";
                    break;

                case R.id.menu_navigation_setting:
                    if (fragmentManager.findFragmentByTag("four") != null) {
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("four")).setTransition(transition).commit();
                    } else {
                        fragmentManager.beginTransaction().add(R.id.main_container, fragments[3], "four").setTransition(transition).commit();
                    }

                    if (fragmentManager.findFragmentByTag(currentFragment) != null && !currentFragment.equals("four")) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(currentFragment)).setTransition(transition).commit();
                    }
                    currentFragment = "four";
                    break;
            }

            setTitleTimeTable();

            return true;
        });
    }

    void sendStatisticToFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = firebaseDatabase.getReference();
        SettingsHelper helper = new SettingsHelper(getApplicationContext());

        if (helper.getVkData()[3].equals("-"))
            return;

        mReference.child("users")
                .child(helper.getVkData()[3])
                .child("last_connection_at")
                .setValue(DateUtil.generateToday() + " " + DateUtil.generateTime());

        mReference.child("users")
                .child(helper.getVkData()[3])
                .child("last_connection_milliseconds")
                .setValue(System.currentTimeMillis());

        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            mReference.child("users").child(helper.getVkData()[3]).child("current_version").setValue(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("RestrictedApi")
    void setTitleTimeTable() {
        BottomNavigationItemView timeTableMenuItem = findViewById(R.id.menu_navigation_time);
        Container container = new SettingsHelper(getApplicationContext()).getSettings();
        if (this.timeTableMenuItem != null) {
            this.timeTableMenuItem.setTitle((container.getName().equals("nothing") ? "Расписание" : container.getName()));
        } else {
            timeTableMenuItem.setTitle((container.getName().equals("nothing") ? "Расписание" : container.getName()));
        }
    }
}
