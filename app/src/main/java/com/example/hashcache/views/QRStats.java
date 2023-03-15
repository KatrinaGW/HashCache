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
/**
 The QRStats activity displays statistics for the current user, including total score, number of codes scanned,
 highest score achieved, and lowest score achieved. It also provides buttons for navigating to other pages.
 */
public class QRStats extends AppCompatActivity {
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
     * Initializes the activity and sets the layout. Also adds functionality to the menu button and my profile button.
     *
     * @param savedInstanceState a Bundle object containing the activity's previously saved state, if any
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        playerWallet = AppStore.get().getCurrentPlayer().getPlayerWallet();

        setMyCodesValueTextView(playerWallet.getSize());
        setTotalScoreValueTextView();
        setLowScoreValueTextView();
        setTopScoreValueTextView();

        if(playerWallet.getSize()>0){
            Database.getInstance()
                    .getPlayerWalletLowScore(playerWallet.getScannedCodeIds())
                    .thenAccept(lowestScoring -> {
                        this.lowestScoring = lowestScoring;
                        setLowScoreValueTextView();
                    });

            Database.getInstance()
                    .getPlayerWalletTopScore(playerWallet.getScannedCodeIds())
                    .thenAccept(lowestScoring -> {
                        this.highestScoring = lowestScoring;
                        setTopScoreValueTextView();
                    });
        }
    }

    private void highestScoringCodeClicked(){
        AppStore.get().setCurrentScannableCode(highestScoring);
        startActivity(new Intent(QRStats.this, DisplayMonsterActivity.class));
    }

    private void lowestScoringCodeClicked(){
        AppStore.get().setCurrentScannableCode(lowestScoring);
        startActivity(new Intent(QRStats.this, DisplayMonsterActivity.class));
    }

    private void init() {

        menuButton = findViewById(R.id.menu_button);
        scoreIcon = findViewById(R.id.score_icon);
        totalScoreTextView = findViewById(R.id.total_score_textview);
        myCodesTextView = findViewById(R.id.my_codes_textview);
        topScoreTextView = findViewById(R.id.top_score_textview);
        lowScoreTextView = findViewById(R.id.low_score_textview);
    }
    public ImageButton getMenuButton() {
        return menuButton;
    }

    public ImageButton getScoreIcon() {
        return scoreIcon;
    }

    public TextView getTotalScoreTextView() {
        return totalScoreTextView;
    }

    public TextView getMyCodesTextView() {
        return myCodesTextView;
    }

    public TextView getTopScoreTextView() {
        return topScoreTextView;
    }

    public TextView getLowScoreTextView() {
        return lowScoreTextView;
    }

    private void setTotalScoreValueTextView(){
        Database.getInstance()
                .getPlayerWalletTotalScore(playerWallet.getScannedCodeIds())
                .thenAccept(totalScore -> {
                    if(totalScore>0){
                        this.totalScoreValueTextView.setText(Long.toString(totalScore));
                    }else{
                        this.totalScoreValueTextView.setText(R.string.no_codes_scanned);
                    }

                });
    }

    private void setMyCodesValueTextView(int numCodes){
        if(numCodes>0){
            this.myCodesValueTextView.setText(Integer.toString(numCodes));
        }else{
            this.myCodesValueTextView.setText(R.string.no_codes_scanned);
        }
    }

    private void setTopScoreValueTextView(){
        if(highestScoring!=null){
            this.topScoreValueTextView.setText(Long.toString(highestScoring.getHashInfo().getGeneratedScore()));
        }else{
            this.topScoreValueTextView.setText(R.string.no_codes_scanned);
        }
    }

    private void setLowScoreValueTextView(){
        if(lowestScoring != null){
            this.lowScoreValueTextView.setText(Long.toString(lowestScoring.getHashInfo().getGeneratedScore()));
        }else{
            this.lowScoreValueTextView.setText(R.string.no_codes_scanned);
        }
    }
}
