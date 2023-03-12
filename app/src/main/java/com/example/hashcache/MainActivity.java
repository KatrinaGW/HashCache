/*
 * MainActivity
 *
 * Allows users to create an account and start the game.
 */

package com.example.hashcache;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.hashcache.controllers.AddUserCommand;
import com.example.hashcache.controllers.DependencyInjector;
import com.example.hashcache.controllers.HashInfoGenerator;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database_connections.ScannableCodesConnectionHandler;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.PlayerList;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.InputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    PlayerList playerList;
    EditText usernameEditText;
    AddUserCommand addUserCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this, new FirebaseOptions.Builder()
                .setApplicationId("1:901109849854:android:59c5ab124b7d20ef1d4faf")
                .setApiKey("AIzaSyBbOhuWDn2sYOsEkslCjercBYitb2MLMho")
                .setDatabaseUrl("https://hashcache2.firebaseio.com/")
                .setGcmSenderId("901109849854")
                .setStorageBucket("hashcache2.appspot.com")
                .setProjectId("hashcache2")
                .build());

        addUserCommand = new AddUserCommand();
        playerList = PlayerList.getInstance();

        // add functionality to start button
        AppCompatButton startButton = findViewById(R.id.start_button);
        usernameEditText = findViewById(R.id.username_edittext);

        // Gets input stream to names.csv which is used for name generation
        InputStream is;
        is = getResources().openRawResource(R.raw.names);
        HashInfoGenerator.NameGenerator.getNames(is);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartCachingClicked();
            }
        });

    }

    private void onStartCachingClicked(){
        addUserCommand.addUser(usernameEditText.getText().toString(), new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                if(!isTrue){
                    throw new IllegalArgumentException("Something went wrong while adding player");
                }
                Intent goHome = new Intent(MainActivity.this, AppHome.class);

                startActivity(goHome);
            }
        });
    }



}