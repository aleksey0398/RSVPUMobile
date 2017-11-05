package ru.rsvpu.mobile.items;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aleksej on 18.10.2017.
 *
 */

public class myNetwork {

    public static boolean checkURL() {

        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(var.url+"checkURL").openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isWorking(Context context) {

        final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr != null ? conMgr.getActiveNetworkInfo() : null;

        // notify user you are not online
        return activeNetwork != null && activeNetwork.isConnected();

    }
}
