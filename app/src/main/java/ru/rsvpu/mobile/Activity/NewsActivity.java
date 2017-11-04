package ru.rsvpu.mobile.Activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

import ru.rsvpu.mobile.R;


public class NewsActivity extends AppCompatActivity {

    ImageView imageHead;
    myTaskText taskText = new myTaskText();
    private final String LOG_ARGS = "activityNews";
    FloatingActionButton fab_openInWeb;
    private WebView webView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initView();
        setToolbar();
        fabListener();
        loadContent();
//        setImageHead();
    }

    void loadContent() {
        taskText.execute(getIntent().getStringExtra("url"));

    }

    void initView() {

        progressBar = findViewById(R.id.activity_news_progressBar);
        webView = findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        //webView.getSettings().setLoadWithOverviewMode(true);
        //webView.getSettings().setUseWideViewPort(true);
        //webView.getSettings().setBuiltInZoomControls(true);
//        head.setText(getIntent().getStringExtra("title"));
        fab_openInWeb = findViewById(R.id.fab);
        imageHead = findViewById(R.id.activity_news_image_head);
    }

    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle(getIntent().getIntExtra("nestedText", R.string.app_name));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    private boolean loadImage() {
        SharedPreferences setting = getSharedPreferences("setting", 0);

        return setting.getBoolean("load_image", true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void fabListener() {
        fab_openInWeb.setOnClickListener(v -> openWeb());
    }

    protected void openWeb() {
        CustomTabsIntent.Builder chrome = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = chrome.build();

        if (getIntent().getStringExtra("url") != null)
            customTabsIntent.launchUrl(this, Uri.parse(getIntent().getStringExtra("url")));
    }

    @SuppressLint("StaticFieldLeak")
    class myTaskText extends AsyncTask<String, Void, Void> {

        String newsText = "";
        Document doc;
        Elements titleElements;


        @Override
        protected Void doInBackground(String... strings) {
            for (String url : strings)
                try {
                    doc = Jsoup.connect(url).get();
                    // titleElements = doc.select("div").select(".content").select("p");
                    doc.getElementsByClass("upTag2").remove();

                    try {
                        doc.getElementById("gallery").remove();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!loadImage())
                        doc.getElementsByAttribute("src").remove();


                    titleElements = doc.select("div").select(".content");
                    String html = titleElements.html().replace("<img", "<img <img style=\"max-width: 100%; width: auto; height: auto\"");
                    newsText += "<html><body>" + html + "</body></html>";
                    Log.d(LOG_ARGS, titleElements.text());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //text.setText(newsText);
            progressBar.setVisibility(View.GONE);
            webView.loadDataWithBaseURL(null, newsText, "text/html", "en_US", null);

            new loadImg().execute(doc);
        }
    }

    class loadImg extends AsyncTask<Document, Void, Void> {
        Bitmap img;

        @Override
        protected Void doInBackground(Document... documents) {
            Document doc;
            try {

                Elements picture = documents[0].select("img");
                String urlImg = picture.get(9).attr("src");
                Log.d(LOG_ARGS, urlImg);
                Log.d(LOG_ARGS, picture.html());
                img = BitmapFactory.decodeStream(new URL(urlImg).openConnection().getInputStream());


            } catch (Exception e) {
                Log.d(LOG_ARGS, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageHead.setVisibility(View.INVISIBLE);
            imageHead.setImageBitmap(img);
            imageHead.setAlpha(0f);
            imageHead.setVisibility(View.VISIBLE);
            imageHead.animate().alpha(1f).setDuration(400).start();
        }
    }

}
