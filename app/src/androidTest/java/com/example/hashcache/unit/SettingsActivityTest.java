package com.example.hashcache.unit;

import static com.example.hashcache.unit.TestData.TEST_OTHER_USER;
import static com.example.hashcache.unit.TestData.TEST_OTHER_USER_EMAIL;
import static com.example.hashcache.unit.TestData.TEST_OTHER_USER_PHONE_NUMBER;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;


import com.example.hashcache.R;
import com.example.hashcache.views.AppHome;
import com.example.hashcache.views.Community;
import com.example.hashcache.views.MainActivity;
import com.example.hashcache.views.MyProfile;
import com.example.hashcache.views.QRStats;
import com.example.hashcache.views.SettingsActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;


/**
 * Test class for AppHome. All the UI tests are written here. Robotium test framework is
 used
 */
public class SettingsActivityTest {

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

        solo.enterText((EditText) solo.getView(R.id.username_edittext), TEST_OTHER_USER);
        solo.clickOnButton("START CACHING");
        solo.clickOnImageButton(0);
        solo.clickOnImageButton(0);

    }

    private void logout(){
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }

    @Test
    public void checkPlayerSettings(){
        solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
        solo.waitForText(TEST_OTHER_USER_PHONE_NUMBER);
        solo.waitForText(TEST_OTHER_USER_EMAIL);
        solo.waitForText(TEST_OTHER_USER);
        logout();

    }

    @Test
    public void checkEditPlayerInfoButton(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
        solo.clickOnView(solo.getView(R.id.edit_info_image));
        solo.sleep(100);
        solo.waitForView(solo.getView(R.id.confirm_player_info_button));
        logout();
    }


}