package ru.rsvpu.mobile.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.DateTimeTable;

/**
 * Created by aleksej
 * on 04.12.2017.
 */

public class FragmentTimeTableSetting extends Fragment implements View.OnClickListener{

    Button btnPlusWeek,btnMinusWeek,btnSetDate, btnOpenTime, btnFriend, btnToday;
    TextView txtCurrentDate;
    FloatingActionButton fabDone;

    DateTimeTable dateTimeTable = new DateTimeTable();
    private String LOG_ARGS = "TimeTableSetting";

    public static FragmentTimeTableSetting getInstance(){
        FragmentTimeTableSetting setting = new FragmentTimeTableSetting();
        Bundle args = new Bundle();
        setting.setArguments(args);
        return setting;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_table_options,container,false);
        initView(view);

        return view;
    }

    void initView(View v){
        btnSetDate = v.findViewById(R.id.fragment_timetable_setting_btn_set_date);
        btnPlusWeek = v.findViewById(R.id.fragment_timetable_setting_btn_plus_week);
        btnMinusWeek = v.findViewById(R.id.fragment_timetable_setting_btn_minus_week);
        btnOpenTime = v.findViewById(R.id.fragment_timetable_setting_btn_time);
        btnFriend = v.findViewById(R.id.fragment_timetable_setting_btn_friends);
        btnToday = v.findViewById(R.id.fragment_timetable_setting_btn_today);
        fabDone = v.findViewById(R.id.fragment_timetable_setting_fab_done);

        txtCurrentDate = v.findViewById(R.id.fragment_timetable_setting_current_date);
        updateTextView();

        btnOpenTime.setOnClickListener(this);
        btnMinusWeek.setOnClickListener(this);
        btnPlusWeek.setOnClickListener(this);
        btnFriend.setOnClickListener(this);
        btnSetDate.setOnClickListener(this);
        btnToday.setOnClickListener(this);

        fabDone.setOnClickListener(v1 -> {
            if(FragmentTabTimeTable.viewPager != null){
                FragmentTabTimeTable.viewPager.setCurrentItem(0,true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_timetable_setting_btn_time:
                openTime();
                break;

            case R.id.fragment_timetable_setting_btn_friends:
                setFriend();
                break;

            case R.id.fragment_timetable_setting_btn_minus_week:
                minusWeek();
                break;

            case R.id.fragment_timetable_setting_btn_plus_week:
                plusWeek();
                break;

            case R.id.fragment_timetable_setting_btn_set_date:
                setDate();
                break;
            case R.id.fragment_timetable_setting_btn_today:
                dateTimeTable = new DateTimeTable();
                updateTextView();
                break;
        }
        updateTextView();
    }

    void setDate(){
       DatePickerDialog mDataPickerDialog = new DatePickerDialog(getActivity(), (datePicker, i, i1, i2) -> {
           String dataString = "";

           if (i2 < 10) {
               dataString += "0" + String.valueOf(i2) + ".";
           } else {
               dataString += String.valueOf(i2) + ".";
           }

           if (i1 < 9) {
               dataString += "0" + String.valueOf(i1 + 1) + ".";
           } else {
               dataString += String.valueOf(i1 + 1) + ".";
           }
           dataString += String.valueOf(i);
           Log.d(LOG_ARGS, dataString);

           dateTimeTable.setDate(i2,i1,i);

           updateTextView();

       }, dateTimeTable.getYear(), dateTimeTable.getMonth(), dateTimeTable.getDay());

        mDataPickerDialog.show();
    }

    void plusWeek(){
        dateTimeTable.plus14Day();
    }
    void minusWeek(){
        dateTimeTable.minus14Day();
    }

    void setFriend(){

    }

    void openTime(){

    }

    void updateTextView(){
        txtCurrentDate.setText(dateTimeTable.getCurrentTimeString());
    }
}
