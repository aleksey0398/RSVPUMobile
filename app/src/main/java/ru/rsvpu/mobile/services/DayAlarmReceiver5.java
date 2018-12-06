package ru.rsvpu.mobile.services;

/**
 * Created by aleksej on
 * 08.11.2017.
 */

public class DayAlarmReceiver5 extends DayAlarmReceiver {
    @Override
    void setLogArgs() {
        LOG_ARGS = "Pair 5 receiver";
    }

    @Override
    void setPair() {
        currentPair = 5;
    }
}
