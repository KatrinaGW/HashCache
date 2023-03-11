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
import android.widget.TextView;

import com.example.hashcache.controllers.AddUserCommand;
import com.example.hashcache.controllers.DependencyInjector;
import com.example.hashcache.controllers.HashInfoGenerator;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database_connections.ScannableCodesConnectionHandler;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.views.MainActivityView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.function.Function;
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

        addUserCommand.loginUser(getUsername()).thenAccept(res -> {
            Intent goHome = new Intent(MainActivity.this, AppHome.class);
            startActivity(goHome);
        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                // display some error on the screen
                return null;
            }
        });
    }
    public void setStartBtnListener(View.OnClickListener listeners) {
        findViewById(R.id.start_button).setOnClickListener(listeners);
    }
    public String getUsername(){
        TextView tx = this.findViewById(R.id.username_edittext);
        return tx.getText().toString();
    }

    public void setUsername(String username){
        TextView tx = this.findViewById(R.id.username_edittext);
        tx.setText(username);
    }
}