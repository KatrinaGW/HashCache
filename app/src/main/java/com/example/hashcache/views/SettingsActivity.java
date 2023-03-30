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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.R;
import com.example.hashcache.controllers.LogoutCommand;
import com.example.hashcache.controllers.ResetCommand;
import com.example.hashcache.controllers.UpdateContactInfoCommand;
import com.example.hashcache.controllers.UpdateUserPreferencesCommand;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.database.Database;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Function;

/**

 The Settings activity displays the user's settings page, including their username and contact information,
 and allows them to toggle their location settings on and off, as well as edit their username and contact information.
 Additional buttons permit navigation to other pages within the application.
 */
public class SettingsActivity extends AppCompatActivity implements Observer,
EditPlayerInfoFragment.EditPlayerInfoFragmentDismisser{
    private TextView usernameView;
    private TextView phoneNumberView;
    private TextView emailView;
    private ImageView editInfoButton;
    private ImageButton menuButton;
    private Button logoutButton;

    /**
     * Dismiss the edit info fragment and make the buttons visible again
     */
    @Override
    public void dismissFragment(ContactInfo contactInfo){
        if(contactInfo!=null){
            UpdateContactInfoCommand.updateContactInfoCommand(AppContext.get().getCurrentPlayer().getUserId(),
                            contactInfo, Database.getInstance(), AppContext.get())
                    .thenAccept(isComplete->{
                        if(isComplete){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setValues();
                                }
                            });
                        }
                    })
                    .exceptionally(new Function<Throwable, Void>() {
                        @Override
                        public Void apply(Throwable throwable) {
                            return null;
                        }
                    });
        }else{
            setValues();
        }
        getSupportFragmentManager().beginTransaction().
                remove(getSupportFragmentManager().findFragmentById(R.id.edit_info_fragment_container)).commit();
        logoutButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
        editInfoButton.setVisibility(View.VISIBLE);
    }

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
        editInfoButton = findViewById(R.id.edit_info_image);
        logoutButton = findViewById(R.id.logout_button);

        editInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditInfoClicked();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutClicked();
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
        AppContext.get().addObserver(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setValues();
    }

    private void onEditInfoClicked(){
        logoutButton.setVisibility(View.GONE);
        usernameView.setVisibility(View.GONE);
        emailView.setVisibility(View.GONE);
        phoneNumberView.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);
        editInfoButton.setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_info_fragment_container, EditPlayerInfoFragment.class, null)
                .commit();
    }

    private void onLogoutClicked(){
        LogoutCommand.logout(Database.getInstance())
                .thenAccept(nullValue -> {
                    ResetCommand.reset();
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                });
    }

    private void setValues(){
        Player playerInfo = AppContext.get().getCurrentPlayer();
        usernameView.setVisibility(View.VISIBLE);
        setUsername(playerInfo.getUsername());

        setEmail(playerInfo.getContactInfo().getEmail());
        setPhoneNumber(playerInfo.getContactInfo().getPhoneNumber());
    }

    /**
     * Called when the geolocation preference checkbox is clicked.
     *
     * @param view The checkbox view that was clicked.
     */
    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        UpdateUserPreferencesCommand.toggleGeoLocationPreference(checked, AppContext.get(),
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
            this.phoneNumberView.setVisibility(View.VISIBLE);
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
            this.emailView.setVisibility(View.VISIBLE);
        }else{
            this.emailView.setVisibility(View.GONE);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        setValues();
    }
}
