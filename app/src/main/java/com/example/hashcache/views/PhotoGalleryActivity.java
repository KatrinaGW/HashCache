package com.example.hashcache.views;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**

 The PhotoGalleryActivity displays all the location photos for a scannable code.
 This activity sets up the UI elements (a scrollable list of photos and associated location text).
 The back button allows users to return to the monster info page for the scannable code.
 The menu button allows users to navigate to different activities in the app.
 */
public class PhotoGalleryActivity extends AppCompatActivity implements Observer {
    private ListView photoList;
    private PhotoGalleryArrayAdapter photoGalleryArrayAdapter;
    //private ArrayList<<Pair> > photosList

    private TextView monsterName;
    private ImageButton menuButton;
    private Button backButton;

    private ScannableCode currentScannableCode;

    /**
     * Called when the activity is created.
     *
     * Initializes the activity by setting up the UI elements and adding functionality to the buttons.
     *
     * @param savedInstanceState saved state of the activity, if it was previously closed.
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
            public void onClick(View view) { onMenuButtonClicked(); }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setViews();
        //setPhotoGalleryAdapter();
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


    // TODO: give location photos & text for scannable code to array adapter
    private void setPhotoGalleryAdapter(){
        // for each location photo attached to scannable code
        // get location text if exists, get location photo
        // add to list
        ArrayList<Pair<String, Drawable>> photoTextAndLocation = new ArrayList<>();
        photoGalleryArrayAdapter = new PhotoGalleryArrayAdapter(this, photoTextAndLocation);
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
     * @param o     the observable object.
     * @param arg   an argument passed to the {@code notifyObservers}
     *                 method.
     */
    @Override
    public void update(Observable o, Object arg) {
        currentScannableCode = AppContext.get().getCurrentScannableCode();
        Log.d("PhotoGalleryActivity", "called to update");
        setViews();
    }

}
