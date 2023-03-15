package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;

import java.util.ArrayList;
import java.util.function.Function;

public class OtherCacheActivity extends AppCompatActivity {
    private TextView otherUsernameHeader;
    private TextView otherUserScoreHeader;
    private ScannableCodesArrayAdapter scannableCodesArrayAdapter;
    private ArrayList<ScannableCode> scannableCodes;
    private ImageButton menuButton;
    private ListView scannableCodesList;
    Player player;
    private boolean afterOnCreate;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_cache);

        afterOnCreate = true;

        otherUsernameHeader = findViewById(R.id.other_username_header);
        otherUserScoreHeader = findViewById(R.id.other_user_score);
        player = AppStore.get().getSelectedPlayer();
        scannableCodesList = findViewById(R.id.scannable_codes_list);

        otherUsernameHeader.setText(player.getUsername());

        setAdapterValues();

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
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

        if(!afterOnCreate){
            setAdapterValues();
        }else{
            afterOnCreate = false;
        }
    }

    private void setListViewButtonListener(){
        scannableCodesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ScannableCode scannableCode = scannableCodes.get(i);
                AppStore.get().setCurrentScannableCode(scannableCode);

                Intent intent = new Intent(getApplicationContext(), DisplayMonsterActivity.class);
                intent.putExtra("belongsToCurrentUser", false);

                startActivity(intent);
            }
        });
    }

    private void setAdapterValues(){

        Database.getInstance()
                .getScannableCodesByIdInList(player.getPlayerWallet().getScannedCodeIds())
                .thenAccept(scannableCodes -> {
                    this.scannableCodes = scannableCodes;
                    scannableCodesArrayAdapter = new ScannableCodesArrayAdapter(this,
                            scannableCodes);
                    scannableCodesList.setAdapter(scannableCodesArrayAdapter);
                    setListViewButtonListener();
                }).exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        System.out.println(throwable);
                        return null;
                    }
                });

        Database.getInstance()
                .getPlayerWalletTotalScore(player.getPlayerWallet().getScannedCodeIds())
                .thenAccept(totalScore -> {
                    this.otherUserScoreHeader.setText(Long.toString(totalScore));
                });
    }
}
