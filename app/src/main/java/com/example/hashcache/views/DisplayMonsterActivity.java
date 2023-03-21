package com.example.hashcache.views;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.hashcache.R;
import com.example.hashcache.controllers.hashInfo.HashController;
import com.example.hashcache.controllers.hashInfo.ImageGenerator;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.context.Context;

import java.util.function.Function;

/**

 The NewMonsterActivity class represents an activity in the app that allows users to create a new monster.
 This activity sets up the UI elements and adds functionality to the buttons, including taking a location photo
 for the new monster or skipping the photo and navigating to the MyProfile activity. The menu button is also
 functional, allowing users to navigate to different activities in the app.
 */
public class DisplayMonsterActivity extends AppCompatActivity {
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
    private ImageView miniMap;
    private ImageButton menuButton;
    private Button viewCacherButton;
    private Button deleteButton;
    private ScannableCode currentScannableCode;
    private boolean belongToCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_monster);

        Intent intent = getIntent();
        belongToCurrentUser = intent.getBooleanExtra("belongsToCurrentUser", false);

        init();
        // take location photo
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteButtonClicked();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentScannableCode = Context.get().getCurrentScannableCode();
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
    }

    private void init() {
        monsterName = findViewById(R.id.monster_name);
        monsterScore = findViewById(R.id.monster_score);
        monsterImage = findViewById(R.id.monster_image);
        miniMap = findViewById(R.id.mini_map);
        menuButton = findViewById(R.id.menu_button);
        deleteButton = findViewById(R.id.delete_button);
        viewCacherButton = findViewById(R.id.view_comments_button);

        if(!belongToCurrentUser){
            deleteButton.setVisibility(View.GONE);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    private void onViewCacherCommentsButtonClicked(){
//        Intent intent = new Intent(getApplicationContext(), DisplayMonsterActivity.class);
//        intent.putExtra("belongsToCurrentUser", true);
//
//        Context.get().setCurrentScannableCode(clickedScannableCode);
//        startActivity(intent);
    }
    private void onDeleteButtonClicked(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HashController.deleteScannableCodeFromWallet(currentScannableCode.getScannableCodeId(),
                                Context.get().getCurrentPlayer().getUserId())
                        .thenAccept(completed -> {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Context.get().setCurrentScannableCode(null);
                                    startActivity(new Intent(DisplayMonsterActivity.this, MyProfile.class));
                                }
                            });
                        });
            }
        });
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
        deleteButton.setOnClickListener(listener);
    }
}
