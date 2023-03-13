package com.example.hashcache.unit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.hashcache.views.AppHome;
import com.example.hashcache.views.Community;
import com.example.hashcache.views.LeaderboardNumQRActivity;
import com.example.hashcache.views.LeaderboardRegionActivity;
import com.example.hashcache.views.LeaderboardScoreActivity;
import com.example.hashcache.views.LeaderboardTopQRActivity;
import com.example.hashcache.views.MainActivity;
import com.example.hashcache.views.MyProfile;
import com.example.hashcache.views.QRByLocation;
import com.example.hashcache.views.QRScanActivity;
import com.example.hashcache.views.QRStats;
import com.example.hashcache.R;
import com.example.hashcache.views.Settings;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

/**
 * Test class for LeaderboardScoreActivity. All the UI tests are written here. Robotium test framework is
 used
 */
public class LeaderboardScoreActivityTest {

    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < 15; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));

        solo.enterText((EditText) solo.getView(R.id.username_edittext), sb.toString());
        solo.clickOnButton("START CACHING");
        solo.clickOnImageButton(3);
        solo.clickOnImageButton("LEADERBOARD");
    }

    @Test
    public void checkNumQRButton(){
        // Asserts that the current activity is the LeaderboardScoreActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LeaderboardScoreActivity.class);
        solo.clickOnButton("# QR");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", LeaderboardNumQRActivity.class);
    }

    @Test
    public void checkTopQRButton(){
        // Asserts that the current activity is the LeaderboardScoreActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LeaderboardScoreActivity.class);
        solo.clickOnButton("# QR");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", LeaderboardTopQRActivity.class);
    }

    @Test
    public void checkRegionButton(){
        // Asserts that the current activity is the LeaderboardScoreActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LeaderboardScoreActivity.class);
        solo.clickOnButton("REGION");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", LeaderboardRegionActivity.class);
    }

    @Test
    public void checkMenuButton1(){
        // Asserts that the current activity is the LeaderboardScoreActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LeaderboardScoreActivity.class);
        solo.clickOnImageButton(0);
        solo.clickOnText("Map");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);
    }

    @Test
    public void checkMenuButton2(){
        // Asserts that the current activity is the LeaderboardScoreActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LeaderboardScoreActivity.class);
        solo.clickOnImageButton(0);
        solo.clickOnText("My QR Codes");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
    }

    @Test
    public void checkMenuButton3(){
        // Asserts that the current activity is the LeaderboardScoreActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LeaderboardScoreActivity.class);
        solo.clickOnImageButton(0);
        solo.clickOnText("Stats");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", QRStats.class);
    }

    @Test
    public void checkMenuButton4(){
        // Asserts that the current activity is the LeaderboardScoreActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", LeaderboardScoreActivity.class);
        solo.clickOnImageButton(0);
        solo.clickOnText("Community");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", Community.class);
    }

}