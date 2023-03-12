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

 The NewMonsterActivity class represents an activity in the app that allows users to create a new monster.
 This activity sets up the UI elements and adds functionality to the buttons, including taking a location photo
 for the new monster or skipping the photo and navigating to the MyProfile activity. The menu button is also
 functional, allowing users to navigate to different activities in the app.
 */
public class NewMonsterActivity extends AppCompatActivity {
    /**
     * Called when the activity is created.
     *
     * Initializes the activity by setting up the UI elements and adding functionality to the buttons.
     *
     * @param savedInstanceState saved state of the activity, if it was previously closed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_monster);

        // take location photo
        ImageButton photoButton = findViewById(R.id.photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to activity to take location photo
            }
        });

        // skip taking location photo (go to my profile)
        AppCompatButton skipPhotoButton = findViewById(R.id.skip_photo_button);
        skipPhotoButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the skip photo button is clicked.
             *
             * Navigates to the MyProfile activity when the skip photo button is clicked.
             *
             * @param v the view that was clicked (the skip photo button)
             */
            @Override
            public void onClick(View v) {
                // go to profile page
                startActivity(new Intent(NewMonsterActivity.this, MyProfile.class));
            }
        });

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the menu button is clicked.
             *
             * Creates a popup menu and sets up the menu items. Navigates to different activities based on the menu item
             * that was clicked.
             *
             * @param v the view that was clicked (the menu button)
             */
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(NewMonsterActivity.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    /**
                     * Called when a menu item is clicked.
                     *
                     * Navigates to the appropriate activity based on the menu item that was clicked.
                     *
                     * @param item the menu item that was clicked
                     * @return true if the menu item was handled, false otherwise
                     */
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(NewMonsterActivity.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(NewMonsterActivity.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(NewMonsterActivity.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(NewMonsterActivity.this, Community.class));
                            return true;
                        }
                        return NewMonsterActivity.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });
    }

}
