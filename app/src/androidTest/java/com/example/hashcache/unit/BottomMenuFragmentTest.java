package com.example.hashcache.unit;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.hashcache.R;
import com.example.hashcache.views.AppHome;
import com.example.hashcache.views.Community;
import com.example.hashcache.views.LeaderboardScoreActivity;
import com.example.hashcache.views.MainActivity;
import com.example.hashcache.views.MyProfile;
import com.example.hashcache.views.QRScanActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

/**
 * Test class for bottom sheet menu. All the UI tests are written here.
 * Robotium test framework is used.
 */

public class BottomMenuFragmentTest {
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
    }

    private void logout(){
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }

    @Test
    public void checkAppHomeButton(){
        // Asserts that the menu button navigates to app home activity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);
        // open menu
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.sleep(100);
        // select map button
        solo.clickOnView(solo.getView(R.id.map_button));
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);
        logout();
    }

    @Test
    public void checkLeaderboardButton(){
        // Asserts that the menu button navigates to leaderboard activity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);
        // open menu
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.sleep(100);
        // select leaderboard button
        solo.clickOnView(solo.getView(R.id.leaderboard_button));
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", LeaderboardScoreActivity.class);
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnText("My QR Codes");
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }

    @Test
    public void checkScanQRButton(){
        // Asserts that the menu button navigates to scan QR activity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);
        // open menu
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.sleep(100);
        // select QRScan button
        solo.clickOnView(solo.getView(R.id.scan_qr_button, 1));
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", QRScanActivity.class);
        solo.goBack();
        logout();
    }

    @Test
    public void checkProfileButton(){
        // Asserts that the menu button navigates to profile activity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);
        // open menu
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.sleep(100);
        // select my profile button
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
        logout();
    }

    @Test
    public void checkCommunityButton(){
        // Asserts that the menu button navigates to leaderboard activity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);
        // open menu
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.sleep(100);
        // select community button
        solo.clickOnView(solo.getView(R.id.community_button, 1));
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", Community.class);
        logout();
    }
}
