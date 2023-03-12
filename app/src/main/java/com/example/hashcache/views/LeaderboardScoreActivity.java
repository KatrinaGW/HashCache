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

public class LeaderboardScoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_score);

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(LeaderboardScoreActivity.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(LeaderboardScoreActivity.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(LeaderboardScoreActivity.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(LeaderboardScoreActivity.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(LeaderboardScoreActivity.this, Community.class));
                            return true;
                        }
                        return LeaderboardScoreActivity.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });

        // add functionality to region button
        AppCompatButton regionButton = findViewById(R.id.region_tab_button);
        regionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to region leaderboard page
                startActivity(new Intent(LeaderboardScoreActivity.this, LeaderboardRegionActivity.class));
            }
        });
        // add functionality to numQR button
        AppCompatButton numQRButton = findViewById(R.id.numQR_tab_button);
        numQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to numQR leaderboard page
                startActivity(new Intent(LeaderboardScoreActivity.this, LeaderboardNumQRActivity.class));
            }
        });
        // add functionality to topQR button
        AppCompatButton topButton = findViewById(R.id.topQR_tab_button);
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to topQR leaderboard page
                startActivity(new Intent(LeaderboardScoreActivity.this, LeaderboardTopQRActivity.class));
            }
        });
    }
}

