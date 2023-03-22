package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.R;
import com.example.hashcache.context.Context;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DisplayCommentsActivity extends AppCompatActivity {
    private ListView commentsList;
    private CommentsDataArrayAdapter commentsDataArrayAdapter;
    private ArrayList<Comment> comments;
    private boolean belongToCurrentUser;


    @Override
    /**
     * Called when the activity is created.
     *
     * It sets up the functionality for the logo button, the QR STATS button, and
     * the menu button. It also retrieves the
     * current user's information and sets the username and score on the profile
     * page.
     *
     * @param savedInstanceState the saved state of the activity, if it was
     *                           previously closed
     * @see Context
     * @see Player
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comments);

        Intent intent = getIntent();
        belongToCurrentUser = intent.getBooleanExtra("belongsToCurrentUser", false);

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomMenuFragment bottomMenu = new BottomMenuFragment();
                bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
            }

        });
    }

    private void setCommentsAdapter(){
        ArrayList<String> userIds = new ArrayList<>();
        HashMap<String, ArrayList<String>> commentatorIdTextBody = new HashMap<>();
        ArrayList<Pair<String, String> >userNameTextBody = new ArrayList<>();
        String commentatorId;

        for(Comment comment : comments){
            commentatorId = comment.getCommentatorId();
            userIds.add(commentatorId);
            if(commentatorIdTextBody.containsKey(commentatorId)){
                commentatorIdTextBody.get(commentatorId).add(comment.getBody());
            }else{
                ArrayList<String> newList = new ArrayList<>();
                newList.add(comment.getBody());
                commentatorIdTextBody.put(commentatorId, newList);
            }
        }

        DisplayCommentsActivity activityContext = this;

        Database.getInstance().getUsernamesByIds(userIds)
                .thenAccept(userIdsNames -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(Pair<String, String> idName : userIdsNames){
                                userNameTextBody.add(new Pair<>(idName.second,
                                        commentatorIdTextBody.get(idName.first).get(0)));
                                commentatorIdTextBody.get(idName.first).remove(0);
                            }

                            commentsDataArrayAdapter = new CommentsDataArrayAdapter(activityContext,
                                    userNameTextBody);
                            commentsList.setAdapter(commentsDataArrayAdapter);
                        }
                    });
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        comments = Context.get().getCurrentScannableCode().getComments();
        commentsList = findViewById(R.id.comment_listview_content);
        setCommentsAdapter();
    }
}
