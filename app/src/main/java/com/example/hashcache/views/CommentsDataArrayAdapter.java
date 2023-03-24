package com.example.hashcache.views;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.R;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.database.Database;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class CommentsDataArrayAdapter extends ArrayAdapter<Pair<String, String>> {
    public CommentsDataArrayAdapter(Context context, ArrayList<Pair<String, String>> commentsData) {
        super(context, 0, commentsData);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.comment_listview_content,
                    parent, false);
        } else {
            view = convertView;
        }

        Pair<String, String> commentData = getItem(position);

        TextView commentatorNameView = view.findViewById(R.id.comment_commentator_listview_item);
        TextView commentBodyView = view.findViewById(R.id.comment_body_listview_item);
        commentBodyView.setText(commentData.second);
        commentatorNameView.setText(commentData.first);


        return view;
    }
}
