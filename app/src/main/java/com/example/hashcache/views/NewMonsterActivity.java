package com.example.hashcache.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.hashInfo.ImageGenerator;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.models.database.DatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.CodeMetadataDatabaseAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.util.function.Function;

/**
 * 
 * The NewMonsterActivity class represents an activity in the app that allows
 * users to create a new monster.
 * This activity sets up the UI elements and adds functionality to the buttons,
 * including taking a location photo
 * for the new monster or skipping the photo and navigating to the MyProfile
 * activity. The menu button is also
 * functional, allowing users to navigate to different activities in the app.
 */
public class NewMonsterActivity extends AppCompatActivity {
    /**
     * Called when the activity is created.
     *
     * Initializes the activity by setting up the UI elements and adding
     * functionality to the buttons.
     *
     * @param savedInstanceState saved state of the activity, if it was previously
     *                           closed.
     */

    private final String TAG = "NewMonsterActivity";
    private TextView monsterName;
    private TextView monsterScore;
    private ImageView monsterImage;
    private ImageView miniMap;
    private ImageButton menuButton;
    private TextView takePhotoText;
    private ImageButton photoButton;
    private AppCompatButton skipPhotoButton;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_monster);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        init();

        ScannableCode curCode = AppContext.get().getCurrentScannableCode();
        HashInfo curInfo = curCode.getHashInfo();
        ActivityResultLauncher<Intent> startCapture = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Bundle imageData = result.getData().getExtras();
                            // https://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string
                            Bitmap image = (Bitmap) imageData.get("data");
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            Log.d(TAG, String.format("Image taken. Size: %d bytes", encodedImage.getBytes().length));
                            Log.d("NewMonsterActivity - Camera", "Location photo successfully taken.");
                            // Log.d("New Monster Activity - Camera", encoded);
                            // Sets the scannable code to the image
                            curCode.setImage(image);

                            String userId = AppContext.get().getCurrentPlayer().getUserId();
                            String scannableCodeId = AppContext.get().getCurrentScannableCode().getScannableCodeId();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewMonsterActivity.this,
                                            "Adding image location...",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            Database.getInstance().updatePlayerCodeMetadataImage(userId, scannableCodeId, encodedImage).thenAccept(v -> {
                                Log.d(TAG, "Image successfully added!");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NewMonsterActivity.this,
                                                "Image location added!",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(NewMonsterActivity.this, MyProfile.class));
                                    }
                                });
                            }).exceptionally(new Function<Throwable, Void>() {
                                @Override
                                public Void apply(Throwable throwable) {
                                    Log.d(TAG, throwable.getMessage());
                                    return null;
                                }
                            });

                        }
                    }
                });
        // take location photo
        ImageButton photoButton = findViewById(R.id.photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to activity to take location photo
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startCapture.launch(intent);
            }
        });

        // generate monster image and name
        setMonsterName(curInfo.getGeneratedName());
        setMonsterScore(curInfo.getGeneratedScore());
        ImageGenerator.getImageFromHash(curCode.getScannableCodeId()).thenAccept(drawable -> {
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

        // skip taking location photo (go to my profile)
        AppCompatButton skipPhotoButton = findViewById(R.id.skip_photo_button);
        skipPhotoButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the skip photo button is clicked.
             * <p>
             * Navigates to the MyProfile activity when the skip photo button is clicked.
             *
             * @param v the view that was clicked (the skip photo button)
             */
            @Override
            public void onClick(View v) {

                // no picture
                // check if they allow for metadata

                if (AppContext.get().getCurrentPlayer().getPlayerPreferences().getRecordGeolocationPreference()) {
                    String codeID = curCode.getScannableCodeId();
                    // getLocationAndAdd(codeID);
                }

                // go to profile page
                startActivity(new Intent(NewMonsterActivity.this, MyProfile.class));
            }
        });

        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the menu button is clicked.
             * <p>
             * Creates a popup menu and sets up the menu items. Navigates to different
             * activities based on the menu item
             * that was clicked.
             *
             * @param view the view that was clicked (the menu button)
             */
            @Override
            public void onClick(View view) {
                BottomMenuFragment bottomMenu = new BottomMenuFragment();
                bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
            }
        });
    }

    private void init() {

        monsterName = findViewById(R.id.monster_name);
        monsterScore = findViewById(R.id.monster_score);
        monsterImage = findViewById(R.id.monster_image);
        miniMap = findViewById(R.id.location_image);
        menuButton = findViewById(R.id.menu_button);
        takePhotoText = findViewById(R.id.take_photo_text);
        photoButton = findViewById(R.id.photo_button);
        skipPhotoButton = findViewById(R.id.skip_photo_button);
    }

    public void setMonsterName(String name) {
        monsterName.setText(name);
    }

    public void setMonsterScore(long score) {
        monsterScore.setText("Score: " + score);
    }

    public void setMonsterImage(Drawable image) {
        monsterImage.setImageDrawable(image);
    }

    public void setMiniMapImage(int imageRes) {
        miniMap.setImageResource(imageRes);
    }

    public void setMenuButtonClickListener(View.OnClickListener listener) {
        menuButton.setOnClickListener(listener);
    }

    public void setPhotoButtonClickListener(View.OnClickListener listener) {
        photoButton.setOnClickListener(listener);
    }

    public void setSkipPhotoButtonClickListener(View.OnClickListener listener) {
        skipPhotoButton.setOnClickListener(listener);
    }

}
