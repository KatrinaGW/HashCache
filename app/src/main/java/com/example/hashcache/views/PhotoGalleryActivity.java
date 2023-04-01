package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.R;


/**

 The PhotoGalleryActivity displays all the location photos for a scannable code.
 This activity sets up the UI elements (a scrollable list of photos and associated location text).
 The back button allows users to return to the monster info page for the scannable code.
 The menu button allows users to navigate to different activities in the app.
 */
public class PhotoGalleryActivity extends AppCompatActivity {
    private ListView photoList;
    private TextView monsterName;
    private ImageButton menuButton;
    private Button backButton;

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
        setContentView(R.layout.activity_photo_gallery);

        Intent intent = getIntent();

        initializeViews();

        // add functionality to back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClicked();
            }
        });

        // add functionality to menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onMenuButtonClicked(); }
        });
    }

    private void initializeViews() {
        monsterName = findViewById(R.id.monster_name);
        menuButton = findViewById(R.id.menu_button);
        backButton = findViewById(R.id.back_button);
        photoList = findViewById(R.id.photo_list);
    }

    // go back to monster info activity when back button clicked
    private void onBackButtonClicked() {
        // end activity
        finish();
    }

    // open menu when button clicked
    private void onMenuButtonClicked() {
        BottomMenuFragment bottomMenu = new BottomMenuFragment();
        bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
    }


}
