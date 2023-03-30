/*
 * MainActivity
 *
 * Allows users to create an account and start the game.
 */

package com.example.hashcache.views;

import static com.example.hashcache.controllers.DependencyInjector.getOrMakeScannableCodesConnectionHandler;
import static com.example.hashcache.controllers.DependencyInjector.makeLoginsAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hashcache.R;
import com.example.hashcache.controllers.LoginUserCommand;
import com.example.hashcache.controllers.SetupUserCommand;
import com.example.hashcache.controllers.checkLoginCommand;
import com.example.hashcache.controllers.hashInfo.NameGenerator;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.context.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.List;
import java.util.function.Function;
import java.io.InputStream;
/**

 MainActivity

 The MainActivity class is the main entry point to the game. It allows users to create an account and start the game.

 It initializes Firebase and the PlayerList instance, and sets up the functionality for the start button.

 @see AppCompatActivity

 @see FirebaseOptions.Builder

 @see LoginUserCommand

 @see PlayerList
 */
public class MainActivity extends AppCompatActivity {

    PlayerList playerList;
    EditText usernameEditText;
    LoginUserCommand loginUserCommand;
    /**

     onCreate method is called when the activity is created.

     It initializes Firebase, the AddUserCommand instance, and the PlayerList instance.

     It sets up the functionality for the start button, which will take the user to the AppHome activity.

     It also sets up the functionality for the name generator, which generates random names.

     @param savedInstanceState saved state of the activity, if it was previously closed.

     @see FirebaseApp#initializeApp

     @see FirebaseOptions.Builder

     @see LoginUserCommand

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
        makeLoginsAdapter();

        Context.get().setDeviceId(Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID));
        loginUserCommand = new LoginUserCommand();
        playerList = PlayerList.getInstance();
        checkDeviceId();
    }

    private void checkDeviceId(){
        checkLoginCommand.checkLogin(loginUserCommand)
                .thenAccept(existed -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!existed){
                                newUserLoginProcess();
                            }else{
                                InputStream is;
                                is = getResources().openRawResource(R.raw.names);
                                NameGenerator.getNames(is);
                                startActivity(new Intent(MainActivity.this, AppHome.class));
                            }
                        }
                    });
                })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        return null;
                    }
                });
    }

    private void newUserLoginProcess(){
        // add functionality to start button
        AppCompatButton startButton = findViewById(R.id.start_button);
        usernameEditText = findViewById(R.id.username_edittext);
        usernameEditText.setVisibility(View.VISIBLE);

        // Gets input stream to names.csv which is used for name generation
        InputStream is;
        is = getResources().openRawResource(R.raw.names);
        NameGenerator.getNames(is);        // Sets the listener for the start button
        setStartBtnListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loginUserCommand.loginUser(getUsername(), Database.getInstance(), Context.get(),
                                 new SetupUserCommand())
                        .thenAccept(res -> {
                            Intent goHome = new Intent(MainActivity.this, AppHome.class);

                            Database.getInstance().getPlayers().thenAccept(players -> {

                                startActivity(goHome);
                            });

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
        Button startCachingButton = findViewById(R.id.start_button);
        startCachingButton.setVisibility(View.VISIBLE);
        startCachingButton.setOnClickListener(listeners);
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