package com.example.hashcache.unit;
import static com.example.hashcache.unit.TestData.TEST_OTHER_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.hashcache.R;
import com.example.hashcache.views.AppHome;
import com.example.hashcache.views.Community;
import com.example.hashcache.views.LeaderboardScoreActivity;
import com.example.hashcache.views.MainActivity;
import com.example.hashcache.views.MyProfile;
import com.example.hashcache.views.OtherCacheActivity;
import com.example.hashcache.views.OtherProfileInformationActivity;
import com.example.hashcache.views.QRStats;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;


/**
 * Test class for AppHome. All the UI tests are written here. Robotium test framework is
 used
 */
public class CommunityTest {

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
        solo.clickOnView(solo.getView(R.id.community_button));
        solo.sleep(100);

    }

    private void logout(){
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }

    @Test
    public void checkCommunityButton(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", Community.class);
        logout();
    }

    @Test
    public void checkPlayerSearch(){
        solo.assertCurrentActivity("Wrong Activity", Community.class);
        solo.enterText((EditText) solo.getView(R.id.search_bar_edittext), TEST_OTHER_USER.substring(0, 4));
        solo.clickOnView(solo.getView(R.id.search_button));
        solo.sleep(100);
        solo.clickOnText(TEST_OTHER_USER);
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity!", OtherProfileInformationActivity.class);
        solo.sleep(100);
        solo.clickOnView(solo.getView(R.id.view_other_player_codes));
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity!", OtherCacheActivity.class);
        logout();
    }

    @Test
    public void checkMenuButton1(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", Community.class);
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.map_button));
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }

    @Test
    public void checkMenuButton2(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", Community.class);
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }

    @Test
    public void checkMenuButton3(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", Community.class);
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.community_button));
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", Community.class);
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }




}