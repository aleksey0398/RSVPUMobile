package ru.rsvpu.mobile.Activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.vk.sdk.VKSdk;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.SettingsHelper;

public class SettingActivity extends AppCompatActivity {

    ImageView imageAvatar;
    Switch switchEvening, switchDay, switchPeople;
    Button buttonExit;
    Toolbar toolbar;
    TextView textName, textSurname, textVersion;

//    private String LOG_ARGS = "Setting Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),0);
            textVersion.setText("Версия: "+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            textVersion.setText("Ошибка определения версии");
            e.printStackTrace();
        }

        String[] vkDate = new SettingsHelper(getApplicationContext()).getVkData();
        if (!vkDate[0].equals("-")) {
            textName.setText(vkDate[0]);
            textSurname.setText(vkDate[1]);

            new Thread(() -> {
                URL url;
                try {
                    url = new URL(vkDate[2]);
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    runOnUiThread(() -> imageAvatar.setImageBitmap(bitmap));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }

        buttonExit.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setCancelable(true)
                .setPositiveButton("Выйти", (dialog, which) -> {
                    TutorialActivity.setAlarm(getApplicationContext(), true);
                    TutorialActivity.setAlarmPair(getApplicationContext(), true);
                    new SettingsHelper(getApplicationContext()).removeAllSettings();
                    VKSdk.logout();
                    finish();
                })
                .setNegativeButton("Отмена", ((dialog, which) -> {
                }))
                .setIcon(R.drawable.ic_logout)
                .setTitle("Внимание")
                .setMessage("Вы уверены, что хотите выйти из аккаунта VK?\nПриложение не будет работать пока Вы не авторизуетесь снова!")
                .show());

        switchEvening.setOnCheckedChangeListener((buttonView, isChecked) -> {
            new SettingsHelper(getApplicationContext()).saveCheckedEvening(isChecked);
            if (isChecked) {
                TutorialActivity.setAlarm(getApplicationContext(), false);
            } else {
                TutorialActivity.setAlarm(getApplicationContext(), true);
            }
        });

        switchDay.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            new SettingsHelper(getApplicationContext()).saveCheckedDay(isChecked);
            if (isChecked) {
                TutorialActivity.setAlarmPair(getApplicationContext(), false);
            } else {
                TutorialActivity.setAlarmPair(getApplicationContext(), true);
            }
        }));

        switchPeople.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            SettingsHelper helper = new SettingsHelper(getApplicationContext());
            helper.saveCheckedPeople(isChecked);

            if (isChecked) {
                PeopleTActivity.setAlarm(getApplicationContext(), helper.getPeopleDate1(), helper.getPeopleDate2(), helper.getPeopleTMessage(), helper.getPeopleTTitle(), false);
            } else {
                PeopleTActivity.setAlarm(getApplicationContext(), helper.getPeopleDate1(), helper.getPeopleDate2(), helper.getPeopleTMessage(), helper.getPeopleTTitle(), true);
            }
        }));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void initView() {
        toolbar = findViewById(R.id.activity_setting_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Настройки приложения");
        }

        imageAvatar = findViewById(R.id.activity_setting_img);
        switchDay = findViewById(R.id.activity_setting_switch_notification_day);
        switchEvening = findViewById(R.id.activity_setting_switch_notification_evening);
        switchPeople = findViewById(R.id.activity_setting_switch_notification_people);
        buttonExit = findViewById(R.id.activity_setting_button_exit);
        textName = findViewById(R.id.activity_setting_text_name);
        textSurname = findViewById(R.id.activity_setting_text_surname);
        textVersion = findViewById(R.id.activity_setting_txt_version);

        SettingsHelper settings = new SettingsHelper(getApplicationContext());
        switchDay.setChecked(settings.getCheckedDay());
        switchEvening.setChecked(settings.getCheckedEvening());
        switchPeople.setChecked(settings.getCheckedPeople());
    }

}
