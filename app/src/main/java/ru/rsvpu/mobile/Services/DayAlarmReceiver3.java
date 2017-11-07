package ru.rsvpu.mobile.Services;

/**
 * Created by aleksej on 08.11.2017.
 */

public class DayAlarmReceiver3 extends DayAlarmReceiver {

    @Override
    void setPair() {
        currentPair = 3;
    }

    @Override
    void setLogArgs() {
        LOG_ARGS = "Pair 3 receiver";
    }
}
