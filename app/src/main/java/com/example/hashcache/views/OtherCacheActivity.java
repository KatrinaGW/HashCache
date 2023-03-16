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
/**

 The OtherCacheActivity displays a view of another player's cache.
 The activity displays the username of the other player, a list of their scannable codes,
 and the total score of the scannable codes.
 It also allows the user to navigate to other pages within the application via a popup menu.
 */

public class OtherCacheActivity extends AppCompatActivity {
    private TextView otherUsernameHeader;
    private TextView otherUserScoreHeader;
    private ScannableCodesArrayAdapter scannableCodesArrayAdapter;
    private ArrayList<ScannableCode> scannableCodes;
    private ImageButton menuButton;
    private ListView scannableCodesList;
    Player player;
    private boolean afterOnCreate;
    /**
     * Called when the activity is starting. Initializes the activity and its associated layout.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state, if any.
     */
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
    /**
     * Called when the activity has become visible and is in the foreground.
     * Updates the adapter values if the activity is not after the onCreate method.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if(!afterOnCreate){
            setAdapterValues();
        }else{
            afterOnCreate = false;
        }
    }
    /**

     Sets the click listener for the scannable codes list view.

     When a scannable code is clicked, the current scannable code in the AppStore is set to the clicked code,

     and the DisplayMonsterActivity is launched with the "belongsToCurrentUser" flag set to false.
     */
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
    /**

     Sets the adapter values for the scannable codes list view and the user score header.

     Retrieves the list of scannable codes from the player's wallet, sets the adapter for the scannable codes list view,

     and sets the click listener for the list view.

     Retrieves the total score for the player's wallet and updates the user score header.
     */
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
                        return null;
                    }
                });

        Database.getInstance()
                .getPlayerWalletTotalScore(player.getPlayerWallet().getScannedCodeIds())
                .thenAccept(totalScore -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            otherUserScoreHeader.setText(Long.toString(totalScore));
                        }
                    });
                });
    }
}
