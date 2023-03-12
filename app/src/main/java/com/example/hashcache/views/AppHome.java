/*
 * AppHome
 *
 * Main landing page of the app.
 * Displays a map centred on the user's location,
 * with pins indicating QR codes scanned by user and others.
 * Additional buttons permit navigation to other pages.
 */

package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;
/**

 Represents the main landing page of the app.

 Displays a map centered on the user's location, with pins indicating QR codes scanned by the user and others.
 */
public class AppHome extends AppCompatActivity {

    private ImageButton mLogoButton;
    private TextView mUsernameTextView;
    private TextView mScoreTextView;
    private ImageButton mMenuButton;
    private ImageButton mMapButton;
    private ImageButton mCommunityButton;
    private View mTempMap;
    private AppCompatButton mScanQrButton;
    private Player playerInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_home);
        initView();

        TextView playerName = findViewById(R.id.username_textview);

        playerInfo = AppStore.get().getCurrentPlayer();
        setUsername(playerInfo.getUsername());
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
        Button qrCodeTakeButton = findViewById(R.id.scan_qr_button);
        qrCodeTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppHome.this, QRScanActivity.class));
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

    private void initView() {
        mLogoButton = findViewById(R.id.logo_button);
        mUsernameTextView = findViewById(R.id.username_textview);
        mScoreTextView = findViewById(R.id.score_textview);
        mMenuButton = findViewById(R.id.menu_button);
        mMapButton = findViewById(R.id.map_button);
        mCommunityButton = findViewById(R.id.community_button);
        mTempMap = findViewById(R.id.temp_map);
        mScanQrButton = findViewById(R.id.scan_qr_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Database.getInstance()
                .getPlayerWalletTotalScore(playerInfo.getPlayerWallet().getScannedCodeIds())
                .thenAccept(totalScore -> {
                    this.setScore(totalScore);
                });
    }
    /**

     Sets the listener for the logo button.
     @param listener the listener to set
     */
    public void setLogoButtonListener(View.OnClickListener listener) {
        mLogoButton.setOnClickListener(listener);
    }
    /**

     Sets the listener for the menu button.
     @param listener the listener to set
     */
    public void setMenuButtonListener(View.OnClickListener listener) {
        mMenuButton.setOnClickListener(listener);
    }
    /**

     Sets the listener for the map button.
     @param listener the listener to set
     */
    public void setMapButtonListener(View.OnClickListener listener) {
        mMapButton.setOnClickListener(listener);
    }
    /**

     Sets the listener for the community button.
     @param listener the listener to set
     */
    public void setCommunityButtonListener(View.OnClickListener listener) {
        mCommunityButton.setOnClickListener(listener);
    }
    /**

     Sets the listener for the QR scan button.
     @param listener the listener to set
     */
    public void setScanQrButtonListener(View.OnClickListener listener) {
        mScanQrButton.setOnClickListener(listener);
    }
    /**

     Gets the username of the player.
     @return the username of the player
     */
    public String getUsername() {
        return mUsernameTextView.getText().toString();
    }
    /**

     Sets the username of the player.
     @param username the username of the player
     */
    public void setUsername(String username) {
        mUsernameTextView.setText(username);
    }
    /**

     Gets the score of the player.
     @return the score of the player
     */
    public int getScore() {
        String scoreStr = mScoreTextView.getText().toString().replace("Score: ", "");
        return Integer.parseInt(scoreStr);
    }
    /**

     Sets the score of the player.
     @param score the score of the player
     */
    public void setScore(long score) {
        mScoreTextView.setText("Score: " + score);
    }
    /**

     Sets the listener for the temporary map view.
     @param listener the listener to set
     */
    public void setMapTempViewClickListener(OnClickListener listener) {
        mTempMap.setOnClickListener(listener);
    }

}
