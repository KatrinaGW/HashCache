/*
 * AppHome
 *
 * Main landing page of the app.
 * Displays a map centred on the user's location,
 * with pins indicating QR codes scanned by user and others.
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

public class AppHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_home);

        // add functionality to logo button
        ImageButton logoButton = findViewById(R.id.logo_button);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to user's profile page
                startActivity(new Intent(AppHome.this, MyProfile.class));
            }
        });

        // add functionality to map button
        ImageButton qrLocationButton = findViewById(R.id.map_button);
        qrLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to page showing QR codes near the user (as list)
                startActivity(new Intent(AppHome.this, QRByLocation.class));
            }
        });

        // add functionality to community button
        ImageButton communityButton = findViewById(R.id.community_button);
        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to community page
                startActivity(new Intent(AppHome.this, Community.class));
            }
        });

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(AppHome.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // remain on AppHome page
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(AppHome.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(AppHome.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(AppHome.this, Community.class));
                            return true;
                        }
                        return AppHome.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });
    }
}
