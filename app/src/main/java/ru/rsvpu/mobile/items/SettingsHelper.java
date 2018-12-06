package ru.rsvpu.mobile.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by aleksej
 * on 13.10.2017.
 */

public class SettingsHelper {
    private Context context;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    public static final String NO_SAVED_TIME_TABLE = "no saved time table";
    public static final String NO_SAVED_CATEGORIES = "no saved categories";

    public SettingsHelper(Context context) {
        this.context = context;
        this.setting = context.getSharedPreferences(Var.SETTINGS_MAIN, Context.MODE_PRIVATE);
        this.editor = setting.edit();
    }

    public void saveSelectedGroup(Container container, int typeOfGroup) {
        Log.d("SaveSelectedGroup", "Save settings:\n" + container.getAttr() + "\n" + container.getValue() + "\n" + container.getName() + "\n" + typeOfGroup);

        editor.putInt(Var.SETTINGS_type, typeOfGroup);
        editor.putString(Var.SETTINGS_name, container.getName());
        editor.putString(Var.SETTINGS_attr, container.getAttr());

        //save course for only group
        if (container.getAttr().equals("gr"))
            editor.putInt(Var.SETTINGS_group_course, getGroupCourse(container.getName()));

        if (!setting.contains(Var.SETTINGS_preview_value)) {
            editor.putString(Var.SETTINGS_value, container.getValue());
            editor.putString(Var.SETTINGS_preview_value, container.getValue());
        } else {
            editor.putString(Var.SETTINGS_preview_value, setting.getString(Var.SETTINGS_value, "-"));
            editor.putString(Var.SETTINGS_value, container.getValue());
        }
        editor.apply();
    }

    public Container getSettings() {
        Container container = new Container();
//        SharedPreferences settings = context.getSharedPreferences(Var.SETTINGS_MAIN,Context.MODE_PRIVATE);
        container.setAttr(setting.getString(Var.SETTINGS_attr, "nothing"));
        container.setValue(setting.getString(Var.SETTINGS_value, "nothing"));
        container.setName(setting.getString(Var.SETTINGS_name, "nothing"));
        return container;
    }

    public int getTypeOfGroup() {
//        SharedPreferences settings = context.getSharedPreferences(Var.SETTINGS_MAIN,Context.MODE_PRIVATE);

        return setting.getInt(Var.SETTINGS_type, -1);
    }

    public void saveTimeTable(String argsForSave) {
//        SharedPreferences setting = context.getSharedPreferences(Var.SETTINGS_MAIN,Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = setting.edit();
        editor.putString(Var.SETTINGS_time_table, argsForSave);
        editor.apply();
    }

    public String getLastSavedTimeTable() {
        if (!setting.contains(Var.SETTINGS_time_table)) {
            return NO_SAVED_TIME_TABLE;
        } else {
            return setting.getString(Var.SETTINGS_time_table, NO_SAVED_TIME_TABLE);
        }
    }

    public void saveCategories(String argsForSave) {
        editor.putString(Var.SETTINGS_categories, argsForSave);
        editor.apply();
    }

    public String getSavedCategories() {
        if (!setting.contains(Var.SETTINGS_categories)) {
            return NO_SAVED_CATEGORIES;
        } else {
            return setting.getString(Var.SETTINGS_categories, NO_SAVED_CATEGORIES);
        }
    }

    private int getGroupCourse(String group) {
        return Integer.valueOf(String.valueOf(group.split("-")[1].charAt(0)));
    }

    public int getGroupCourse() {
        return setting.getInt(Var.SETTINGS_group_course, -1);
    }

    public void saveVKData(String name, String surname, String urlPhoto, String id) {
        editor.putString(Var.SETTINGS_person_name, name);
        editor.putString(Var.SETTINGS_person_surname, surname);
        editor.putString(Var.SETTINGS_photo_url, urlPhoto);
        editor.putString(Var.SETTINGS_person_id, id);
        editor.apply();
    }

    public String[] getVkData() {
        return new String[]{setting.getString(Var.SETTINGS_person_name, "-"),
                setting.getString(Var.SETTINGS_person_surname, "-"),
                setting.getString(Var.SETTINGS_photo_url, "-"),
                setting.getString(Var.SETTINGS_person_id, "-")};
    }

    public void removeAllSettings() {
        editor.clear().apply();
    }

    public boolean getCheckedDay() {
        return setting.getBoolean(Var.SETTINGS_alarm_day, true);
    }

    public boolean getCheckedEvening() {
        return setting.getBoolean(Var.SETTINGS_alarm_evening, true);
    }

    public void saveCheckedDay(boolean check) {
        editor.putBoolean(Var.SETTINGS_alarm_day, check);
        editor.apply();
    }

    public void saveCheckedEvening(boolean check) {
        editor.putBoolean(Var.SETTINGS_alarm_evening, check);
        editor.apply();
    }

    public boolean getCheckedPeople() {
        return setting.getBoolean(Var.SETTINGS_alarm_people, true);
    }

    public void saveCheckedPeople(boolean check) {
        editor.putBoolean(Var.SETTINGS_alarm_people, check);
        editor.apply();
    }

    public void savePeopleTDataForNotification(String date1, String date2, String title, String message) {
        editor.putString(Var.SETTINGS_alarm_people_date1, date1);
        editor.putString(Var.SETTINGS_alarm_people_date2, date2);
        editor.putString(Var.SETTINGS_alarm_people_title, title);
        editor.putString(Var.SETTINGS_alarm_people_message, message);
        editor.apply();
    }

    public String getPeopleTMessage() {
        return setting.getString(Var.SETTINGS_alarm_people_message, "-");
    }

    public String getPeopleTTitle() {
        return setting.getString(Var.SETTINGS_alarm_people_title, "-");
    }

    public String getPeopleDate1() {
        return setting.getString(Var.SETTINGS_alarm_people_date1, "-");
    }

    public String getPeopleDate2() {
        return setting.getString(Var.SETTINGS_alarm_people_date2, "-");
    }

    public void saveLastUpdateVkDate(long time){
        editor.putLong(Var.SETTINGS_vk_lastupdate,time);
        editor.apply();
    }

    public boolean needUpdateVk(){
        return System.currentTimeMillis() - setting.getLong(Var.SETTINGS_vk_lastupdate,0)>= Var.FIVE_DAY;
    }

    public boolean checkContains(String args) {
        return setting.contains(args);
    }
}
