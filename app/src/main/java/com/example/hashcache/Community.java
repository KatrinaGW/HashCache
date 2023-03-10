/*
 * Community
 *
 * Community/social page of the app.
 * Displays a scrollable list of other app users,
 * and search bar for searching for users by username.
 * Clicking on a user navigates to their profile page.
 * Additional buttons permit navigation to other pages.
 */

package com.example.hashcache;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

public class Community extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community);

        // add functionality to leaderboard button
//        ImageButton logoButton = findViewById(R.id.leaderboard_button);
//        logoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Community.this, Leaderboard.class));
//            }
//        });

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create main menu
                PopupMenu menu = new PopupMenu(Community.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(Community.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(Community.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(Community.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // remain on Community page
                            return true;
                        }
                        return Community.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });
    }
}
