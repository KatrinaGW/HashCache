package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.Database;

public class OtherProfileInformationActivity extends AppCompatActivity {
    TextView otherUsernameView;
    TextView otherEmailView;
    TextView otherPhoneNumberView;
    Player otherPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_profile_information);

        Intent intent = getIntent();
        String otherUsername = intent.getStringExtra("otherUsername");

        otherUsernameView = findViewById(R.id.other_username_textview);
        otherEmailView = findViewById(R.id.other_email_textview);
        otherPhoneNumberView = findViewById(R.id.other_phone_textview);

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

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create main menu
                PopupMenu menu = new PopupMenu(OtherProfileInformationActivity.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(OtherProfileInformationActivity.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(OtherProfileInformationActivity.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(OtherProfileInformationActivity.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // remain on Community page
                            return true;
                        }
                        return OtherProfileInformationActivity.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });
    }

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
