package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Function;

public class DisplayCommentsActivity extends AppCompatActivity implements Observer {
    private ListView commentsList;
    private CommentsDataArrayAdapter commentsDataArrayAdapter;
    private ArrayList<Comment> comments;
    private Button addCommentButton;
    private boolean userHasScanned;


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
     * @see AppContext
     * @see Player
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comments);

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomMenuFragment bottomMenu = new BottomMenuFragment();
                bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
            }

        });

        AppContext.get().addObserver(this);
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

    private void setButtons(){
        if(userHasScanned){
            addCommentButton.setVisibility(View.VISIBLE);
            addCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AddCommentActivity.class);

                    startActivity(intent);
                }
            });
        }else{
            addCommentButton.setVisibility(View.GONE);
        }
    }

    /**
     * Called whenever this activity resumes
     */
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        userHasScanned = AppContext.get().getCurrentPlayer().getPlayerWallet().getScannedCodeIds()
                .contains(AppContext.get().getCurrentScannableCode().getScannableCodeId());
        comments = AppContext.get().getCurrentScannableCode().getComments();
        addCommentButton = findViewById(R.id.add_comment_button);
        commentsList = findViewById(R.id.comment_listview_content);
        setCommentsAdapter();
        setButtons();
    }

    /**
     * Called when the observable for this observer is updated
     * @param o     the observable object.
     * @param arg   an argument passed to the {@code notifyObservers}
     *                 method.
     */
    @Override
    public void update(Observable o, Object arg) {
        Log.d("DisplayCommentsActivity.update", "called to update");
        init();
    }
}
