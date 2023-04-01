package com.example.hashcache.unit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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
public class MyProfileTest {

    private Solo solo;
    private String sbStr;
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
        sbStr = sb.toString();

        solo.enterText((EditText) solo.getView(R.id.username_edittext), sb.toString());
        solo.clickOnButton("START CACHING");
        solo.clickOnView(solo.getView(R.id.logo_button));

    }

    private void logout(){
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }

    @Test
    public void checkComponents(){
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
        solo.waitForView(solo.getView(R.id.username_textview));
        solo.waitForView(solo.getView(R.id.score_textview));
        solo.waitForText(sbStr);
        solo.waitForView(solo.getView(R.id.scannable_codes_list));
        solo.waitForView(solo.getView(R.id.qr_stats_button));
        logout();
    }

    @Test
    public void checkLogoButton(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MyProfile.class);
        solo.clickOnImageButton(0);
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
        solo.clickOnView(solo.getView(R.id.logout_button));
    }
}