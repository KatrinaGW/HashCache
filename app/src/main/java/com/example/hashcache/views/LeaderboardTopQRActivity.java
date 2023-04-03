package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabasePort;
import com.example.hashcache.models.database.values.FieldNames;

import java.util.ArrayList;
import java.util.Objects;

/**

 The LeaderboardTopQRActivity class is an activity that displays the leaderboard of scores based on the top QR codes scanned.

 It has a menu button that displays a popup menu of different activities to navigate to.

 The user can navigate to different leaderboards - region, number of QR codes scanned, and scores - by clicking on the respective buttons.
 */
public class LeaderboardTopQRActivity extends AppCompatActivity {
    private ArrayList<Pair<String, Long>> leaderboard;
    /**

     This method sets up the activity, inflating the layout and adding functionality to the menu button,

     region button, number of QR codes scanned button, and score button.

     @param savedInstanceState Bundle containing the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setup the activity and inflate the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_topqr);


        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the menu button is clicked.
             * It displays a popup menu of different activities to navigate to.
             *
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                BottomMenuFragment bottomMenu = new BottomMenuFragment();
                bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
            }
        });


        // add functionality to region button
        AppCompatButton regionButton = findViewById(R.id.region_tab_button);
        regionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to numQR leaderboard page
                startActivity(new Intent(LeaderboardTopQRActivity.this, LeaderboardRegionActivity.class));
            }
        });
        // add functionality to numQR button
        AppCompatButton numQRButton = findViewById(R.id.numQR_tab_button);
        numQRButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the number of QR codes scanned button is clicked.
             * It starts the LeaderboardNumQRActivity, displaying the leaderboard of scores based on the number of QR codes scanned.
             *
             * @param v The view that was clicked
             */
            @Override
            public void onClick(View v) {
                // go to numQR leaderboard page
                startActivity(new Intent(LeaderboardTopQRActivity.this, LeaderboardNumQRActivity.class));
            }
        });
        // add functionality to score button
        AppCompatButton topButton = findViewById(R.id.score_tab_button);
        topButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the score button is clicked.
             * It starts the LeaderboardScoreActivity, displaying the leaderboard of scores.
             *
             * @param v The view that was clicked
             */
            @Override
            public void onClick(View v) {
                // go to score leaderboard page
                startActivity(new Intent(LeaderboardTopQRActivity.this, LeaderboardScoreActivity.class));
            }
        });

        setLeaderboard();
    }

    /**
     * Sets the leaderboard scores
     */
    private void setLeaderboard() {
        // Update the my QR code scores
        AppContext appContext = AppContext.get();
        PlayerWallet playerWallet = appContext.getCurrentPlayer().getPlayerWallet();

        TextView playersNumQrCodes = findViewById(R.id.score_value_textview);
        playersNumQrCodes.setText(String.valueOf(playerWallet.getMaxScore()));

        // Get access to the database
        DatabasePort databaseAdapter = Database.getInstance();


        // Get the text views needed to set the leaderboard
        ArrayList<TextView> userNames = new ArrayList<>();
        userNames.add(findViewById(R.id.user_one));
        userNames.add(findViewById(R.id.user_two));
        userNames.add(findViewById(R.id.user_three));

        // Get the text views for the monster names
        ArrayList<TextView> monsterNames = new ArrayList<>();
        monsterNames.add(findViewById(R.id.monster_name_one));
        monsterNames.add(findViewById(R.id.monster_name_two));
        monsterNames.add(findViewById(R.id.monster_name_three));

        // Get the text view for the qr counts
        ArrayList<TextView> qrCounts = new ArrayList<>();
        qrCounts.add(findViewById(R.id.score_one));
        qrCounts.add(findViewById(R.id.score_two));
        qrCounts.add(findViewById(R.id.score_three));

        // Fetch the values from the database needed for the leaderboards
        databaseAdapter.getTopUsers(FieldNames.MAX_SCORE.fieldName).thenAccept(score -> {
            if (score.size() != 0) {
                int count = 0;
                for (TextView view : userNames) {
                    if (count < score.size()) {
                        view.setText(score.get(count++).getFirst());
                    } else {
                        view.setText("NA");
                    }
                }
                count = 0;
                for (TextView view : qrCounts) {
                    if(count < score.size()) {
                        view.setText(String.valueOf(score.get(count++).getSecond()));
                    } else {
                        view.setText("0");
                    }
                }

                count = 0;
                for(TextView view: monsterNames) {
                    if(count < score.size()) {
                        databaseAdapter.getTopMonsterName(score.get(count++).getThird()).thenAccept(view::setText);
                    } else {
                        view.setText("N/A");
                    }
                }

                // Find the player rating
                String userName = appContext.getCurrentPlayer().getUsername();

                // Update the player rating
                for(int i = 0; i < score.size(); i++) {
                    if(Objects.equals(score.get(i).getFirst(), userName)) {
                        TextView rankingView = findViewById(R.id.region_value_textview);
                        rankingView.setText(String.valueOf(i + 1));

                        // No need to keep looking
                        break;
                    }
                }
            } else {
                Log.e("DATABASE", "Error in getting the top k users");
            }
        });
    }
}

