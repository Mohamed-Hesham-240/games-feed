package com.example.gamesfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ArticleAdapter extends ArrayAdapter<Article> {
    //constructor
    public ArticleAdapter(Context context, ArrayList<Article> list) {
        super(context, 0, list);
    }

    //Extracting the date helper method
    private String extractDate(String fullDate) {
        return fullDate.substring(0, 10);
    }

    //overriding the getView method
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Article currentArticle = getItem(position);
        View listItemView = convertView;
        //if there is not scrap views, inflate a new view according to list_item layout
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        //updating the title
        TextView title = listItemView.findViewById(R.id.title);
        title.setText(currentArticle.getTitle());
        //updating the section
        TextView section = listItemView.findViewById(R.id.section);
        section.setText(currentArticle.getSection());
        //updating the date
        TextView date = listItemView.findViewById(R.id.date);
        date.setText(extractDate(currentArticle.getDate()));
        //updating the author
        TextView author = listItemView.findViewById(R.id.author);
        author.setText(currentArticle.getAuthor());
        //updating the image
        ImageView thumbnail = listItemView.findViewById(R.id.thumbnail);
        thumbnail.setImageBitmap(currentArticle.getThumbnail());
        return listItemView;
    }
}
