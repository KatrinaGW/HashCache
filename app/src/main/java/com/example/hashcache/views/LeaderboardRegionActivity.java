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

 LeaderboardRegionActivity is an {@link AppCompatActivity} that displays the leaderboard based on the user's region.

 It includes a menu button and four navigation buttons to switch between different leaderboards.

 */

public class LeaderboardRegionActivity extends AppCompatActivity {
    /**

     Initializes the activity, sets the layout, and adds functionality to the menu and navigation buttons.
     @param savedInstanceState a bundle of the saved state of the activity, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_region);

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        /**

         {@link View.OnClickListener} that creates and displays a popup menu when the menu button is clicked.
         */
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(LeaderboardRegionActivity.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(LeaderboardRegionActivity.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(LeaderboardRegionActivity.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(LeaderboardRegionActivity.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(LeaderboardRegionActivity.this, Community.class));
                            return true;
                        }
                        return LeaderboardRegionActivity.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });

        // add functionality to score button
        AppCompatButton scoreButton = findViewById(R.id.score_tab_button);
        /**

         {@link View.OnClickListener} that starts a new activity to the score leaderboard page when the score button is clicked.
         */
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to score leaderboard page
                startActivity(new Intent(LeaderboardRegionActivity.this, LeaderboardScoreActivity.class));
            }
        });
        // add functionality to numQR button
        AppCompatButton numQRButton = findViewById(R.id.numQR_tab_button);
        /**

         {@link View.OnClickListener} that starts a new activity to the numQR leaderboard page when the numQR button is clicked.
         */
        numQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to numQR leaderboard page
                startActivity(new Intent(LeaderboardRegionActivity.this, LeaderboardNumQRActivity.class));
            }
        });
        // add functionality to topQR button
        AppCompatButton topButton = findViewById(R.id.topQR_tab_button);
        /**

         {@link View.OnClickListener} that starts a new activity to the topQR leaderboard page when the topQR button is clicked.
         */
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to topQR leaderboard page
                startActivity(new Intent(LeaderboardRegionActivity.this, LeaderboardTopQRActivity.class));
            }
        });
    }
}

