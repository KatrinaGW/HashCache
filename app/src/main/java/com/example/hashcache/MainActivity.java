/*
 * MainActivity
 *
 * Allows users to create an account and start the game.
 */

package com.example.hashcache;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.hashcache.controllers.AddUserCommand;
import com.example.hashcache.database_connections.BooleanCallback;
import com.example.hashcache.database_connections.CodeLocationConnectionHandler;
import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.PlayerList;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

public class MainActivity extends AppCompatActivity {

    PlayerList playerList;
    EditText usernameEditText;
    AddUserCommand addUserCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this, new FirebaseOptions.Builder()
                .setApplicationId("1:343583306337:android:d5e9ae9095f25cb5b4f020")
                .setApiKey("AIzaSyD25aeFyGGaQ9nSNs5QFSJXLfQm6gFb9kM")
                .setDatabaseUrl("https://hashcache-78ec8.firebaseio.com/")
                .setGcmSenderId("343583306337")
                .setStorageBucket("hashcache-78ec8.appspot.com")
                .setProjectId("hashcache-78ec8")
                .build());

        playerList = new PlayerList();
        addUserCommand = new AddUserCommand();

        // add functionality to start button
        AppCompatButton startButton = findViewById(R.id.start_button);
        usernameEditText = findViewById(R.id.username_edittext);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartCachingClicked();
            }
        });
    }

    private void onStartCachingClicked(){
        addUserCommand.addUser(usernameEditText.getText().toString(), playerList);
        Intent goHome = new Intent(MainActivity.this, AppHome.class);
        startActivity(goHome);

    }
}