/*
 * Settings
 *
 * User's settings page.
 * Displays username and contact information.
 * Allows user to toggle on/off location settings.
 * Allows user to edit their username and contact information.
 * Additional buttons permit navigation to other pages.
 */

package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.controllers.UpdateUserPreferencesCommand;
import com.example.hashcache.models.Player;
import com.example.hashcache.store.AppStore;

public class Settings extends AppCompatActivity {
    private TextView usernameView;
    private TextView phoneNumberView;
    private TextView emailView;
    private CheckBox geoLocationPreferenceCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Player playerInfo = AppStore.get().getCurrentPlayer();

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        usernameView = findViewById(R.id.username_textview);
        phoneNumberView = findViewById(R.id.phone_textview);
        emailView = findViewById(R.id.email_textview);
        geoLocationPreferenceCheckbox = findViewById(R.id.geolocation_checkbox);

        setUsername(playerInfo.getUsername());
        setEmail(playerInfo.getContactInfo().getEmail());
        setPhoneNumber(playerInfo.getContactInfo().getPhoneNumber());

        geoLocationPreferenceCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(Settings.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // go to AppHome page
                            startActivity(new Intent(Settings.this, AppHome.class));
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(Settings.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(Settings.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(Settings.this, Community.class));
                            return true;
                        }
                        return Settings.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });
    }

    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        UpdateUserPreferencesCommand.toggleGeoLocationPreference(checked);
    }

    private void setUsername(String username){
        this.usernameView.setText(username);
    }

    private void setPhoneNumber(String phoneNumber){
        if(!phoneNumber.equals("")){
            this.phoneNumberView.setText(phoneNumber);
        }else{
            this.phoneNumberView.setVisibility(View.GONE);
        }
    }

    private void setEmail(String email){
        if(!email.equals("")){
            this.emailView.setText(email);
        }else{
            this.emailView.setVisibility(View.GONE);
        }
    }
}
