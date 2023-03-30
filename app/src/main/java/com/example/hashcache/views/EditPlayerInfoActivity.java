/**

 EditPlayerInfoActivity
 Activity that allows the user to edit their contact information.
 This includes their email address and phone number.
 Upon clicking the confirmation button, the new information is saved to the database.
 */
package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.UpdateContactInfoCommand;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.database.Database;

public class EditPlayerInfoActivity extends AppCompatActivity {
    private EditText editEmail;
    private EditText editPhoneNumber;
    private Button confirmButton;
    private ContactInfo currentContactInfo;
    /**
     * Initializes the EditPlayerInfoActivity.
     * Sets the EditText objects and confirmation button and sets a click listener to the button.
     * Initializes the values of the email and phone number EditText objects.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player_info);

        editEmail = findViewById(R.id.email_edit_text);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
        confirmButton = findViewById(R.id.confirm_player_info_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClicked();
            }
        });

        initValues();

    }
    /**
     * Initializes the values of the email and phone number EditText objects.
     * Gets the current contact information of the user and sets the text of the EditText objects
     * with the current values of the user's email and phone number.
     */
    private void initValues(){
        currentContactInfo = AppContext.get().getCurrentPlayer().getContactInfo();
        System.out.println(currentContactInfo.getEmail());
        editEmail.setText(currentContactInfo.getEmail());
        editPhoneNumber.setText(currentContactInfo.getPhoneNumber());
    }
    /**
     * Initializes the values of the email and phone number EditText objects when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        initValues();
    }
    /**
     * Saves the new contact information of the user to the database.
     * Creates a new ContactInfo object with the values in the EditText objects.
     * Calls the UpdateContactInfoCommand to update the contact information in the database.
     * Upon successful completion, starts the Settings activity.
     */
    private void onConfirmClicked(){
        String emailText = editEmail.getText().toString();
        String phoneNumberText = editPhoneNumber.getText().toString();
        ContactInfo newContactInfo = new ContactInfo();

        //TODO: FE input validation
        newContactInfo.setPhoneNumber(phoneNumberText);
        newContactInfo.setEmail(emailText);

        UpdateContactInfoCommand.updateContactInfoCommand(AppContext.get().getCurrentPlayer().getUserId(),
                        newContactInfo, Database.getInstance(), AppContext.get())
                .thenAccept(isComplete->{
                    if(isComplete){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(EditPlayerInfoActivity.this, SettingsActivity.class));
                            }
                        });
                    }
                });
    }
}
