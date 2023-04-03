package com.example.hashcache.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.hashInfo.HashController;
import com.example.hashcache.controllers.hashInfo.ImageGenerator;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.Database;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Function;

/**

 The NewMonsterActivity class represents an activity in the app that allows users to create a new monster.
 This activity sets up the UI elements and adds functionality to the buttons, including taking a location photo
 for the new monster or skipping the photo and navigating to the MyProfile activity. The menu button is also
 functional, allowing users to navigate to different activities in the app.
 */
public class DisplayMonsterActivity extends AppCompatActivity implements Observer {
    /**
     * Called when the activity is created.
     *
     * Initializes the activity by setting up the UI elements and adding functionality to the buttons.
     *
     * @param savedInstanceState saved state of the activity, if it was previously closed.
     */
    private TextView monsterName;
    private TextView monsterScore;
    private ImageView monsterImage;
    private ImageView locationImage;
    private ImageButton menuButton;
    private AppCompatButton commentsButton;
    private AppCompatButton photoButton;
    private AppCompatButton deleteButton;
    private ScannableCode currentScannableCode;
    private boolean belongToCurrentUser;
    private boolean fromMap;
    private String userId = null;
    private TextView numPlayersValueView;

    /**
     * Called when the activity is started
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_monster);

        Intent intent = getIntent();
        belongToCurrentUser = intent.getBooleanExtra("belongsToCurrentUser", false);
        fromMap = intent.getBooleanExtra("fromMap", false);
        userId = intent.getStringExtra("userId");
        initializeViews();
        // add functionality to delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteButtonClicked();
            }
        });

        // add functionality to photos button
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoButtonClicked();
            }
        });

        // add functionality to comments button
        commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommentsButtonClicked();
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

    /**
     * Called when the activity resumes
     */
    @Override
    protected void onResume() {
        super.onResume();
        setViews();
    }

    private void setViews(){
        currentScannableCode = AppContext.get().getCurrentScannableCode();
        HashInfo currentHashInfo = currentScannableCode.getHashInfo();
        setMonsterScore(currentHashInfo.getGeneratedScore());
        setMonsterName(currentHashInfo.getGeneratedName());
        ImageGenerator.getImageFromHash(currentScannableCode.getScannableCodeId()).thenAccept(drawable -> {
            Log.d("NewMonsterActivity", "Received drawable from API");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("NewMonsterActivity", "Setting image...");
                    setMonsterImage(drawable);
                }
            });

        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                Log.d("NewMonsterActivity ERROR", throwable.getMessage());
                return null;
            }
        });

        Database.getInstance().getNumPlayersWithScannableCode(currentScannableCode.getScannableCodeId())
                .thenAccept(numPlayers -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setNumPlayersCached(numPlayers);
                        }
                    });
                })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.d("NewMonsterActivity ERROR", throwable.getMessage());
                        return null;
                    }
                });
        AppContext.get().addObserver(this);
        if(belongToCurrentUser || (fromMap && this.userId != null)){
            String userId = fromMap ? this.userId : AppContext.get().getCurrentPlayer().getUserId();
            String scannableCodeId = currentScannableCode.getScannableCodeId();
            Database.getInstance().getPlayerCodeMetadataById(userId, scannableCodeId).thenAccept(codeMetadata -> {
                String base64Image = codeMetadata.getImage();
                if(base64Image != null) {
                    byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmapImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setMiniMapImage(new BitmapDrawable(getResources(), bitmapImage));
                        }
                    });
                }
            }).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    Log.d("ERROR", throwable.getMessage());
                    return null;
                }
            });
        }
    }

    private void initializeViews() {
        monsterName = findViewById(R.id.monster_name);
        monsterScore = findViewById(R.id.monster_score);
        monsterImage = findViewById(R.id.monster_image);
        locationImage = findViewById(R.id.location_image);
        menuButton = findViewById(R.id.menu_button);
        commentsButton = findViewById(R.id.view_comments_button);
        photoButton = findViewById(R.id.view_photos_button);
        deleteButton = findViewById(R.id.delete_button);
        numPlayersValueView = findViewById(R.id.num_players_value);

        if(!belongToCurrentUser){
            deleteButton.setVisibility(View.GONE);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    // got to comments activity when comments button clicked
    private void onCommentsButtonClicked(){
        Intent intent = new Intent(getApplicationContext(), DisplayCommentsActivity.class);

        startActivity(intent);
    }

    // got to photo gallery when photo button clicked
    private void onPhotoButtonClicked(){
        Intent intent = new Intent(getApplicationContext(), PhotoGalleryActivity.class);

        startActivity(intent);
    }

    // delete monster from player wallet when delete button clicked
    private void onDeleteButtonClicked(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HashController.deleteScannableCodeFromWallet(currentScannableCode.getScannableCodeId(),
                                AppContext.get().getCurrentPlayer().getUserId())
                        .thenAccept(completed -> {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Database.getInstance().removeScannableCodeMetadata(
                                            currentScannableCode.getScannableCodeId(),
                                            AppContext.get().getCurrentPlayer().getUserId())
                                                    .thenAccept(success -> {
                                                        AppContext.get().setCurrentScannableCode(null);
                                                        startActivity(new Intent(DisplayMonsterActivity.this, MyProfile.class));
                                                    });
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
        });
    }

    private void setMonsterName(String name) {
        monsterName.setText(name);
    }

    private void setMonsterScore(long score) {
        monsterScore.setText("Score: " + score);
    }

    private void setMonsterImage(Drawable image) {
        monsterImage.setImageDrawable(image);
    }

    private void setNumPlayersCached(int numPlayers){
        numPlayersValueView.setText(Integer.toString(numPlayers));
    }

    private void setMiniMapImage(Drawable drawable) {
        locationImage.setBackgroundDrawable(drawable);
    }


    /**
     * Called when the observable object is updated
     * @param o     the observable object.
     * @param arg   an argument passed to the {@code notifyObservers}
     *                 method.
     */
    @Override
    public void update(Observable o, Object arg) {
        currentScannableCode = AppContext.get().getCurrentScannableCode();
        Log.d("DisplayMonsterActivity", "called to update");
        setViews();
    }
}
