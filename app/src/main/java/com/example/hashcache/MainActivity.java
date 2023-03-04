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

import com.example.hashcache.models.PlayerList;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    PlayerList playerList;

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
        playerList.addPlayer("Tester");
        System.out.println("Completed add");

        // add functionality to start button
        AppCompatButton startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to AppHome page (map view)
                Intent goHome = new Intent(MainActivity.this, AppHome.class);
                startActivity(goHome);
            }
        });
    }
}