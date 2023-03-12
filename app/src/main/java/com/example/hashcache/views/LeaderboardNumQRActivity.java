package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
/**
 * The LeaderboardNumQRActivity class extends the AppCompatActivity and is used to display the
 * leaderboard based on the number of QR codes scanned.
 */
public class LeaderboardNumQRActivity extends AppCompatActivity {
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
        /**
         * onCreate method is used to initialize the activity and is called when the activity is first created.
         *
         * @param savedInstanceState A Bundle object containing the activity's previously saved state.
         */

        // Get the top 3 users sorted by the number of QR codes they have


        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(LeaderboardNumQRActivity.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(LeaderboardNumQRActivity.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(LeaderboardNumQRActivity.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(LeaderboardNumQRActivity.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(LeaderboardNumQRActivity.this, Community.class));
                            return true;
                        }
                        return LeaderboardNumQRActivity.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
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
    }
}
