package ru.rsvpu.mobile.items;

import java.util.Calendar;

/**
 * Created by aleksej on 13.10.2017.
 *
 */

public class DateUtil {

    public static void main(String[] args) {
        generateToday();
    }

    public static String generateToday(){

        Calendar calendar = Calendar.getInstance();
//        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
//        System.out.println(calendar.get(Calendar.MONTH));
//        System.out.println(calendar.get(Calendar.YEAR));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        return (day<10?"0"+day:day)+"."+(month<10?"0"+month:month)+"."+year;
    }

    public static String generateTime(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        return hours+":"+minutes+":"+seconds;
    }

    public int getCurrentDayInWeek(){
        Calendar cal= Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }
}
