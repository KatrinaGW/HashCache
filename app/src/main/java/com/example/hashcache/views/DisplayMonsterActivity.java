package com.example.hashcache.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

 The DisplayMonsterActivity class represents an activity in the app that allows users to create a new monster.
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

    /**
     * set the content of the views for DisplayMonster activity
     */
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
                    Bitmap rotatedBitmap = rotateScaleBitmap(bitmapImage, 90, 350);
                    bitmapImage.recycle();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setMiniMapImage(new BitmapDrawable(getResources(), rotatedBitmap));
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

    /**
     * Rotates and scales a supplied bitmap image <angle> degrees. Returns the rotated and scaled bitmap.
     * Source: https://stackoverflow.com/
     * Question: https://stackoverflow.com/questions/9015372/how-to-rotate-a-bitmap-90-degrees
     * Answer: https://stackoverflow.com/a/16219591
     * @param bitmap The bitmap image to be rotated
     * @param angle The angle to rotate the bitmap, as a float
     * @param newW The new width of the bitmap, as an integer
     * @return The rotated and scaled bitmap image
     */
    private static Bitmap rotateScaleBitmap(Bitmap bitmap, float angle, int newW) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scale = ((float)newW) / w;

        Matrix matrix = new Matrix();

        // scale bitmap
        matrix.postScale(scale, scale);
        // rotate bitmap
        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * initialize views and determine if delete button should be visible
     */
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

        // show the delete button ONLY if the monster is in the current user's cache
        if(!belongToCurrentUser){
            deleteButton.setVisibility(View.GONE);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    /** go to comments activity when comments button clicked
     *
     */
    private void onCommentsButtonClicked(){
        Intent intent = new Intent(getApplicationContext(), DisplayCommentsActivity.class);

        startActivity(intent);
    }

    /** got to photo gallery when photo button clicked
     *
     */
    private void onPhotoButtonClicked(){
        Intent intent = new Intent(getApplicationContext(), PhotoGalleryActivity.class);

        startActivity(intent);
    }

    /** delete monster from player wallet when delete button clicked
     *
     */
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

    /**
     * Sets the monster name TextView in the activity header to the name of the current monster
     * @param name The name of the monster (String)
     */
    private void setMonsterName(String name) {
        monsterName.setText(name);
    }

    /**
     * Sets the score TextView in the activity header to the monster's score
     * @param score The score of the monster (Long)
     */
    private void setMonsterScore(long score) {
        monsterScore.setText("Score: " + score);
    }

    /**
     * Sets the monster image
     * @param image The Drawable object to use as a monster image
     */
    private void setMonsterImage(Drawable image) {
        monsterImage.setImageDrawable(image);
    }

    /**
     * Displays the number of other users that have also scanned this QR code
     * @param numPlayers The number of players (Integer)
     */
    private void setNumPlayersCached(int numPlayers){
        numPlayersValueView.setText(Integer.toString(numPlayers));
    }

    /**
     * Sets the location image (photo of where the QR code was scanned)
     * @param drawable The Drawable object to set as the location image
     */
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
