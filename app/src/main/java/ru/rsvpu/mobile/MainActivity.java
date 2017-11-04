package ru.rsvpu.mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.vk.sdk.VKSdk;

import ru.rsvpu.mobile.Activity.TutorialActivity;
import ru.rsvpu.mobile.Fragments.FragmentAds;
import ru.rsvpu.mobile.Fragments.FragmentNews;
import ru.rsvpu.mobile.Fragments.FragmentSettings;
import ru.rsvpu.mobile.Fragments.FragmentTimeTable;
import ru.rsvpu.mobile.items.Container;
import ru.rsvpu.mobile.items.SettingsHelper;
import ru.rsvpu.mobile.items.var;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private MenuItem timeTableMenuItem;
    Fragment fragments[] = {new FragmentTimeTable(), new FragmentNews(), new FragmentAds(), new FragmentSettings()};
    String currentFragment = "one";
    int transition = FragmentTransaction.TRANSIT_FRAGMENT_FADE;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!VKSdk.isLoggedIn()) {
            startActivity(new Intent(this, TutorialActivity.class));
            finish();
        }

//        VKSdk.logout();

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        setTitleTimeTable();

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);

        if(getIntent().getIntExtra("firstStart",-1)==1){
            navigationView.setSelectedItemId(R.id.menu_navigation_setting);
            currentFragment = "four";
            fragmentManager.beginTransaction().add(R.id.main_container, fragments[3], "four").setTransition(transition).commit();

        } else {
            transaction.add(R.id.main_container, fragments[0], "one").commit();
        }

        navigationView.setOnNavigationItemSelectedListener(item -> {


            switch (item.getItemId()) {
                case R.id.menu_navigation_time:

                    if (fragmentManager.findFragmentByTag("one") != null) {
                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("one")).setTransition(transition).commit();
                    } else {
                        fragmentManager.beginTransaction().add(R.id.main_container, fragments[0], "one").setTransition(transition).commit();
                    }

                    if (fragmentManager.findFragmentByTag(currentFragment) != null && !currentFragment.equals("one")) {
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(currentFragment)).setTransition(transition).commit();
                    }
                    currentFragment = "one";
                    if (var.changeGroup) {
                        FragmentTimeTable fragmentTimeTable = (FragmentTimeTable) fragments[0];
                        fragmentTimeTable.refreshTimeTable();
                    }
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
