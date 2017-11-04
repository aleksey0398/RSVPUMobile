package ru.rsvpu.mobile.items;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aleksej on 13.10.2017.
 *
 */

public class SettingsHelper {
    private Context context;

    public SettingsHelper(Context context) {
        this.context = context;
    }

    public void saveSelectedGroup(Container container, int typeOfGroup){
        System.out.println("Save settings:\n"+container.getAttr()+"\n"+container.getValue()+"\n"+container.getName()+"\n"+typeOfGroup);

        SharedPreferences setting = context.getSharedPreferences(var.SETTINGS_MAIN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();

        editor.putInt(var.SETTINGS_type,typeOfGroup);
        editor.putString(var.SETTINGS_name,container.getName());
        editor.putString(var.SETTINGS_attr,container.getAttr());

        if(!setting.contains(var.SETTINGS_preview_value)){
            editor.putString(var.SETTINGS_value,container.getValue());
            editor.putString(var.SETTINGS_preview_value,container.getValue());
        } else {
            editor.putString(var.SETTINGS_preview_value, setting.getString(var.SETTINGS_value,"-"));
            editor.putString(var.SETTINGS_value,container.getValue());
        }
        editor.apply();
    }

    public Container getSettings(){
        Container container = new Container();
        SharedPreferences settings = context.getSharedPreferences(var.SETTINGS_MAIN,Context.MODE_PRIVATE);
        container.setAttr(settings.getString(var.SETTINGS_attr,"nothing"));
        container.setValue(settings.getString(var.SETTINGS_value,"nothing"));
        container.setName(settings.getString(var.SETTINGS_name,"nothing"));
        return container;
    }

    public int getTypeOfGroup() {
        SharedPreferences settings = context.getSharedPreferences(var.SETTINGS_MAIN,Context.MODE_PRIVATE);

        return settings.getInt(var.SETTINGS_type,-1);
    }
}
