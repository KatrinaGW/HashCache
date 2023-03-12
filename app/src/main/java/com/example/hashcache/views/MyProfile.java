/*
 * MyProfile
 *
 * User's profile page.
 * Displays scrollable list of QR monsters scanned by the user.
 * Top logo button allows navigation to user settings page.
 * Selecting a monster navigates to that monster's info page.
 * Additional buttons permit navigation to other pages.
 */

package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.models.Player;
import com.example.hashcache.store.AppStore;
/**
 * MyProfile
 *
 * The MyProfile class is an activity that displays the user's profile page.
 *
 * It displays a scrollable list of QR monsters scanned by the user, along with their username and score. The top logo
 * button allows navigation to the user settings page. Selecting a monster navigates to that monster's info page. Additional
 * buttons permit navigation to other pages.
 *
 * @see AppCompatActivity
 * @see AppCompatButton
 * @see ImageButton
 * @see ListView
 * @see TextView
 * @see PopupMenu
 * @see MenuItem
 * @see Player
 * @see AppStore
 * @see Settings
 * @see QRStats
 * @see Community
 */
public class MyProfile extends AppCompatActivity {
    private View mPurpleRect;
    private ImageButton mLogoButton;
    private TextView mUsernameTextView;
    private TextView mScoreTextView;
    private ImageButton mMenuButton;
    private ListView mTempList;
    private AppCompatButton mQRStatsButton;
    @Override
    /**
     * Called when the activity is created.
     *
     * It sets up the functionality for the logo button, the QR STATS button, and the menu button. It also retrieves the
     * current user's information and sets the username and score on the profile page.
     *
     * @param savedInstanceState the saved state of the activity, if it was previously closed
     * @see AppStore
     * @see Player
     */

    protected void onCreate(Bundle savedInstanceState) {
        initView();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);

        // add functionality to logo button
        ImageButton logoButton = findViewById(R.id.logo_button);
        mUsernameTextView = findViewById(R.id.username_textview);
        mScoreTextView = findViewById(R.id.score_textview);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to user settings page
                startActivity(new Intent(MyProfile.this, Settings.class));
            }
        });

        // add functionality to QR STATS button
        AppCompatButton statsButton = findViewById(R.id.qr_stats_button);

        Player playerInfo = AppStore.get().getCurrentPlayer();
        setUsername(playerInfo.getUsername());
        setScore(playerInfo.getPlayerWallet().getTotalScore());
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to page displaying QR Stats
                startActivity(new Intent(MyProfile.this, QRStats.class));
            }
        });

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(MyProfile.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(MyProfile.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(MyProfile.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // remain on MyProfile
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(MyProfile.this, Community.class));
                            return true;
                        }
                        return MyProfile.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });
    }
    /**
     * Initializes the view.
     */
    private void initView() {

        mPurpleRect = findViewById(R.id.purple_rect);
        mLogoButton = findViewById(R.id.logo_button);
        mUsernameTextView = findViewById(R.id.username_textview);
        mScoreTextView = findViewById(R.id.score_textview);
        mMenuButton = findViewById(R.id.menu_button);
        mTempList = findViewById(R.id.temp_list);
        mQRStatsButton = findViewById(R.id.qr_stats_button);
    }
    /**
     * Sets the listener for the logo button.
     *
     * @param listener the listener to be set for the logo button
     * @see ImageButton
     * @see Settings
     */
    public void setLogoButtonListener(View.OnClickListener listener) {
        mLogoButton.setOnClickListener(listener);
    }
    /**
     * Sets the listener for the menu button.
     *
     * @param listener the listener to be set for the menu button
     * @see ImageButton
     * @see PopupMenu
     * @see MenuItem
     * @see AppHome
     * @see QRStats
     * @see Community
     */

    public void setMenuButtonListener(View.OnClickListener listener) {
        mMenuButton.setOnClickListener(listener);
    }
    /**
     * Sets the username displayed on the profile page.
     *
     * @param username the username to be set
     * @see TextView
     */

    public void setUsername(String username) {
        mUsernameTextView.setText(username);
    }
    /**
     * Sets the score displayed on the profile page.
     *
     * @param score the score to be set
     * @see TextView
     */
    public void setScore(int score) {
        mScoreTextView.setText("Score: " + score);
    }
    /**
     * Sets the list adapter for the list of QR monsters.
     *
     * @param adapter the adapter to be set for the list of QR monsters
     * @see ProfileListAdapter
     * @see ListView
     */
    public void setListAdapter(ProfileListAdapter adapter) {
        mTempList.setAdapter(adapter);
    }
    /**
     * Sets the empty view for the list of QR monsters.
     *
     * @param view the empty view to be set for the list of QR monsters
     * @see ListView
     */
    public void setEmptyView(View view) {
        mTempList.setEmptyView(view);
    }
    /**

     Sets the click listener for the QR Stats button on the MyProfile page.
     This method takes a View.OnClickListener as a parameter and sets it as the click listener for the mQRStatsButton,
     which is an instance of AppCompatButton. When the user clicks on the QR Stats button, the listener's onClick()
     method is called. This method can be used to perform some action when the button is clicked, such as navigating to
     the QR Stats page.
     @param listener the View.OnClickListener to set as the click listener for the QR Stats button
     */
    public void setQRStatsButtonListener(View.OnClickListener listener) {
        mQRStatsButton.setOnClickListener(listener);
    }
}
