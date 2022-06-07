package com.example.gamesfeed;

import android.graphics.Bitmap;

public class Article {

    private String date;
    private String urlString;
    private String title;
    private String section;
    private Bitmap thumbnail;
    private String author;

    public Article(String date, String urlString, String title, String section, Bitmap thumbnail, String author) {
        this.date = date;
        this.urlString = urlString;
        this.title = title;
        this.section = section;
        this.author = author;
        this.thumbnail = thumbnail;
    }

    public String getUrlString() {
        return urlString;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getSection() {
        return section;
    }

    public String getDate() {
        return date;
    }
}
