/*
 * Community
 *
 * Community/social page of the app.
 * Displays a scrollable list of other app users,
 * and search bar for searching for users by username.
 * Clicking on a user navigates to their profile page.
 * Additional buttons permit navigation to other pages.
 */

package com.example.hashcache.views;
/*
 * Community
 *
 * Community/social page of the app.
 * Displays a scrollable list of other app users,
 * and search bar for searching for users by username.
 * Clicking on a user navigates to their profile page.
 * Additional buttons permit navigation to other pages.
 */

import android.R.layout;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.models.PlayerList;

import java.util.ArrayList;
/**
 * The Community class represents the community/social page of the app. It displays a scrollable list of other app users
 * and provides a search bar for searching for users by username. Clicking on a user navigates to their profile page.
 * Additional buttons permit navigation to other pages.
 */
public class Community extends AppCompatActivity {
    private ImageButton mMenuButton;
    private ImageButton mSearchButton;
    private EditText mSearchEditText;
    private ListView mUserListView;
    private AppCompatButton mLeaderboardButton;
    private ArrayList<String> userResults;
    private ListView searchResultsView;
    @Override
    /**
     * Initializes the view elements and sets the click listeners for the menu button, search button, and leaderboard button.
     *
     * @param savedInstanceState the saved instance state
     */
    protected void onCreate(Bundle savedInstanceState) {
        initView();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community);

        // add functionality to leaderboard button
        AppCompatButton logoButton = findViewById(R.id.leaderboard_button);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Community.this, LeaderboardScoreActivity.class));
            }
        });

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

        // Handles the search button
        EditText searchBarText = findViewById(R.id.search_bar_edittext);
        searchResultsView = findViewById(R.id.user_listview);
        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PlayerList.getInstance().searchPlayers(searchBarText.getText().toString(), 10)
                                .thenAccept(searchResults->{
                                    userResults = searchResults;
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), layout.simple_list_item_1, searchResults);
                                    searchResultsView.setAdapter(adapter);

                                    setListViewItemClickListener();
                                });
                    }
                });
            }
        });

    }

    private void setListViewItemClickListener(){
        searchResultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectUsername = userResults.get(position);
                Intent intent = new Intent(getApplicationContext(), OtherProfileInformationActivity.class);
                intent.putExtra("otherUsername", selectUsername);
                startActivity(intent);
            }
        });
    }
    private void initView() {
        mMenuButton = findViewById(R.id.menu_button);
        mSearchButton = findViewById(R.id.search_button);
        mSearchEditText = findViewById(R.id.search_bar_edittext);
        mUserListView = findViewById(R.id.user_listview);
        mLeaderboardButton = findViewById(R.id.leaderboard_button);
    }
    /**

     Sets a listener for menu button in the Community activity.
     @param listener A listener for menu button.
     */
    public void setMenuButtonListener(View.OnClickListener listener) {
        mMenuButton.setOnClickListener(listener);
    }
    /**

     Sets a listener for search button in the Community activity.
     @param listener A listener for search button.
     */
    public void setSearchButtonListener(View.OnClickListener listener) {
        mSearchButton.setOnClickListener(listener);
    }
    /**

     Returns the search query in the search bar of Community activity.
     @return The search query in the search bar of Community activity.
     */
    public String getSearchQuery() {
        return mSearchEditText.getText().toString();
    }
    /**

     Sets the search query in the search bar of Community activity.
     @param query The search query to set in the search bar of Community activity.
     */
    public void setSearchQuery(String query) {
        mSearchEditText.setText(query);
    }

    public void setUserListViewEmptyView(View view) {
        mUserListView.setEmptyView(view);
    }
    /**

     Sets a listener for the leaderboard button in the Community activity.
     @param listener A listener for the leaderboard button.
     */
    public void setLeaderboardButtonListener(View.OnClickListener listener) {
        mLeaderboardButton.setOnClickListener(listener);
    }
}
