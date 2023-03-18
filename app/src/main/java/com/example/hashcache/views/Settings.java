/*
 * Settings
 *
 * User's settings page.
 * Displays username and contact information.
 * Allows user to toggle on/off location settings.
 * Allows user to edit their username and contact information.
 * Additional buttons permit navigation to other pages.
 */
/**

 The Settings activity displays the user's settings page, including their username and contact information,
 and allows them to toggle their location settings on and off, as well as edit their username and contact information.
 Additional buttons permit navigation to other pages within the application.
 */
package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.controllers.UpdateUserPreferencesCommand;
import com.example.hashcache.models.Player;
import com.example.hashcache.context.Context;
import com.example.hashcache.models.database.Database;

import java.util.Observable;
import java.util.Observer;

/**

 The Settings activity displays the user's settings page, including their username and contact information,
 and allows them to toggle their location settings on and off, as well as edit their username and contact information.
 Additional buttons permit navigation to other pages within the application.
 */
public class Settings extends AppCompatActivity implements Observer {
    private TextView usernameView;
    private TextView phoneNumberView;
    private TextView emailView;
    private CheckBox geoLocationPreferenceCheckbox;
    private ImageView editInfoButton;
    /**
     * Called when the activity is starting. Initializes the activity and its associated layout.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        ImageButton menuButton = findViewById(R.id.menu_button);
        usernameView = findViewById(R.id.username_textview);
        phoneNumberView = findViewById(R.id.phone_textview);
        emailView = findViewById(R.id.email_textview);
        geoLocationPreferenceCheckbox = findViewById(R.id.geolocation_checkbox);
        editInfoButton = findViewById(R.id.edit_info_image);

        geoLocationPreferenceCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });

        editInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, EditPlayerInfoActivity.class));
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
        Context.get().addObserver(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setValues();
    }

    private void setValues(){
        Player playerInfo = Context.get().getCurrentPlayer();
        setUsername(playerInfo.getUsername());
        setEmail(playerInfo.getContactInfo().getEmail());
        setPhoneNumber(playerInfo.getContactInfo().getPhoneNumber());
        setRecordGeoLocationChecked(playerInfo.getPlayerPreferences().getRecordGeolocationPreference());
    }

    /**
     * Called when the geolocation preference checkbox is clicked.
     *
     * @param view The checkbox view that was clicked.
     */
    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        UpdateUserPreferencesCommand.toggleGeoLocationPreference(checked, Context.get(),
                Database.getInstance());
    }
    /**
     * Sets the username of the current user in the username view.
     *
     * @param username The username of the current user.
     */
    private void setUsername(String username){
        this.usernameView.setText(username);
    }
    /**
     * Sets the phone number of the current user in the phone number view.
     * If the phone number is empty, the view is hidden.
     *
     * @param phoneNumber The phone number of the current user.
     */
    private void setPhoneNumber(String phoneNumber){
        if(!phoneNumber.equals("")){
            this.phoneNumberView.setText(phoneNumber);
        }else{
            this.phoneNumberView.setVisibility(View.GONE);
        }
    }
    /**
     * Sets the email address of the current user in the email address view.
     * If the email address is empty, the view is hidden.
     *
     * @param email The email address of the current user.
     */
    private void setEmail(String email){
        if(!email.equals("")){
            this.emailView.setText(email);
        }else{
            this.emailView.setVisibility(View.GONE);
        }
    }

    private void setRecordGeoLocationChecked(boolean checked){
        this.geoLocationPreferenceCheckbox.setChecked(checked);
    }

    @Override
    public void update(Observable observable, Object o) {
        setValues();
    }
}
