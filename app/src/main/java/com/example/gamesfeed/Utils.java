package com.example.gamesfeed;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final String LOG_TAG = MainActivity.class.getName();

    //constructor
    private Utils() {
    }

    //json parsing
    private static List<Article> parseJson(String jsonResponse) {
        ArrayList<Article> articles = new ArrayList<>();
        if (jsonResponse == null || jsonResponse.length() < 1) {
            return null;
        }
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); ++i) {
                JSONObject resultObject = results.getJSONObject(i);
                String date = resultObject.optString("webPublicationDate");
                String urlString = resultObject.optString("webUrl");
                String title = resultObject.optString("webTitle");
                String section = resultObject.optString("sectionName");
                JSONObject fieldObject = resultObject.getJSONObject("fields");
                String thumbnail = fieldObject.optString("thumbnail");
                String author = fieldObject.optString("byline");
                Bitmap bitmap = downloadImage(thumbnail);
                articles.add(new Article(date, urlString, title, section, bitmap, author));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing the json response", e);
        }
        return articles;
    }

    //creating the url function
    private static URL createUrl(String urlString) {
        URL url = null;
        if (urlString != null && urlString.length() > 0) {
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error creating the url", e);
            }
        }
        return url;
    }

    //fetching the image from the web
    private static Bitmap downloadImage(String imageStringUrl) {
        URL imageUrl = createUrl(imageStringUrl);
        Bitmap bitmap = null;
        try {
            InputStream inputStream = imageUrl.openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error downloading photo", e);
        }
        return bitmap;
    }

    //making the http request
    private static String makeHttpRequest(String urlString) {
        URL url = createUrl(urlString);
        String jsonResponse = "";
        if (url == null)
            return "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making the connection", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing the input stream", e);
                }
            }
        }
        return jsonResponse;
    }

    //reading from the stream
    private static String readFromStream(InputStream inputStream) throws IOException {
        if (inputStream == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }

    //using all the previous methods here
    public static List<Article> giveMeTheArrayList(String url) {
        String jsonResponse = makeHttpRequest(url);
        return parseJson(jsonResponse);
    }

    //building the url queries method
    public static String buildTheUrl(String baseUrl) {
        Uri baseUri = Uri.parse(baseUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("q", "video games");
        uriBuilder.appendQueryParameter("lang", "en");
        uriBuilder.appendQueryParameter("page-size", "50");
        uriBuilder.appendQueryParameter("from-date", "2010-01-01");
        uriBuilder.appendQueryParameter("lang", "en");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,byline");
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("section", "games|film|media-sport");
        uriBuilder.appendQueryParameter("api-key", "test");
        return uriBuilder.toString();
    }

    //
}
