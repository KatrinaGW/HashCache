package com.example.hashcache.views;

import android.content.Context;
import android.graphics.drawable.Drawable;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.CodeMetadata;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.Database;
import com.firebase.geofire.GeoLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 
 * The PhotoGalleryActivity displays all the location photos for a scannable
 * code.
 * This activity sets up the UI elements (a scrollable list of photos and
 * associated location text).
 * The back button allows users to return to the monster info page for the
 * scannable code.
 * The menu button allows users to navigate to different activities in the app.
 */
public class PhotoGalleryActivity extends AppCompatActivity implements Observer {
    private ListView photoList;
    private PhotoGalleryArrayAdapter photoGalleryArrayAdapter;

    private TextView monsterName;
    private ImageButton menuButton;
    private Button backButton;

    private ScannableCode currentScannableCode;

    /**
     * Called when the activity is created.
     *
     * Initializes the activity by setting up the UI elements and adding
     * functionality to the buttons.
     *
     * @param savedInstanceState saved state of the activity, if it was previously
     *                           closed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        initializeViews();

        AppContext.get().addObserver(this);

        // add functionality to back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClicked();
            }
        });

        // add functionality to menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuButtonClicked();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setViews();
        setPhotoGalleryAdapter();
    }

    private void initializeViews() {
        monsterName = findViewById(R.id.monster_name);
        menuButton = findViewById(R.id.menu_button);
        backButton = findViewById(R.id.back_button);
        photoList = findViewById(R.id.photo_list);
    }

    private void setViews() {
        currentScannableCode = AppContext.get().getCurrentScannableCode();
        HashInfo currentHashInfo = currentScannableCode.getHashInfo();
        setMonsterName(currentHashInfo.getGeneratedName());
    }

    /**
     * Checks database for location photos of current scannable code
     * Passes location photo and location information to PhotoGalleryArrayAdapter
     * as Array<Pair<String, String>> (photo, location).
     */
    private void setPhotoGalleryAdapter() {
        String scannableCodeId = currentScannableCode.getScannableCodeId();
        ArrayList<HashMap<String, Object>> photoListData = new ArrayList<>();

        PhotoGalleryActivity activityContext = this;

        // get metadata for all instances of this scannable code
        Database.getInstance().getCodeMetadataById(scannableCodeId).thenAccept(codeMetadata -> {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // for each instance of the scannable code
                    for (CodeMetadata data : codeMetadata) {
                        // if there is a location photo
                        if (data.hasImage()) {
                            HashMap<String, Object> dataMap = new HashMap<>();
                            String base64Image = data.getImage();
                            dataMap.put("base64Image", base64Image);
                            dataMap.put("codeMetadata", data);
                            Database.getInstance().getUsernameById(data.getUserId()).thenAccept(userName -> {
                                dataMap.put("userName", userName.second);
                                if (data.hasLocation()) {
                                    Geocoder gc = new Geocoder(getApplicationContext());
                                    GeoLocation geoloc = data.getLocation();
                                    double lat = geoloc.latitude;
                                    double lng = geoloc.longitude;
                                    dataMap.put("locationText", String.format("Lat: %f, Lng: %f", lat, lng));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        gc.getFromLocation(geoloc.latitude, geoloc.longitude, 1,
                                                new Geocoder.GeocodeListener() {
                                                    @Override
                                                    public void onGeocode(@NonNull List<Address> list) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (!list.isEmpty()) {
                                                                    String addr = list.get(0).getAddressLine(0);
                                                                    if (addr != null) {
                                                                        dataMap.put("locationText", addr);
                                                                    }
                                                                }
                                                                photoGalleryArrayAdapter.add(dataMap);

                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                }
                                else{
                                    photoGalleryArrayAdapter.add(dataMap);
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
                    // give info to array adapter
                    photoGalleryArrayAdapter = new PhotoGalleryArrayAdapter(activityContext, photoListData);
                    photoList.setAdapter(photoGalleryArrayAdapter);
                }
            });

        });
        // .exceptionally(new Function<Throwable, Void>() {
        // @Override
        // public Void apply(Throwable throwable) {
        // Log.d("ERROR", throwable.getMessage());
        // return null;
        // }
        // });
    }

    // go back to monster info activity when back button clicked
    private void onBackButtonClicked() {
        // end activity
        finish();
    }

    // open menu when button clicked
    private void onMenuButtonClicked() {
        BottomMenuFragment bottomMenu = new BottomMenuFragment();
        bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
    }

    // set monster name in header to name of current monster
    public void setMonsterName(String name) {
        monsterName.setText(name);
    }

    /**
     * Called when the observable object is updated
     * 
     * @param o   the observable object.
     * @param arg an argument passed to the {@code notifyObservers}
     *            method.
     */
    @Override
    public void update(Observable o, Object arg) {
        currentScannableCode = AppContext.get().getCurrentScannableCode();
        Log.d("PhotoGalleryActivity", "called to update");
        setViews();
    }

}
