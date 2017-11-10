package ru.rsvpu.mobile.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.URL;

import ru.rsvpu.mobile.Activity.PeopleTActivity;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.ItemNewsPeopleT;

/**
 * Created by aleksej
 * on 09.11.2017.
 */

public class FragmentPagePeopleT extends Fragment {

    TextView textTitle, textMain, textDate;
    ImageView image;
    CardView card;
    ScrollView scrollView;

    private static String ARGUMENT_PAGE_NUMBER = "page_number";

    public static FragmentPagePeopleT newInstance(int page) {
        FragmentPagePeopleT pageFragment = new FragmentPagePeopleT();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_t, container, false);
        initView(view);

//        textView.setText(String.valueOf(getArguments().getInt(ARGUMENT_PAGE_NUMBER)));
        ItemNewsPeopleT news = PeopleTActivity.newsList.get(getArguments().getInt(ARGUMENT_PAGE_NUMBER));

        textTitle.setText(news.getTitle());
        textMain.setText(news.getText());
        textDate.setText("Дата публикации\t" + news.getDate());

        new Thread(() -> {
            try {
                URL url = new URL(news.getImageURL());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                getActivity().runOnUiThread(() -> image.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        scrollView.setOnClickListener(v -> openWeb(news.getUrl()));
        return view;
    }

    void initView(View v) {
        textTitle = v.findViewById(R.id.fragment_people_title);
        textMain = v.findViewById(R.id.fragment_people_text);
        textDate = v.findViewById(R.id.fragment_people_date);
        image = v.findViewById(R.id.fragment_people_image);
        card = v.findViewById(R.id.fragment_people_card);
        scrollView = v.findViewById(R.id.fragment_people_scroll);
    }

    protected void openWeb(String url) {
        CustomTabsIntent.Builder chrome = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = chrome.build();

        if (url != null)
            customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
    }
}
