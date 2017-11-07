package ru.rsvpu.mobile.items;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksej
 * on 07.11.2017.
 */

public class GetTimeTableForNotif extends AsyncTask<Context, Void, List<TimeTableOneDay>> {

    private String resultString;
    private boolean urlValid = false;

    @Override
    protected List<TimeTableOneDay> doInBackground(Context... contexts) {
        Context context = contexts[0];

        boolean network = myNetwork.isWorking(contexts[0]);
        if (network) {
            urlValid = myNetwork.checkURL();
        }

        boolean connection;
        if (network && urlValid) {
            try {
                URL url = new URL(generateRequest(context));
                resultString = getJson(url);
                connection = true;
//                System.out.println(resultString);
            } catch (Exception e) {
                e.printStackTrace();
                connection = false;
            }
        } else {
            connection = false;
        }

        List<TimeTableOneDay> list = new ArrayList<>();
        if (connection) {
            new SettingsHelper(context).saveTimeTable(resultString);
            list = generateListFromJson(resultString);
        }
        if (!network) {
            resultString = new SettingsHelper(context).getLastSavedTimeTable();
            if (resultString.equals(SettingsHelper.NO_SAVED_TIME_TABLE)) {
                //nothing
                list = null;
            } else {
                list = generateListFromJson(resultString);
            }
        } else {
            if (!urlValid) {
//                    Toast.makeText(getActivity(), "Сервер недоступен, загружаем последнее сохранённое расписание", Toast.LENGTH_SHORT).show();
                resultString = new SettingsHelper(context).getLastSavedTimeTable();
                if (resultString.equals(SettingsHelper.NO_SAVED_TIME_TABLE)) {
//                        Toast.makeText(getActivity(), "Сохранённого расписания не найдено", Toast.LENGTH_SHORT).show();
                    list = null;
                } else {
                    list = generateListFromJson(resultString);
                }
            }
        }

        return list;
    }

    private String generateRequest(Context context) {
        String url = var.url;

        SettingsHelper helper = new SettingsHelper(context);
        Container saved_container = helper.getSettings();
        String value = saved_container.getValue();
        int typeGroup = helper.getTypeOfGroup();
        switch (saved_container.getAttr()) {
            case "gr":
                url += "v_gru=" + value + "&";
                break;
            case "prep":
                url += "v_prep=" + value + "&";
                break;
            case "aud":
                url += "v_aud=" + value + "&";
                break;
        }
        url += "v_date=" + DateUtil.generateToday() + "&";
        url += "type=" + (typeGroup == 0 ? "ochnoe" : "zaochnoe");
        Log.d("Generate Request", url);
        return url;
    }

    private String getJson(URL url) throws IOException {
        HttpURLConnection urlConnection;
        BufferedReader reader;

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    private List<TimeTableOneDay> generateListFromJson(String resultString) {
        Type listOfTimeTable = new TypeToken<List<TimeTableOneDay>>() {
        }.getType();
        return new Gson().fromJson(resultString, listOfTimeTable);
    }
}
