package ru.rsvpu.mobile.Fragments;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import ru.rsvpu.mobile.Activity.PeopleTActivity;
import ru.rsvpu.mobile.Adapters.RVAdapterNews;
import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.ItemNews;
import ru.rsvpu.mobile.items.MyNetwork;
import ru.rsvpu.mobile.items.TabletHelper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Thread.sleep;

/**
 * Created by aleksej
 * on 12.10.2017.
 */

public class FragmentNews extends Fragment {

    public String URL = "http://www.rsvpu.ru/news/";

    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeToRefresh;
    private RVAdapterNews adapter;
    protected int color;
    private FrameLayout mainFrame;
    private View noSignal;
    private RecyclerView rv;
    private FloatingActionButton fabPeopleT;
    private String LOG_ARGS = "FragmentNews";

    public FragmentNews() {
        color = R.color.news;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_news, container, false);
        view.setBackgroundColor(getResources().getColor(color));
        initView(view);
        swipeToRefresh.setOnRefreshListener(() -> new LoadNews().execute());

        new LoadNews().execute();

        fabPeopleT.setOnClickListener(v -> startActivity(new Intent(getActivity(), PeopleTActivity.class)));
        startAnimationFab();

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
//        startAnimationFab();
        Log.d(LOG_ARGS, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_ARGS,"onPause");
//        fabPeopleT = getActivity().findViewById(R.id.fragment_news_fab_people_t);
    }

    @Override
    public void onStart() {
        super.onStart();
//        new Thread(() -> {
//            try {
//                sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            getActivity().runOnUiThread(this::startAnimationFab);
//        }).start();
//        startAnimationFab();

        Log.d(LOG_ARGS,"onStart");
    }

    public void initView(View v) {
        rv = v.findViewById(R.id.fragment_news_recyclerView);
        adapter = new RVAdapterNews(getActivity());
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new GridLayoutManager(getActivity(), TabletHelper.getDisplayColumns(getActivity())));
        rv.setAdapter(adapter);

        progressBar = v.findViewById(R.id.fragment_news_progressBar);
        swipeToRefresh = v.findViewById(R.id.fragment_news_SwipeToRefresh);

        mainFrame = v.findViewById(R.id.fragment_news_mainFrame);
        noSignal = v.findViewById(R.id.fragment_news_no_connection);

        fabPeopleT = v.findViewById(R.id.fragment_news_fab_people_t);
    }

    void startAnimationFab() {
        fabPeopleT.setScaleX(1.0f);
        fabPeopleT.setScaleY(1.0f);
        fabPeopleT.clearAnimation();
        fabPeopleT.animate().setStartDelay(1500).scaleX(1.1f).scaleY(1.1f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                fabPeopleT.clearAnimation();
                fabPeopleT.setScaleX(1.1f);
                fabPeopleT.setScaleY(1.1f);
                fabPeopleT.animate().setStartDelay(0).scaleY(1f).scaleX(1f).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startAnimationFab();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    @SuppressLint("StaticFieldLeak")
    class LoadNews extends AsyncTask<Void, Void, Void> {

        Document document;

        Elements newsGet;
        Elements newsTime;
        Elements newsURL;
        Elements newsPicture;
        Elements newsTitle;
        ItemNews[] arrayItemNews;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeToRefresh.setRefreshing(true);
            if (MyNetwork.isWorking(getActivity())) {
                noSignal.setVisibility(GONE);
            } else {
                noSignal.setVisibility(VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (MyNetwork.isWorking(getActivity())) {
                    document = Jsoup.connect(URL).get();
                    newsTitle = document.select("dd").select(".newsname");
                    newsGet = document.select("dd");
                    newsURL = document.select("dd").select(".img");
                    newsPicture = document.select("dd").select("img");
                    newsTime = document.select("dt").select("span");


                    String LOG_ARGS = "FragmentNews";
                    Log.d(LOG_ARGS, String.valueOf(newsTitle.size()));

                    arrayItemNews = new ItemNews[newsTime.size()];

                    for (int i1 = 0; i1 < arrayItemNews.length; i1++) {
                        arrayItemNews[i1] = new ItemNews();
                    }

                    int i = 0;
                    for (Element timeElement : newsTime) {
                        arrayItemNews[i].setDate(timeElement.text());
//                    Log.d(LOG_ARGS, timeElement.text());
                        i++;
                    }

                    i = 0;
                    for (Element titleElement : newsTitle) {
                        arrayItemNews[i].setTitle(titleElement.text());
//                    Log.d(LOG_ARGS, titleElement.text());
                        i++;
                    }

                    i = 1;
                    int specialI = 0;
                    for (Element newsElement : newsGet) {

                        if (i % 3 == 0) {
                            arrayItemNews[specialI].setNews(newsElement.text());
                            specialI++;
                            // Log.d(LOG_ARGS, newsElement.text()+"\n"+i);
                        }

                        i++;
                    }

                    i = 0;
                    for (Element pictureElement : newsURL) {
                        String[] newsString = pictureElement.html().split("=\"");
                        arrayItemNews[i].setUrl_news(newsString[1].substring(0, newsString[1].length() - 10));
//                    Log.d(LOG_ARGS, newsString[1].substring(0, newsString[1].length() - 10));

                        i++;
                    }
                    i = 0;
                    for (Element pictureURL : newsPicture) {

//                    java.net.URL url = new URL(pictureURL.absUrl("src"));
//                    arrayItemNews[i].setPicture(BitmapFactory.decodeStream(url.openConnection().getInputStream()));

                        arrayItemNews[i].setUrl_picture(pictureURL.absUrl("src"));
//                    Log.d(LOG_ARGS, pictureURL.absUrl("src"));
                        i++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (MyNetwork.isWorking(getActivity())) {
                rv.setVisibility(VISIBLE);
                adapter.addArray(arrayItemNews);
            } else {
                rv.setVisibility(GONE);
            }
            progressBar.setVisibility(GONE);
            swipeToRefresh.setRefreshing(false);
        }
    }

}
