package ru.rsvpu.mobile.services;

/**
 * Created by aleksej on
 * 08.11.2017.
 */

public class DayAlarmReceiver2 extends DayAlarmReceiver {

    @Override
    void setLogArgs() {
        LOG_ARGS = "Pair 2";
    }

    @Override
    void setPair() {
        currentPair = 2;
    }
}
