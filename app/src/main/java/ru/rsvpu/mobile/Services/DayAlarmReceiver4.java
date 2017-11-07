package ru.rsvpu.mobile.Services;

/**
 * Created by aleksej on
 * 08.11.2017.
 */

public class DayAlarmReceiver4 extends DayAlarmReceiver {

    @Override
    void setPair() {
        currentPair = 4;
    }

    @Override
    void setLogArgs() {
        LOG_ARGS = "Pair 4 receiver";
    }
}
