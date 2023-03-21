package com.example.hashcache.tests;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.R;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.Database;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommentsArrayAdapter extends ArrayAdapter<Comment> {
    public CommentsArrayAdapter(Context context, List<Comment> comments) {
        super(context, 0, comments);
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

        Comment comment = getItem(position);

        TextView commentatorNameView = view.findViewById(R.id.comment_commentator_listview_item);
        TextView commentBodyView = view.findViewById(R.id.comment_body_listview_item);


        Database.getInstance().getUsernameById(comment.getCommentatorId())
                .thenAccept(username -> {
                    ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("CommentsArrayAdapter", "Setting name and body");
                            commentBodyView.setText(comment.getBody());
                            commentatorNameView.setText(username);
                        }
                    });
                });

        return view;
    }
}
