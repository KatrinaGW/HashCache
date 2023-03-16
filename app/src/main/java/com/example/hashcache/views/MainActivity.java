/*
 * MainActivity
 *
 * Allows users to create an account and start the game.
 */

package com.example.hashcache.views;

import static com.example.hashcache.controllers.DependencyInjector.getOrMakeScannableCodesConnectionHandler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hashcache.R;
import com.example.hashcache.controllers.AddUserCommand;
import com.example.hashcache.controllers.hashInfo.NameGenerator;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.context.Context;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.ScannableCodesDatabaseAdapter;
import com.example.hashcache.models.data_exchange.database.DatabaseMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.function.Function;
import java.io.InputStream;
/**

 MainActivity

 The MainActivity class is the main entry point to the game. It allows users to create an account and start the game.

 It initializes Firebase and the PlayerList instance, and sets up the functionality for the start button.

 @see AppCompatActivity

 @see FirebaseOptions.Builder

 @see AddUserCommand

 @see PlayerList
 */
public class MainActivity extends AppCompatActivity {

    PlayerList playerList;
    EditText usernameEditText;
    AddUserCommand addUserCommand;
    /**

     onCreate method is called when the activity is created.

     It initializes Firebase, the AddUserCommand instance, and the PlayerList instance.

     It sets up the functionality for the start button, which will take the user to the AppHome activity.

     It also sets up the functionality for the name generator, which generates random names.

     @param savedInstanceState saved state of the activity, if it was previously closed.

     @see FirebaseApp#initializeApp

     @see FirebaseOptions.Builder

     @see AddUserCommand

     @see PlayerList

     @see AppCompatButton

     @see InputStream

     @see NameGenerator#getNames

     @see AppHome

     @see Function
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        boolean hasBeenInitialized=false;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(getApplicationContext());
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)){
                hasBeenInitialized=true;
            }
        }

        if(!hasBeenInitialized) FirebaseApp.initializeApp(this, new FirebaseOptions.Builder()
                .setApplicationId("1:901109849854:android:59c5ab124b7d20ef1d4faf")
                .setApiKey("AIzaSyBbOhuWDn2sYOsEkslCjercBYitb2MLMho")
                .setDatabaseUrl("https://hashcache2.firebaseio.com/")
                .setGcmSenderId("901109849854")
                .setStorageBucket("hashcache2.appspot.com")
                .setProjectId("hashcache2")
                .build());

        Context.get();

        getOrMakeScannableCodesConnectionHandler();
        // Initializes the AddUserCommand and PlayerList instances
        addUserCommand = new AddUserCommand();

        playerList = PlayerList.getInstance();

        // add functionality to start button
        AppCompatButton startButton = findViewById(R.id.start_button);
        usernameEditText = findViewById(R.id.username_edittext);

        // Gets input stream to names.csv which is used for name generation
        InputStream is;
        is = getResources().openRawResource(R.raw.names);
        NameGenerator.getNames(is);        // Sets the listener for the start button
        setStartBtnListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                addUserCommand.loginUser(getUsername()).thenAccept(res -> {
                    Intent goHome = new Intent(MainActivity.this, AppHome.class);

                    PlayersDatabaseAdapter.getInstance().getPlayers().thenAccept(players -> {
                        DatabaseMapper databaseMapper = new DatabaseMapper();

                        databaseMapper.updatePlayerPreferences("fa13e327-e172-4763-aa3d-a1e49681e0a2",
                                new PlayerPreferences())
                                        .thenAccept(thing -> {
                                            startActivity(goHome);
                                        });

                    });

                }).exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        // display some error on the screen
                        return null;
                    }
                });
            }
        });
    }
    /**
     * Sets the listener for the start button.
     *
     * @param listeners the listener to be set for the start button
     * @see View.OnClickListener
     */
    public void setStartBtnListener(View.OnClickListener listeners) {
        findViewById(R.id.start_button).setOnClickListener(listeners);
    }
    /**
     * Gets the username entered by the user.
     *
     * @return the username entered by the user
     */
    public String getUsername(){
        TextView tx = this.findViewById(R.id.username_edittext);
        return tx.getText().toString();
    }
    /**
     * Sets the username entered by the user.
     *
     * @param username the username to be set
     */
    public void setUsername(String username){
        TextView tx = this.findViewById(R.id.username_edittext);
        tx.setText(username);
    }
}