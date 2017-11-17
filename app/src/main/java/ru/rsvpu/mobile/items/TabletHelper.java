package ru.rsvpu.mobile.items;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by
 * aleksej on 10.08.16.
 * 15.11.17
 */
public class TabletHelper {

    public static int getDisplayColumns(Activity activity) {
        int columnCount = 1;
        int sizeLayout = isTabletRV(activity);
        if (sizeLayout == 1 && activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columnCount = 3;
        } else if (sizeLayout == 1 && activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columnCount = 2;
        } else if (sizeLayout == 2 && activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columnCount = 2;
        }
        return columnCount;
    }

    private static int isTabletRV(Context context) {
        int screenLayout = 0;
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        if (xlarge) {
            screenLayout = 1;
        } else if (large) {
            screenLayout = 2;
        }
        return (screenLayout);
    }

    public static boolean isTablet(Context context){

        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);

        return (large||xlarge);
    }

    public static boolean[] isTabletCurrentType(Context context){

        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        boolean[] type = {xlarge,large};

        return type;
    }

}
