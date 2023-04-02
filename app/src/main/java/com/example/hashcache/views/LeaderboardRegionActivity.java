package com.example.hashcache.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.example.hashcache.models.CodeMetadata;
import com.example.hashcache.models.ScannableCode;
import com.firebase.geofire.GeoLocation;

import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.database.Database;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**

 LeaderboardRegionActivity is an {@link AppCompatActivity} that displays the leaderboard based on the user's region.

 It includes a menu button and four navigation buttons to switch between different leaderboards.

 */

public class LeaderboardRegionActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        /**
         *
         *
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
        setLeaderboard();
    }

    private void setLeaderboard() {

        // Sets the players numb qr codes
        TextView playersMaxScore = findViewById(R.id.score_value_textview);

        // Get the text views needed to set the leaderboard
        ArrayList<TextView> userNames = new ArrayList<>();
        userNames.add(findViewById(R.id.user_one));
        userNames.add(findViewById(R.id.user_two));
        userNames.add(findViewById(R.id.user_three));

        ArrayList<TextView> monsterNames = new ArrayList<>();
        monsterNames.add(findViewById(R.id.monster_name_one));
        monsterNames.add(findViewById(R.id.monster_name_two));
        monsterNames.add(findViewById(R.id.monster_name_three));

        ArrayList<TextView> maxScores = new ArrayList<>();
        maxScores.add(findViewById(R.id.score_one));
        maxScores.add(findViewById(R.id.score_two));
        maxScores.add(findViewById(R.id.score_three));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set the score of the player
        TextView scoreView = findViewById(R.id.score_value_textview);

        // Will return if the user does not have the correct permissions set
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Database.getInstance().getScannableCodesWithinRadiusSorted(location).thenAccept(data -> {

                                int count = 0;
                                for(TextView view: monsterNames) {
                                    if(count < data.size()) {
                                        view.setText(data.get(count++).second.getHashInfo().getGeneratedName());
                                    }
                                }

                                count = 0;
                                for(TextView view: maxScores) {
                                    if(count < data.size()) {
                                        view.setText(String.valueOf(data.get(count++).second.getHashInfo().getGeneratedScore()));
                                    }
                                }

                                String playerUserId = AppContext.get().getCurrentPlayer().getUserId();
                                // Use to make sure there rank is correct. And not associated with lowest scoring
                                boolean got_rank = false;
                                ArrayList<String> userIds = new ArrayList<>();
                                TextView rankView = findViewById(R.id.region_value_textview);
                                // Set the score of the player
                                TextView scoreView = findViewById(R.id.score_value_textview);

                                int j = 1;
                                // Fetch the data base for user ids while also getting the players ranking
                                // and score
                                for(Pair<String, ScannableCode> pair: data) {
                                    userIds.add(pair.first);
                                    if(Objects.equals(pair.first, playerUserId) && !got_rank) {
                                        rankView.setText(String.valueOf(j));
                                        long userScore = pair.second.getHashInfo().getGeneratedScore();
                                        Log.i("UserScore: ", String.valueOf(userScore));
                                        scoreView.setText(String.valueOf(userScore));
                                        got_rank = true;
                                    }
                                    j += 1;
                                }



                                Database.getInstance().getUsernamesByIds(userIds).thenAccept(names -> {
                                    int i = 0;
                                    Log.i("DATA" ,"here");

                                    for(TextView view: userNames) {
                                        if(i < names.size()) {
                                            view.setText(names.get(i++).second);
                                        }
                                    }
                                }).exceptionally(new Function<Throwable, Void>() {
                                    @Override
                                    public Void apply(Throwable throwable) {
                                        return null;
                                    }
                                });

                            }).exceptionally(new Function<Throwable, Void>() {
                                @Override
                                public Void apply(Throwable throwable) {

                                    return null;
                                }
                            });
                        }
                    }
                });
    }
}

