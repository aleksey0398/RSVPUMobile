package ru.rsvpu.mobile.items;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by aleksej on 05.06.17.
 */

public class DateTimeTable {

    private String currentString;
    private Calendar calendar;

    private int day = 0;
    private int month = 0;
    private int year = 0;

    public DateTimeTable() {
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }

    public void plus14Day() {
        calendar.add(Calendar.DATE, 14);
        print(getCurrentTimeString());
    }

    public void minus14Day() {
        calendar.add(Calendar.DATE, -14);
        print(getCurrentTimeString());
    }

    public String getCurrentTimeString() {

        currentString = "";
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        if (day<10){
            currentString += "0";
        }
        currentString +=day+".";

        if(month+1<10){
            currentString +="0";
        }
        currentString +=(month+1)+".";
        currentString +=year;
        print(currentString);
        return currentString;
    }

    public void setDate(int day,int month,int year){
        this.calendar.set(Calendar.DAY_OF_MONTH,day);
        this.calendar.set(Calendar.MONTH, month);
        this.calendar.set(Calendar.YEAR, year);
        print(getCurrentTimeString());
    }

    private void print(String str){
        Log.d("DateTimeTable",str);
    }


    //getters
    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }
}
