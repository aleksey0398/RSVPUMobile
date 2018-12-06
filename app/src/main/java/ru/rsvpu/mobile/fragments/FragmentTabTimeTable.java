package ru.rsvpu.mobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.rsvpu.mobile.R;

/**
 * Created by aleksej
 * on 04.12.2017.
 */

public class FragmentTabTimeTable extends Fragment {
    public static ViewPager viewPager;
    FragmentTimeTable fragmentTimeTable;
    public static FragmentPagerTimeTableAdapter adapter;

    public FragmentTabTimeTable() {

    }

    public void updateTimeTable() {
        if (viewPager == null) return;
        this.fragmentTimeTable.refreshTimeTable();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_time_table, container, false);
        initView(view);
        return view;
    }

    void initView(View v) {
        viewPager = v.findViewById(R.id.fragment_timetable_view_pager);
        adapter = new FragmentPagerTimeTableAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
    }


    class FragmentPagerTimeTableAdapter extends FragmentPagerAdapter {

        FragmentPagerTimeTableAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                fragmentTimeTable = FragmentTimeTable.newInstance();
                return fragmentTimeTable;
            } else {
                return FragmentTimeTableSetting.getInstance();
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

}
