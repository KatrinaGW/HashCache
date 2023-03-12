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

public class MyProfile extends AppCompatActivity {
    private View mPurpleRect;
    private ImageButton mLogoButton;
    private TextView mUsernameTextView;
    private TextView mScoreTextView;
    private ImageButton mMenuButton;
    private ListView mTempList;
    private AppCompatButton mQRStatsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initView();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);

        // add functionality to logo button
        ImageButton logoButton = findViewById(R.id.logo_button);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to user settings page
                startActivity(new Intent(MyProfile.this, Settings.class));
            }
        });

        // add functionality to QR STATS button
        AppCompatButton statsButton = findViewById(R.id.qr_stats_button);
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
    private void initView() {

        mPurpleRect = findViewById(R.id.purple_rect);
        mLogoButton = findViewById(R.id.logo_button);
        mUsernameTextView = findViewById(R.id.username_textview);
        mScoreTextView = findViewById(R.id.score_textview);
        mMenuButton = findViewById(R.id.menu_button);
        mTempList = findViewById(R.id.temp_list);
        mQRStatsButton = findViewById(R.id.qr_stats_button);
    }

    public void setLogoButtonListener(View.OnClickListener listener) {
        mLogoButton.setOnClickListener(listener);
    }

    public void setMenuButtonListener(View.OnClickListener listener) {
        mMenuButton.setOnClickListener(listener);
    }

    public void setUsername(String username) {
        mUsernameTextView.setText(username);
    }

    public void setScore(int score) {
        mScoreTextView.setText("Score: " + score);
    }

    public void setListAdapter(ProfileListAdapter adapter) {
        mTempList.setAdapter(adapter);
    }

    public void setEmptyView(View view) {
        mTempList.setEmptyView(view);
    }

    public void setQRStatsButtonListener(View.OnClickListener listener) {
        mQRStatsButton.setOnClickListener(listener);
    }
}
