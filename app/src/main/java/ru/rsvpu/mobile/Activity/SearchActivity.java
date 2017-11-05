package ru.rsvpu.mobile.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.Container;

/**
 * Created by aleksej
 * on 03.11.2017.
 */

public class SearchActivity extends AppCompatActivity {

    public final static String argsClass = "ClassList";
    public final static String argsTeacher = "TeacherList";
    public final static String argsGroup = "GroupList";
    public final static String argsResult = "result";

    public final static int RequestCode = 10;

    List<Container> classList = new ArrayList<>();
    List<Container> teacherList = new ArrayList<>();
    List<Container> groupList = new ArrayList<>();

    ListView listView;
    EditText editSearch;
    Toolbar toolbar;

    List<String> allList = new ArrayList<>();
    List<Container> allContainer = new ArrayList<>();
    List<Container> searchedContainer = new ArrayList<>();

    private final String LOG_ARGS = "Search_Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        getLists();

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Toast.makeText(getApplicationContext(), String.valueOf(s), Toast.LENGTH_SHORT).show();
                if (count == 0) {
                    listViewUpdate(allList);
                    searchedContainer.addAll(groupList);
                    searchedContainer.addAll(teacherList);
                    searchedContainer.addAll(classList);
                    return;
                }
                search(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
//            Toast.makeText(SearchActivity.this, String.valueOf(searchedContainer.get(position).getName()), Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            Log.d(LOG_ARGS, String.valueOf(searchedContainer.size()) + " size list container");
            resultIntent.putExtra(argsResult, new Gson().toJson(searchedContainer.get(position)));
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void search(String args) {
        searchedContainer.clear();
        args = args.toUpperCase();
        List<String> searchedList = new ArrayList<>();
        for (int i = 0; i < allList.size(); i++) {
            if (allList.get(i).toUpperCase().contains(args)) {
                searchedList.add(allList.get(i));
                searchedContainer.add(allContainer.get(i));
            }
        }

        listViewUpdate(searchedList);
    }

    void listViewUpdate(List<String> list) {
        ArrayAdapter<String> adapterAll = new ArrayAdapter<>(SearchActivity.this,
                android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapterAll);
    }

    void initView() {
        listView = findViewById(R.id.activity_search_listView);
        editSearch = findViewById(R.id.activity_search_editText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void getLists() {
        Type listOfTestObject = new TypeToken<List<Container>>() {
        }.getType();
        classList = new Gson().fromJson(getIntent().getStringExtra(argsClass), listOfTestObject);
        groupList = new Gson().fromJson(getIntent().getStringExtra(argsGroup), listOfTestObject);
        teacherList = new Gson().fromJson(getIntent().getStringExtra(argsTeacher), listOfTestObject);
//        Log.d(LOG_ARGS, getIntent().getStringExtra(argsClass));
//        Log.d(LOG_ARGS,getIntent().getStringExtra(argsGroup));
//        Log.d(LOG_ARGS, getIntent().getStringExtra(argsTeacher));

        allContainer.addAll(groupList);
        allContainer.addAll(teacherList);
        allContainer.addAll(classList);

        searchedContainer.addAll(groupList);
        searchedContainer.addAll(teacherList);
        searchedContainer.addAll(classList);

        for (Container item : groupList) {
            allList.add(item.getName());
        }

        for (Container item : teacherList) {
            allList.add(item.getName());
        }

        for (Container item : classList) {
            allList.add(item.getName());
        }

        listViewUpdate(allList);
    }
}
