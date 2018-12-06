package ru.rsvpu.mobile.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.rsvpu.mobile.activities.SearchActivity;
import ru.rsvpu.mobile.activities.SettingActivity;
import ru.rsvpu.mobile.activities.TutorialActivity;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.Container;
import ru.rsvpu.mobile.items.SettingsHelper;
import ru.rsvpu.mobile.items.MyNetwork;
import ru.rsvpu.mobile.items.Var;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by aleksej
 * on 12.10.2017.
 */

public class FragmentTabSettings extends Fragment {

    Spinner spinner;
    ListView listViewGroup;
    ListView listViewClass;
    ListView listViewTeacher;
    List<Container> listGroup = new ArrayList<>(), listTeacher = new ArrayList<>(), listClass = new ArrayList<>();
    FloatingActionButton fab;
    TextView selected;
    Container selectedContainer;
    String[] types = {"Очное", "Заочное"};
    Toolbar toolbar;
    Switch showGroup, showClass, showTeacher;
    CardView cardTeacher, cardGroup, cardClass;
    ProgressBar progressBar;
    View.OnClickListener fabOfflineListener;
    View.OnClickListener fabOnlineListener;

    SettingsHelper settingsHelper;
    int selectedType = 0, typeForSave = 0;
    private final String LOG_ARGS = "Fragment_Setting";

//    private boolean firstStart = false;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Settings", "onCreate");

