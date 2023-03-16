package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;
/**

 OtherProfileInformationActivity displays the public profile information of another player.
 It includes their username, email, and phone number if available.
 It also allows the current user to view the scannable codes of the selected player.
 */
public class OtherProfileInformationActivity extends AppCompatActivity {
    private TextView otherUsernameView;
    private TextView otherEmailView;
    private TextView otherPhoneNumberView;
    private Player otherPlayer;
    private Button viewCacheButton;
    /**
     * Called when the activity is starting. Initializes the activity and its associated layout.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_profile_information);

        Intent intent = getIntent();
        String otherUsername = intent.getStringExtra("otherUsername");

        otherUsernameView = findViewById(R.id.other_username_textview);
        otherEmailView = findViewById(R.id.other_email_textview);
        otherPhoneNumberView = findViewById(R.id.other_phone_textview);
        viewCacheButton = findViewById(R.id.view_other_player_codes);

        Database.getInstance().getIdByUsername(otherUsername).thenAccept(
                userId -> {
                    Database.getInstance().getPlayer(userId).thenAccept(
                            player -> {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        otherPlayer = player;
                                        setValues();
                                    }
                                });
                            }
                    );
                }
        );

        viewCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppStore.get().setSelectedPlayer(otherPlayer);
                startActivity(new Intent(OtherProfileInformationActivity.this, OtherCacheActivity.class));
            }
        });

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
     * Sets the public profile information of the selected player in the appropriate views.
     */
    private void setValues(){
        this.otherUsernameView.setText(otherPlayer.getUsername());

        String otherEmail = otherPlayer.getContactInfo().getEmail();
        if(otherEmail.equals("")){
            this.otherEmailView.setText("Player has no email information");
        }else{
            this.otherEmailView.setText(otherEmail);
        }

        String phoneNumber = otherPlayer.getContactInfo().getPhoneNumber();
        if(phoneNumber.equals("")){
            this.otherPhoneNumberView.setText("Player has no phone number information");
        }else{
            this.otherPhoneNumberView.setText(phoneNumber);
        }
    }
}
