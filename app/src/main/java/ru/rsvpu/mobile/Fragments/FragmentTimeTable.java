package ru.rsvpu.mobile.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import ru.rsvpu.mobile.Adapters.RVAdapterTimeTable;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.Utils.myLinearLayoutManager;
import ru.rsvpu.mobile.items.Container;
import ru.rsvpu.mobile.items.DateUtil;
import ru.rsvpu.mobile.items.SettingsHelper;
import ru.rsvpu.mobile.items.TimeTableOneDay;
import ru.rsvpu.mobile.items.myNetwork;
import ru.rsvpu.mobile.items.var;

/**
 * Created by aleksej on 12.10.2017.
 *
 */

public class FragmentTimeTable extends Fragment {

    private RecyclerView rv;
    private SwipeRefreshLayout swipeToRefresh;
    private int typeGroup;
    private String attr,value;
    private RVAdapterTimeTable adapter;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    private final String LOG_ARGS = "FragmentTimeTable";
    private LinearLayout noSettings;

    public FragmentTimeTable() {

    }

    public void refreshTimeTable(){
        swipeToRefresh.post(() -> {
            swipeToRefresh.setRefreshing(true);
            refreshListener.onRefresh();
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time_table, container, false);
        initView(v);
        Log.d(LOG_ARGS,"onCreate");
        refreshListener = () -> {
            Log.d("Fragment TT","refresh listener");
            getSavedInformation();
            new GetTimeTable().execute();

        };

        swipeToRefresh.setOnRefreshListener(refreshListener);

        getSavedInformation();


//
        return v;
    }

    void swipeToCurrentDay(){
        DateUtil date = new DateUtil();
        int position = date.getCurrentDayInWeek();
        if (position == 1){
            position = 6;
        }
        position -= 2;
//        System.out.println(position);
//        rv.scrollToPosition(2);

        rv.smoothScrollToPosition(position);

    }

    void initView(View v) {
        rv = v.findViewById(R.id.fragment_timetable_recycler);
        rv.setLayoutManager(new myLinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        adapter = new RVAdapterTimeTable(getActivity());
        rv.setAdapter(adapter);

        swipeToRefresh = v.findViewById(R.id.fragment_timetable_SwipeToRefresh);
        noSettings = v.findViewById(R.id.fragment_timetable_no_setting);
//        toolbar = v.findViewById(R.id.fragment_timetable_toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

    }

    private void getSavedInformation(){
        SettingsHelper settingsHelper = new SettingsHelper(getActivity());
        Container container1 = settingsHelper.getSettings();
        attr = container1.getAttr();
        value = container1.getValue();
        typeGroup = settingsHelper.getTypeOfGroup();

        if (typeGroup != -1) {

            noSettings.setVisibility(View.GONE);
            new GetTimeTable().execute();

        } else {
            Log.d(LOG_ARGS,"Расписание не настроенно");
            noSettings.setVisibility(View.VISIBLE);
//            Toast.makeText(getActivity(), "Зайдите в настройки!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetTimeTable extends AsyncTask<Void, Void, Void> {
        String resultString;
        boolean connection = false;
        boolean network = false;
        boolean urlValid = false;

        @Override
        protected Void doInBackground(Void... voids) {

            network = myNetwork.isWorking(getActivity());
            if(network){
                urlValid = myNetwork.checkURL(generateRequest());
            }

            if (network && urlValid)  {
                try {
                    URL url = new URL(generateRequest());
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeToRefresh.setRefreshing(false);
            if (connection) {
                Type listOfTimeTable = new TypeToken<List<TimeTableOneDay>>() {
                }.getType();
                List<TimeTableOneDay> list = new Gson().fromJson(resultString, listOfTimeTable);
//                System.out.println(list.size());
                adapter.setList(list);

                Calendar calendar = Calendar.getInstance();
                System.out.println("Day of the week: " + calendar.get(Calendar.DAY_OF_WEEK));
            }

            if(!network){
                Toast.makeText(getActivity(), "Расписание offline", Toast.LENGTH_SHORT).show();
            } else {
                if(!urlValid){
                    Toast.makeText(getActivity(),"Сервер недоступен, загружаем последнее сохранённое расписание",Toast.LENGTH_SHORT).show();
                }
            }



            swipeToCurrentDay();
            var.changeGroup = false;
        }
    }

    String generateRequest() {
        String url = var.url;
        switch (attr) {
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

    String getJson(URL url) throws IOException {
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

}
