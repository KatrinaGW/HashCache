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
import com.example.hashcache.views.DisplayMonsterActivity;
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
 * Test class for AppHome. All the UI tests are written here. Robotium test framework is
 used
 */
public class PrivateDisplayMonsterActivityTest {

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

        solo.enterText((EditText) solo.getView(R.id.username_edittext), "Leon");
        solo.clickOnButton("START CACHING");
        solo.clickOnImageButton(0);
        solo.clickOnText("Blobulon Crorg the Fifth Alf");

    }


    @Test
    public void checkInfo(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", DisplayMonsterActivity.class);
        solo.waitForText("Blobulon Crorg the Fifth Alf");
        solo.waitForText("Delete?");
    }

    @Test
    public void checkMenuButton1(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", DisplayMonsterActivity.class);
        solo.clickOnImageButton(1);
        solo.clickOnText("Map");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);
    }

    @Test
    public void checkMenuButton2(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", DisplayMonsterActivity.class);
        solo.clickOnImageButton(1);
        solo.clickOnText("My QR Codes");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
    }

    @Test
    public void checkMenuButton3(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", DisplayMonsterActivity.class);
        solo.clickOnImageButton(1);
        solo.clickOnText("Stats");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", QRStats.class);
    }

    @Test
    public void checkMenuButton4(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", DisplayMonsterActivity.class);
        solo.clickOnImageButton(1);
        solo.clickOnText("Community");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", Community.class);
    }


}