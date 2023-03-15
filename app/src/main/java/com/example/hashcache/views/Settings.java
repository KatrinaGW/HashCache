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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.controllers.UpdateUserPreferencesCommand;
import com.example.hashcache.models.Player;
import com.example.hashcache.store.AppStore;
/**

 The Settings activity displays the user's settings page, including their username and contact information,
 and allows them to toggle their location settings on and off, as well as edit their username and contact information.
 Additional buttons permit navigation to other pages within the application.
 */
public class Settings extends AppCompatActivity {
    private TextView usernameView;
    private TextView phoneNumberView;
    private TextView emailView;
    private CheckBox geoLocationPreferenceCheckbox;
    private ImageView editInfoButton;
    private ImageButton menuButton;
    /**
     * Called when the activity is starting. Initializes the activity and its associated layout.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        menuButton = findViewById(R.id.menu_button);
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

        setValues();
    }

    private void setValues(){
        Player playerInfo = AppStore.get().getCurrentPlayer();

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

        UpdateUserPreferencesCommand.toggleGeoLocationPreference(checked);
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
}
