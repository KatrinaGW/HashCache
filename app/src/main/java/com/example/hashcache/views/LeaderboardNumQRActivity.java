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
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.database.DatabaseAdapter;
import com.example.hashcache.models.database.DatabasePort;
import com.example.hashcache.models.database.values.FieldNames;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * The LeaderboardNumQRActivity class extends the AppCompatActivity and is used to display the
 * leaderboard based on the number of QR codes scanned.
 */
public class LeaderboardNumQRActivity extends AppCompatActivity {
    private ArrayList<Pair<String, Long>> scores;
    /**
     * onCreate method is used to initialize the activity and is called when the activity is first created.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_numqr);

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
        /**
         * An anonymous inner class that implements the View.OnClickListener interface.
         * It is used to add the functionality to the region button.
         */
        regionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to numQR leaderboard page
                startActivity(new Intent(LeaderboardNumQRActivity.this, LeaderboardRegionActivity.class));
            }
        });
        // add functionality to score button
        AppCompatButton scoreButton = findViewById(R.id.score_tab_button);
        /**
         * An anonymous inner class that implements the View.OnClickListener interface.
         * It is used to add the functionality to the score button.
         */
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to score leaderboard page
                startActivity(new Intent(LeaderboardNumQRActivity.this, LeaderboardScoreActivity.class));
            }
        });
        // add functionality to topQR button
        AppCompatButton topButton = findViewById(R.id.topQR_tab_button);
        /**
         * An anonymous inner class that implements the View.OnClickListener interface.
         * It is used to add the functionality to the topQR button.
         */
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to topQR leaderboard page
                startActivity(new Intent(LeaderboardNumQRActivity.this, LeaderboardTopQRActivity.class));
            }
        });

        // Sets the leaderboard scores and username for Num qr
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
        playersNumQrCodes.setText(String.valueOf(playerWallet.getQrCount()));

        // Get access to the database
        DatabasePort databaseAdapter = Database.getInstance();


        // Get the text views needed to set the leaderboard
        ArrayList<TextView> userNames = new ArrayList<>();
        userNames.add(findViewById(R.id.user_one));
        userNames.add(findViewById(R.id.user_two));
        userNames.add(findViewById(R.id.user_three));

        ArrayList<TextView> qrCounts = new ArrayList<>();
        qrCounts.add(findViewById(R.id.num_one));
        qrCounts.add(findViewById(R.id.num_two));
        qrCounts.add(findViewById(R.id.num_three));

        // Fetch the values from the database needed for the leaderboards
        databaseAdapter.getTopUsers(FieldNames.QR_COUNT.fieldName).thenAccept(score -> {
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
