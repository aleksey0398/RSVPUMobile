package ru.rsvpu.mobile.items;

import android.graphics.Bitmap;

/**
 * Created by aleksej on 10.08.16.
 * Recreated by aleksej on 11.10.17.
 */
public class itemNews {

    private String date;
    private String title;
    private String news;
    private String url_picture;
    private String url_news;
    private Bitmap picture;

    public itemNews(){

    }

//    public itemNews(String date, String title, String news, String url_picture, String url_news, Bitmap picture) {
//        this.date = date;
//        this.title = title;
//        this.news = news;
//        this.url_picture = url_picture;
//        this.url_news = url_news;
//        this.picture = picture;
//    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getUrl_picture() {
        return url_picture;
    }

    public void setUrl_picture(String url_picture) {
        this.url_picture = url_picture;
    }

    public String getUrl_news() {
        return url_news;
    }

    public void setUrl_news(String url_news) {
        this.url_news = url_news;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
