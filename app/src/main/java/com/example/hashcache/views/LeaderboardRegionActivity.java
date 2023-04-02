package com.example.hashcache.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import com.firebase.geofire.GeoLocation;
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

import java.util.ArrayList;
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


        // Sets the players numb qr codes
        TextView playersTotalScore = findViewById(R.id.score_value_textview);
        AtomicLong playerScores = new AtomicLong();
        Database.getInstance()
                .getTotalScore(AppContext.get().getCurrentPlayer().getUserId())
                .thenAccept(score -> {
                    playerScores.set(score);
                });

        playersTotalScore.setText(String.valueOf(playerScores));

        // Get the text views needed to set the leaderboard
        ArrayList<TextView> userNames = new ArrayList<>();
        userNames.add(findViewById(R.id.user_one));
        userNames.add(findViewById(R.id.user_two));
        userNames.add(findViewById(R.id.user_three));

        for (TextView view : userNames) {
            view.setText("Temp");
        }

        ArrayList<TextView> monsterNames = new ArrayList<>();
        monsterNames.add(findViewById(R.id.user_one));
        monsterNames.add(findViewById(R.id.user_two));
        monsterNames.add(findViewById(R.id.user_three));

        for (TextView view : monsterNames) {
            view.setText("Zorg");
        }

        ArrayList<TextView> totalScores = new ArrayList<>();
        totalScores.add(findViewById(R.id.score_one));
        totalScores.add(findViewById(R.id.score_two));
        totalScores.add(findViewById(R.id.score_three));

        for (TextView view : totalScores) {
            view.setText(String.valueOf(42));
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Database.getInstance().getCodeMetadataWithinRadius(new GeoLocation(
                                    location.getLatitude(), location.getLongitude()), 1000)
                                    .thenAccept(codes -> {
                                        System.out.println("Here");
                                    })
                                    .exceptionally(new Function<Throwable, Void>() {
                                        @Override
                                        public Void apply(Throwable throwable) {
                                            return null;
                                        }
                                    });
                        }
                    }
                });

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomMenuFragment bottomMenu = new BottomMenuFragment();
                bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
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
    }
}

