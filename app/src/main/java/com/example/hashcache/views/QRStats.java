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
    private TextView topScoreValueTextView;
    private TextView lowScoreValueTextView;

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
        totalScoreTextView = findViewById(R.id.total_score_value);
        myCodesTextView = findViewById(R.id.total_score_value);
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


    @Override
    public void update(Observable observable, Object o) {
        updateValues();
    }
}
