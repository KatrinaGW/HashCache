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

        otherUsernameHeader.setText(player.getUsername());

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(OtherCacheActivity.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(OtherCacheActivity.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(OtherCacheActivity.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // remain on MyProfile
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(OtherCacheActivity.this, Community.class));
                            return true;
                        }
                        return OtherCacheActivity.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });

//        scannableCodesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //onScannableCodeItemClicked(i);
//            }
//        });
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
                //onScannableCodeItemClicked(i);
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
                });

        Database.getInstance()
                .getPlayerWalletTotalScore(player.getPlayerWallet().getScannedCodeIds())
                .thenAccept(totalScore -> {
                    this.otherUserScoreHeader.setText(Long.toString(totalScore));
                });
    }
}
