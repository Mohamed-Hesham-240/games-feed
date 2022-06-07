package com.example.gamesfeed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    private ArticleAdapter articleAdapter;
    private static TextView emptyView;
    private static ProgressBar progressBar;
    private static String baseUrl = "https://content.guardianapis.com/search";

    //helper method for going to the intent
    private void goToSite(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //loader overrides
    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ArticleLoader(this, Utils.buildTheUrl(baseUrl));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {
        progressBar.setVisibility(View.GONE);
        articleAdapter.clear();
        if (data == null)
            emptyView.setText(getString(R.string.server_error));
        else {
            articleAdapter.addAll(data);
            emptyView.setText(getString(R.string.no_articles_found));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        articleAdapter.clear();
    }

    //overriding the onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checking the network connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        ListView listView = findViewById(R.id.list);  //the list
        emptyView = findViewById(R.id.empty_view);
        progressBar = findViewById(R.id.progress_bar);
        listView.setEmptyView(emptyView);
        articleAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        listView.setAdapter(articleAdapter);  //connecting the list and the adapter
        if (isConnected)
            getSupportLoaderManager().initLoader(0, null, this);
        else {
            progressBar.setVisibility(View.GONE);
            emptyView.setText(getString(R.string.no_internet_connection));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String siteUrl = articleAdapter.getItem(i).getUrlString();
                goToSite(siteUrl);
            }
        });
        //the refresh listener
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeColors(getColor(R.color.primary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected)
                    getSupportLoaderManager().initLoader(0, null, MainActivity.this);
                else {
                    progressBar.setVisibility(View.GONE);
                    articleAdapter.clear();
                    emptyView.setText(getString(R.string.no_internet_connection));
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}