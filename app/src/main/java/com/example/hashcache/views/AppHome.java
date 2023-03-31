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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CompletableFuture;

import com.example.hashcache.R;
import com.example.hashcache.controllers.UpdateUserPreferencesCommand;
import com.example.hashcache.models.CodeMetadata;
import com.example.hashcache.models.Player;

import com.example.hashcache.models.database.Database;
import com.firebase.geofire.GeoLocation;
import com.example.hashcache.appContext.AppContext;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.example.hashcache.context.Context;

/**

 Represents the main landing page of the app.

 Displays a map centered on the user's location, with pins indicating QR codes scanned by the user and others.
 */
public class AppHome extends AppCompatActivity implements Observer, OnMapReadyCallback {

    private ImageButton mLogoButton;
    private TextView mUsernameTextView;
    private TextView mScoreTextView;
    private ImageButton mMenuButton;
    private ImageButton mCommunityButton;
    private View mMap;
    private AppCompatButton mScanQrButton;
    private Player playerInfo;

    private static final String TAG = AppHome.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;



    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(45.564694, -81.462021);
    private static final int DEFAULT_ZOOM = 13;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    SearchView searchView;

    int test = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_home);
        initView();



        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }



        searchView = findViewById(R.id.idSearchView);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                List<Address> addressList = null;

                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(AppHome.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try{
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                    catch (IndexOutOfBoundsException e) {
                        Toast.makeText(AppHome.this, "Couldn't find location!", Toast.LENGTH_SHORT).show();
                    }


                    //not sure if we want a marker there or not
                    //map.addMarker(new MarkerOptions().position(latLng).title(location));


                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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



        // add functionality to community button
        ImageButton markerButton = findViewById(R.id.community_button);
        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RENDER ALL MARKERS IN VISION
                double zoom = map.getCameraPosition().zoom;
                Log.e("Zoom Level:", zoom + "");
                double radius = ((40000/Math.pow(2,zoom)) * 2) * 1000;
                Log.e("Radius:", radius + "");


                GeoLocation currentLocation = new GeoLocation(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude);
                Database.getInstance().getCodeMetadataWithinRadius(currentLocation, radius).thenAccept(allMarkers -> {
                    for (CodeMetadata mark: allMarkers){
                        Log.e("Markers", "MARKER PLACED PEPEGA");
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(mark.getLocation().latitude, mark.getLocation().longitude))
                                .title("Marker in Sydney"));
                    }
                });

                //LatLng right = new LatLng(map.getCameraPosition().target.latitude + radius, map.getCameraPosition().target.longitude + radius);

            }
        });

        // add functionality to scan QR button
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
            public void onClick(View view) {
                BottomMenuFragment bottomMenu = new BottomMenuFragment();
                bottomMenu.show(getSupportFragmentManager(), bottomMenu.getTag());
            }
        });


        playerInfo = AppContext.get().getCurrentPlayer();
        setUIParams();
        AppContext.get().addObserver(this);

    }



    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }



    //runs when the map is ready to receive user input
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        Log.d("TEST", "Granted entry, permission is ok");
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
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
                UpdateUserPreferencesCommand.toggleGeoLocationPreference(true, Context.get(),
                        Database.getInstance());
                getDeviceLocation();
            } else {
                map.setMyLocationEnabled(false);
                map.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                UpdateUserPreferencesCommand.toggleGeoLocationPreference(false, Context.get(),
                        Database.getInstance());

                //getLocationPermission();


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
        Player currentPlayer = AppContext.get().getCurrentPlayer();
        setUsername(currentPlayer.getUsername());
        setScore(currentPlayer.getPlayerWallet().getTotalScore());

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
