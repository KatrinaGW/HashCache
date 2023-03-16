/*
 * AppHome
 *
 * Main landing page of the app.
 * Displays a map centred on the user's location,
 * with pins indicating QR codes scanned by user and others.
 * Additional buttons permit navigation to other pages.
 */

package com.example.hashcache.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Observable;
import java.util.Observer;

import com.example.hashcache.R;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.Database;
import com.example.hashcache.store.AppStore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**

 Represents the main landing page of the app.

 Displays a map centered on the user's location, with pins indicating QR codes scanned by the user and others.
 */
public class AppHome extends AppCompatActivity implements Observer, OnMapReadyCallback {

    private ImageButton mLogoButton;
    private TextView mUsernameTextView;
    private TextView mScoreTextView;
    private ImageButton mMenuButton;
    private ImageButton mMapButton;
    private ImageButton mCommunityButton;
    private View mMap;
    private AppCompatButton mScanQrButton;
    private Player playerInfo;

    private boolean locationPermissionGranted;

    private GoogleMap map;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private Location lastKnownLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int DEFAULT_ZOOM = 15;

    private final LatLng defaultLocation = new LatLng(45.56312107881602, -81.46643526230866);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_home);
        initView();



        //get the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // add functionality to logo button
        ImageButton logoButton = findViewById(R.id.logo_button);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to user's profile page
                startActivity(new Intent(AppHome.this, MyProfile.class));
            }
        });

        // add functionality to map button
        ImageButton qrLocationButton = findViewById(R.id.map_button);
        qrLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to page showing QR codes near the user (as list)
                startActivity(new Intent(AppHome.this, QRByLocation.class));
            }
        });

        // add functionality to community button
        ImageButton communityButton = findViewById(R.id.community_button);
        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to community page
                startActivity(new Intent(AppHome.this, Community.class));
            }
        });
        Button qrCodeTakeButton = findViewById(R.id.scan_qr_button);
        qrCodeTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppHome.this, QRScanActivity.class));
            }
        });
        // add functionality to menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create menu
                PopupMenu menu = new PopupMenu(AppHome.this, menuButton);
                menu.getMenuInflater()
                        .inflate(R.menu.fragment_popup_menu, menu.getMenu());

                // navigate to different activities based on menu item selected
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_home) {                 // remain on AppHome page
                            return true;

                        } else if (id == R.id.menu_stats) {         // go to QRStats page
                            startActivity(new Intent(AppHome.this, QRStats.class));
                            return true;

                        } else if (id == R.id.menu_profile) {       // go to MyProfile
                            startActivity(new Intent(AppHome.this, MyProfile.class));
                            return true;

                        } else if (id == R.id.menu_community) {     // go to Community
                            startActivity(new Intent(AppHome.this, Community.class));
                            return true;
                        }
                        return AppHome.super.onOptionsItemSelected(item);
                    }
                });
                menu.show();
            }
        });
        playerInfo = AppStore.get().getCurrentPlayer();
        setUIParams();
        AppStore.get().addObserver(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    //runs when the map is ready to receive user input
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }


    //asks the user for permission to get their location
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    //Handles the result of the request for location permissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
        getDeviceLocation();
    }
    //Updates the map's UI settings based on whether the user has granted location permission.
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    private void initView() {
        mLogoButton = findViewById(R.id.logo_button);
        mUsernameTextView = findViewById(R.id.username_textview);
        mScoreTextView = findViewById(R.id.score_textview);
        mMenuButton = findViewById(R.id.menu_button);
        mMapButton = findViewById(R.id.map_button);
        mCommunityButton = findViewById(R.id.community_button);
        mMap = findViewById(R.id.map);
        mScanQrButton = findViewById(R.id.scan_qr_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUIParams();
    }
    /**

     Sets the listener for the logo button.
     @param listener the listener to set
     */
    public void setLogoButtonListener(View.OnClickListener listener) {
        mLogoButton.setOnClickListener(listener);
    }
    /**

     Sets the listener for the menu button.
     @param listener the listener to set
     */
    public void setMenuButtonListener(View.OnClickListener listener) {
        mMenuButton.setOnClickListener(listener);
    }
    /**

     Sets the listener for the map button.
     @param listener the listener to set
     */
    public void setMapButtonListener(View.OnClickListener listener) {
        mMapButton.setOnClickListener(listener);
    }
    /**

     Sets the listener for the community button.
     @param listener the listener to set
     */
    public void setCommunityButtonListener(View.OnClickListener listener) {
        mCommunityButton.setOnClickListener(listener);
    }
    /**

     Sets the listener for the QR scan button.
     @param listener the listener to set
     */
    public void setScanQrButtonListener(View.OnClickListener listener) {
        mScanQrButton.setOnClickListener(listener);
    }
    /**

     Gets the username of the player.
     @return the username of the player
     */
    public String getUsername() {
        return mUsernameTextView.getText().toString();
    }
    /**

     Sets the username of the player.
     @param username the username of the player
     */
    public void setUsername(String username) {
        mUsernameTextView.setText(username);
    }
    /**

     Gets the score of the player.
     @return the score of the player
     */
    public int getScore() {
        String scoreStr = mScoreTextView.getText().toString().replace("Score: ", "");
        return Integer.parseInt(scoreStr);
    }
    /**

     Sets the score of the player.
     @param score the score of the player
     */
    public void setScore(long score) {
        mScoreTextView.setText("Score: " + score);
    }
    /**

     Sets the listener for the temporary map view.
     @param listener the listener to set
     */
    public void setMapTempViewClickListener(OnClickListener listener) {
        mMap.setOnClickListener(listener);
    }

    @Override
    public void update(Observable observable, Object o) {
        setUIParams();
    }

    public void setUIParams(){
        Player currentPlayer = AppStore.get().getCurrentPlayer();
        setUsername(currentPlayer.getUsername());
        setScore(currentPlayer.getTotalScore());

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
