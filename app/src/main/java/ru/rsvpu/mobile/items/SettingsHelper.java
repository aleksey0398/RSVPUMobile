package ru.rsvpu.mobile.items;

import android.content.Context;
import android.content.SharedPreferences;

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
        this.setting = context.getSharedPreferences(var.SETTINGS_MAIN, Context.MODE_PRIVATE);
        this.editor = setting.edit();
    }

    public void saveSelectedGroup(Container container, int typeOfGroup) {
        System.out.println("Save settings:\n" + container.getAttr() + "\n" + container.getValue() + "\n" + container.getName() + "\n" + typeOfGroup);

//        SharedPreferences setting = context.getSharedPreferences(var.SETTINGS_MAIN,Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = setting.edit();

        editor.putInt(var.SETTINGS_type, typeOfGroup);
        editor.putString(var.SETTINGS_name, container.getName());
        editor.putString(var.SETTINGS_attr, container.getAttr());
        editor.putInt(var.SETTINGS_group_course,getGroupCourse(container.getName()));

        if (!setting.contains(var.SETTINGS_preview_value)) {
            editor.putString(var.SETTINGS_value, container.getValue());
            editor.putString(var.SETTINGS_preview_value, container.getValue());
        } else {
            editor.putString(var.SETTINGS_preview_value, setting.getString(var.SETTINGS_value, "-"));
            editor.putString(var.SETTINGS_value, container.getValue());
        }
        editor.apply();
    }

    public Container getSettings() {
        Container container = new Container();
//        SharedPreferences settings = context.getSharedPreferences(var.SETTINGS_MAIN,Context.MODE_PRIVATE);
        container.setAttr(setting.getString(var.SETTINGS_attr, "nothing"));
        container.setValue(setting.getString(var.SETTINGS_value, "nothing"));
        container.setName(setting.getString(var.SETTINGS_name, "nothing"));
        return container;
    }

    public int getTypeOfGroup() {
//        SharedPreferences settings = context.getSharedPreferences(var.SETTINGS_MAIN,Context.MODE_PRIVATE);

        return setting.getInt(var.SETTINGS_type, -1);
    }

    public void saveTimeTable(String argsForSave) {
//        SharedPreferences setting = context.getSharedPreferences(var.SETTINGS_MAIN,Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = setting.edit();
        editor.putString(var.SETTINGS_time_table, argsForSave);
        editor.apply();
    }

    public String getLastSavedTimeTable() {
        if (!setting.contains(var.SETTINGS_time_table)) {
            return NO_SAVED_TIME_TABLE;
        } else {
            return setting.getString(var.SETTINGS_time_table, NO_SAVED_TIME_TABLE);
        }
    }

    public void saveCategories(String argsForSave) {
        editor.putString(var.SETTINGS_categories, argsForSave);
        editor.apply();
    }

    public String getSavedCategories() {
        if (!setting.contains(var.SETTINGS_categories)) {
            return NO_SAVED_CATEGORIES;
        } else {
            return setting.getString(var.SETTINGS_categories,NO_SAVED_CATEGORIES);
        }
    }

    private int getGroupCourse(String group){
       return Integer.valueOf(String.valueOf(group.split("-")[1].charAt(0)));
    }

    public int getGroupCourse(){
        return setting.getInt(var.SETTINGS_group_course,-1);
    }
}
