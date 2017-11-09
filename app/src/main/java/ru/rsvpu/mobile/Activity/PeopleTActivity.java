package ru.rsvpu.mobile.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import ru.rsvpu.mobile.CustomView.PresentationViewPager;
import ru.rsvpu.mobile.R;

public class PeopleTActivity extends AppCompatActivity {

    PresentationViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_t);
        initView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void initView() {
        Toolbar toolbar = findViewById(R.id.activity_people_toolbar);
        toolbar.setTitle("Театр Люди-Т");
        toolbar.setLogo(R.drawable.ic_people_t_96);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.activity_people_viewpager);
    }


}
