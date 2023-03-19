/*
 * QRStats
 *
 * Shows statistics for current user.
 * Additional buttons permit navigation to other pages.
 */

package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.context.Context;

import java.util.Observable;
import java.util.Observer;

/**
 * The QRStats activity displays statistics for the current user, including
 * total score, number of codes scanned,
 * highest score achieved, and lowest score achieved. It also provides buttons
 * for navigating to other pages.
 */
public class QRStats extends AppCompatActivity implements Observer {
    private ImageButton menuButton;
    private ImageButton scoreIcon;
    private TextView totalScoreTextView;
    private TextView myCodesTextView;
    private TextView topScoreTextView;
    private TextView lowScoreTextView;
    private TextView totalScoreValueTextView;
    private TextView myCodesValueTextView;
    private TextView topScoreValueTextView;
    private TextView lowScoreValueTextView;
    private AppCompatButton myProfileButton;

    /**
     * Initializes the activity and sets the layout. Also adds functionality to the
     * menu button and my profile button.
     *
     * @param savedInstanceState a Bundle object containing the activity's
     *                           previously saved state, if any
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_stats);

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        totalScoreValueTextView = findViewById(R.id.total_score_value);
        myCodesValueTextView = findViewById(R.id.my_codes_value);
        topScoreValueTextView = findViewById(R.id.top_score_value);
        lowScoreValueTextView = findViewById(R.id.low_score_value);

        topScoreValueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highestScoringCodeClicked();
            }
        });

        lowScoreValueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowestScoringCodeClicked();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create main menu
                PopupMenu menu = new PopupMenu(QRStats.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) { // go to AppHome page
                            startActivity(new Intent(QRStats.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) { // remain on QRStats page
                            return true;

                        } else if (id == R.id.menu_profile) { // go to MyProfile
                            startActivity(new Intent(QRStats.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) { // go to Community
                            startActivity(new Intent(QRStats.this, Community.class));
                            return true;
                        }
                        return QRStats.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });

        // add functionality to profile button
        AppCompatButton loginButton = findViewById(R.id.my_profile_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 
             * This method handles the click event of the My Profile button in the QRStats
             * activity and starts the MyProfile activity.
             * 
             * @param v The view that was clicked
             */
            @Override
            public void onClick(View v) {

                // go to MyProfile page
                Intent goHome = new Intent(QRStats.this, MyProfile.class);
                startActivity(goHome);
            }
        });
        init();
        Context.get().addObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateValues();
    }

    private void updateValues() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerWallet curWallet = Context.get().getCurrentPlayer().getPlayerWallet();
                ScannableCode lowestScan = Context.get().getLowestScannableCode();
                ScannableCode highestScan = Context.get().getHighestScannableCode();
                long totalScore = Context.get().getCurrentPlayer().getTotalScore();
                setMyCodesValue(curWallet.getSize());
                setLowScoreValue(lowestScan);
                setHighScoreValue(highestScan);
                setTotalScoreValue(totalScore);
            }
        });
    }

    private void highestScoringCodeClicked() {
        ScannableCode highestScan = Context.get().getHighestScannableCode();
        Context.get().setCurrentScannableCode(highestScan);
        Intent intent = new Intent(getApplicationContext(), DisplayMonsterActivity.class);
        intent.putExtra("belongsToCurrentUser", true);
        startActivity(intent);
    }

    private void lowestScoringCodeClicked() {
        ScannableCode lowestScan = Context.get().getLowestScannableCode();
        Context.get().setCurrentScannableCode(lowestScan);
        Intent intent = new Intent(getApplicationContext(), DisplayMonsterActivity.class);
        intent.putExtra("belongsToCurrentUser", true);
        startActivity(intent);
    }

    private void init() {
        menuButton = findViewById(R.id.menu_button);
        scoreIcon = findViewById(R.id.score_icon);
        totalScoreTextView = findViewById(R.id.total_score_value);
        myCodesTextView = findViewById(R.id.my_codes_value);
        myProfileButton = findViewById(R.id.my_profile_button);
    }

    public void setHighScoreValue(ScannableCode highestScoring) {
        if(highestScoring!=null){
            topScoreValueTextView.setText(String.valueOf(highestScoring.getHashInfo().getGeneratedScore()));
            topScoreValueTextView.setClickable(true);
        }else{
            topScoreValueTextView.setText(R.string.no_codes_scanned);
            topScoreValueTextView.setClickable(false);
        }
    }

    public void setLowScoreValue(ScannableCode lowestScoring) {
        if(lowestScoring!=null){
            lowScoreValueTextView.setText(String.valueOf(lowestScoring.getHashInfo().getGeneratedScore()));
            lowScoreValueTextView.setClickable(true);
        }else{
            lowScoreValueTextView.setText(R.string.no_codes_scanned);
            lowScoreValueTextView.setClickable(false);
        }

    }

    public void setTotalScoreValue(long score) {
        totalScoreTextView.setText(String.valueOf(score));
    }

    public void setMyCodesValue(int value) {
        myCodesTextView.setText(String.valueOf(value));
    }

    public ImageButton getMenuButton() {
        return menuButton;
    }

    public ImageButton getScoreIcon() {
        return scoreIcon;
    }

    public AppCompatButton getMyProfileButton() {
        return myProfileButton;
    }

    @Override
    public void update(Observable observable, Object o) {
        updateValues();
    }
}
