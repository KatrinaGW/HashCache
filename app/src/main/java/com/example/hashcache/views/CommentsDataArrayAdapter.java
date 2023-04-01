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

/**

 An ArrayAdapter for displaying comment data in a ListView.
 This adapter is used to populate a ListView with comment data, where each comment is represented
 by a Pair<String, String> object containing the commentator's name and the comment body.
 This adapter inflates the comment_listview_content layout for each row in the ListView and sets the commentator name and comment body text.
 */
public class CommentsDataArrayAdapter extends ArrayAdapter<Pair<String, String>> {
    /**
     * Constructs a new CommentsDataArrayAdapter.
     *
     * @param context The context in which this adapter is being used.
     * @param commentsData The comment data to be displayed in the ListView.
     */
    public CommentsDataArrayAdapter(Context context, ArrayList<Pair<String, String>> commentsData) {
        super(context, 0, commentsData);
    }
    /**
     * Returns the View for the specified position in the ListView.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return The View corresponding to the specified position in the ListView.
     */
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
