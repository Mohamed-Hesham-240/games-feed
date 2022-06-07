package com.example.gamesfeed;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {
    private String urlString;

    public ArticleLoader(@NonNull Context context, String urlString) {
        super(context);
        this.urlString = urlString;
    }

    @Nullable
    @Override
    public List<Article> loadInBackground() {
        return Utils.giveMeTheArrayList(urlString);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
