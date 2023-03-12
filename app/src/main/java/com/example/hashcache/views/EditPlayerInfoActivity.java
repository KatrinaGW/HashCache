package com.example.hashcache.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.BuildConfig;
import com.example.hashcache.R;
import com.example.hashcache.controllers.UpdateContactInfoCommand;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;

public class EditPlayerInfoActivity extends AppCompatActivity {
    private EditText editEmail;
    private EditText editPhoneNumber;
    private Button confirmButton;
    private ContactInfo currentContactInfo;

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

    private void initValues(){
        currentContactInfo = AppStore.get().getCurrentPlayer().getContactInfo();
        System.out.println("INITIalizing values");
        System.out.println(currentContactInfo.getEmail());
        editEmail.setText(currentContactInfo.getEmail());
        editPhoneNumber.setText(currentContactInfo.getPhoneNumber());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initValues();
    }

    private void onConfirmClicked(){
        String emailText = editEmail.getText().toString();
        String phoneNumberText = editPhoneNumber.getText().toString();
        ContactInfo newContactInfo = new ContactInfo();

        //TODO: FE input validation
        newContactInfo.setPhoneNumber(phoneNumberText);
        newContactInfo.setEmail(emailText);

        UpdateContactInfoCommand.updateContactInfoCommand(AppStore.get().getCurrentPlayer().getUserId(),
                        newContactInfo)
                .thenAccept(isComplete->{
                    if(isComplete){
                        startActivity(new Intent(EditPlayerInfoActivity.this, Settings.class));
                    }
                });
    }
}
