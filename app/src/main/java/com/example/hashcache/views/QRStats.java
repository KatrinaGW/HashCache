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
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;

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
    private ScannableCode lowestScoring;
    private ScannableCode highestScoring;
    private PlayerWallet playerWallet;

    /**
     * Initializes the activity and sets the layout. Also adds functionality to the
     * menu button.
     *
     * @param savedInstanceState a Bundle object containing the activity's
     *                           previously saved state, if any
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_stats);

        menuButton = findViewById(R.id.menu_button);
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

        // add functionality to menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                BottomMenuFragment bottomMenu = new BottomMenuFragment();
                bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
            }
        });

        init();
        AppStore.get().addObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateValues();
    }

    private void updateValues() {
        PlayerWallet curWallet = AppStore.get().getCurrentPlayer().getPlayerWallet();
        ScannableCode lowestScan = AppStore.get().getLowestScannableCode();
        ScannableCode highestScan = AppStore.get().getHighestScannableCode();
        long totalScore = AppStore.get().getCurrentPlayer().getTotalScore();
        setMyCodesValue(curWallet.getSize());
        setLowScoreValue(lowestScan.getHashInfo().getGeneratedScore());
        setHighScoreValue(highestScan.getHashInfo().getGeneratedScore());
        setTotalScoreValue(totalScore);
    }

    private void highestScoringCodeClicked() {
        ScannableCode highestScan = AppStore.get().getHighestScannableCode();
        AppStore.get().setCurrentScannableCode(highestScan);
        Intent intent = new Intent(getApplicationContext(), DisplayMonsterActivity.class);
        intent.putExtra("belongsToCurrentUser", true);
        startActivity(intent);
    }

    private void lowestScoringCodeClicked() {
        ScannableCode lowestScan = AppStore.get().getLowestScannableCode();
        AppStore.get().setCurrentScannableCode(lowestScan);
        Intent intent = new Intent(getApplicationContext(), DisplayMonsterActivity.class);
        intent.putExtra("belongsToCurrentUser", true);
        startActivity(intent);
    }

    private void init() {

        menuButton = findViewById(R.id.menu_button);
        scoreIcon = findViewById(R.id.score_icon);
        totalScoreTextView = findViewById(R.id.total_score_textview);
        myCodesTextView = findViewById(R.id.my_codes_textview);
        topScoreTextView = findViewById(R.id.top_score_textview);
        lowScoreTextView = findViewById(R.id.low_score_textview);
    }

    public void setHighScoreValue(long score) {
        topScoreTextView.setText(String.valueOf(score));
    }

    public void setLowScoreValue(long score) {
        lowScoreTextView.setText(String.valueOf(score));
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


    @Override
    public void update(Observable observable, Object o) {
        updateValues();
    }
}