        switch (settingsHelper.getSettings().getAttr()) {
            case "gr":
                showGroup.setChecked(true);
                break;
            case "prep":
                showTeacher.setChecked(true);
                break;
            case "aud":
                showClass.setChecked(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_search:
                if (listGroup == null)
                    break;
                Intent intentForSearch = new Intent(getActivity(), SearchActivity.class);
                intentForSearch.putExtra(SearchActivity.argsTeacher, new Gson().toJson(listTeacher));
                intentForSearch.putExtra(SearchActivity.argsGroup, new Gson().toJson(listGroup));
                intentForSearch.putExtra(SearchActivity.argsClass, new Gson().toJson(listClass));

                startActivityForResult(intentForSearch, 100);
                Log.d(LOG_ARGS, "Search pressed");
                break;
            case R.id.menu_item_setting:

                Intent intentForSettings = new Intent(getActivity(), SettingActivity.class);
                startActivity(intentForSettings);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Container resultContainer = new Gson().fromJson(data.getStringExtra(SearchActivity.argsResult), Container.class);
        selectedContainer = resultContainer;
        typeForSave = selectedType;
        fab.setVisibility(VISIBLE);
        selected.setText("Нажмите \"сохранить\"");
        switch (resultContainer.getAttr()) {
            case "gr":
                showGroup.setChecked(true);
                break;
            case "prep":
                showTeacher.setChecked(true);
                break;
            case "aud":
                showClass.setChecked(true);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_tab_settings, container, false);
        setHasOptionsMenu(true);
        initView(v);

        settingsHelper = new SettingsHelper(getActivity());

        if (settingsHelper.getTypeOfGroup() != -1) {
            Container container1 = settingsHelper.getSettings();
            selected.setText(container1.getName());
            spinner.setSelection(settingsHelper.getTypeOfGroup());
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = i;

                new getList().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setCheckListener();
        setListViewListener();
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("save", true);
    }

    void initView(View v) {
        spinner = v.findViewById(R.id.fragment_settings_spinnerType);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, types);
        spinner.setAdapter(spinnerAdapter);
        listViewTeacher = v.findViewById(R.id.fragment_setting_listViewTeacher);
        listViewClass = v.findViewById(R.id.fragment_setting_listViewClass);
        listViewGroup = v.findViewById(R.id.fragment_setting_listViewGroup);
        fab = v.findViewById(R.id.fragment_setting_fab);
        fab.setVisibility(GONE);
        selected = v.findViewById(R.id.fragment_setting_selected);
        toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("РГППУ");
        showClass = v.findViewById(R.id.fragment_settings_checkClass);
        showGroup = v.findViewById(R.id.fragment_settings_checkGroup);
        showTeacher = v.findViewById(R.id.fragment_settings_checkTeacher);
        cardClass = v.findViewById(R.id.fragment_setting_cardClass);
        cardTeacher = v.findViewById(R.id.fragment_setting_cardTeacher);
        cardGroup = v.findViewById(R.id.fragment_setting_cardGroup);
        progressBar = v.findViewById(R.id.fragment_setting_progressbar);

    }

    void setListViewListener() {
        listViewGroup.setOnItemClickListener((adapterView, view, i, l) -> {
            fab.setVisibility(VISIBLE);
            selected.setText("Нажмите \"сохранить\"");
            selectedContainer = listGroup.get(i);
            typeForSave = selectedType;
        });
        listViewTeacher.setOnItemClickListener((adapterView, view, i, l) -> {
            fab.setVisibility(VISIBLE);
            selected.setText("Нажмите \"сохранить\"");
            selectedContainer = listTeacher.get(i);
            typeForSave = selectedType;
        });
        listViewClass.setOnItemClickListener((adapterView, view, i, l) -> {
            fab.setVisibility(VISIBLE);
            selected.setText("Нажмите \"сохранить\"");
            selectedContainer = listClass.get(i);
            typeForSave = selectedType;
        });

        fabOfflineListener = v -> new getList().execute();

        fabOnlineListener = v -> {
            SettingsHelper helper = new SettingsHelper(getActivity());
            helper.saveSelectedGroup(selectedContainer, typeForSave);
//            Snackbar.make(v, "Сохранено", Snackbar.LENGTH_SHORT).show();
            selected.setText(selectedContainer.getName());
            fab.setVisibility(GONE);
            Var.changeGroup = true;

            if (helper.getCheckedEvening())
                TutorialActivity.setAlarm(getActivity(), false);

            if (selectedContainer.getAttr().equals("gr") && helper.getCheckedDay()) {
                TutorialActivity.setAlarmPair(getActivity(), false);
            } else if (!selectedContainer.getAttr().equals("gr")) {
                Toast.makeText(getActivity(), "Для преподавателей уведомления о следующей паре не выводятся", Toast.LENGTH_SHORT).show();
                TutorialActivity.setAlarmPair(getActivity(), true);
            }
        };
    }

    void setCheckListener() {
        showGroup.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                listViewGroup.setVisibility(GONE);
                LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) cardGroup.getLayoutParams();
                loparams.weight = 0.1f;
                cardGroup.setLayoutParams(loparams);

            } else {
                showTeacher.setChecked(false);
                showClass.setChecked(false);

                LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) cardGroup.getLayoutParams();
                loparams.weight = 0.7f;
                cardGroup.setLayoutParams(loparams);
                listViewGroup.setVisibility(VISIBLE);
            }
        });

        showTeacher.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                listViewTeacher.setVisibility(GONE);
                LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) cardTeacher.getLayoutParams();
                loparams.weight = 0.1f;
                cardTeacher.setLayoutParams(loparams);

            } else {
                showGroup.setChecked(false);
                showClass.setChecked(false);

                LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) cardTeacher.getLayoutParams();
                loparams.weight = 0.7f;
                cardTeacher.setLayoutParams(loparams);
                listViewTeacher.setVisibility(VISIBLE);
            }
        });

        showClass.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                listViewClass.setVisibility(GONE);
                LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) cardClass.getLayoutParams();
                loparams.weight = 0.1f;
                cardClass.setLayoutParams(loparams);

            } else {
                showTeacher.setChecked(false);
                showGroup.setChecked(false);

                LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) cardClass.getLayoutParams();
                loparams.weight = 0.7f;
                cardClass.setLayoutParams(loparams);
                listViewClass.setVisibility(VISIBLE);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class getList extends AsyncTask<Void, Void, Void> {

        String resultJSON;

        //        long startTime;
        boolean connection = true, network = false, urlValid = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(VISIBLE);
            fab.setOnClickListener(fabOnlineListener);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SettingsHelper settingsHelper = new SettingsHelper(getActivity());
            if (connection) {
                updateListView(resultJSON);
                settingsHelper.saveCategories(resultJSON);
                fab.setVisibility(GONE);
                fab.setOnClickListener(fabOnlineListener);
                fab.setImageResource(R.drawable.ic_save);
            }

            progressBar.setVisibility(View.GONE);

            if (!network) {
                if (!settingsHelper.getSavedCategories().equals(SettingsHelper.NO_SAVED_CATEGORIES)) {
                    Toast.makeText(getActivity(), "Списки offline", Toast.LENGTH_SHORT).show();
                    updateListView(settingsHelper.getSavedCategories());
                } else {
                    Toast.makeText(getActivity(), "Нет сохранённых списков", Toast.LENGTH_SHORT).show();
                }
                fab.setOnClickListener(fabOfflineListener);
                fab.setImageResource(R.drawable.ic_refresh);
                fab.setVisibility(VISIBLE);
            } else {
                if (!urlValid) {
                    if (!settingsHelper.getSavedCategories().equals(SettingsHelper.NO_SAVED_CATEGORIES)) {
                        Toast.makeText(getActivity(), "Сервер недоступен, загружаем последние сохранённые группы", Toast.LENGTH_SHORT).show();
                        updateListView(settingsHelper.getSavedCategories());
                    } else {
                        Toast.makeText(getActivity(), "Нет сохранённых списков", Toast.LENGTH_SHORT).show();
                    }
                    fab.setOnClickListener(fabOfflineListener);
                    fab.setImageResource(R.drawable.ic_refresh);
                    fab.setVisibility(VISIBLE);
                }

            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection urlConnection;
            BufferedReader reader;

            network = MyNetwork.isWorking(getActivity());
            urlValid = MyNetwork.checkURL();
            if (network && urlValid) {
                try {
                    URL url = new URL(Var.url + (selectedType != 0 ? "getAllZaochnoe" : "getAllOchnoe") + MyNetwork.additionForStatistic(getContext()));
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
                    resultJSON = buffer.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    connection = false;
                }
            } else {
                connection = false;
            }

            return null;
        }
    }

    void updateListView(String resultJSON) {
        String[] args = resultJSON.split("//");

        Type listOfTestObject = new TypeToken<List<Container>>() {
        }.getType();
        listGroup = new Gson().fromJson(args[0], listOfTestObject);
        listTeacher = new Gson().fromJson(args[1], listOfTestObject);
        listClass = new Gson().fromJson(args[2], listOfTestObject);

        List<String> nameTeacher = new ArrayList<>();
        for (Container teacher : listTeacher) {
            nameTeacher.add(teacher.getName());
        }
        ArrayAdapter<String> adapterTeacher = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, nameTeacher);
        listViewTeacher.setAdapter(adapterTeacher);

        List<String> nameClass = new ArrayList<>();
        for (Container classRoom : listClass) {
            nameClass.add(classRoom.getName());
        }
        ArrayAdapter<String> adapterClass = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, nameClass);
        listViewClass.setAdapter(adapterClass);

        List<String> nameGroup = new ArrayList<>();
        for (Container group : listGroup) {
            nameGroup.add(group.getName());
        }
        ArrayAdapter<String> adapterGroup = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, nameGroup);
        listViewGroup.setAdapter(adapterGroup);
    }

}
