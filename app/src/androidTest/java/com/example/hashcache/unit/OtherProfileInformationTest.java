package com.example.hashcache.unit;

import static com.example.hashcache.unit.TestData.TEST_OTHER_USER;
import static com.example.hashcache.unit.TestData.TEST_OTHER_USER_EMAIL;
import static com.example.hashcache.unit.TestData.TEST_OTHER_USER_PHONE_NUMBER;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.hashcache.R;
import com.example.hashcache.views.Community;
import com.example.hashcache.views.MainActivity;
import com.example.hashcache.views.OtherProfileInformationActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

public class OtherProfileInformationTest {
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
        solo.enterText((EditText) solo.getView(R.id.search_bar_edittext), TEST_OTHER_USER.substring(0, 4));
        solo.clickOnView(solo.getView(R.id.search_button));
        solo.sleep(100);
        solo.waitForText(TEST_OTHER_USER);
        solo.clickOnText(TEST_OTHER_USER);
    }

    @Test
    public void testUsername(){
        solo.assertCurrentActivity("Wrong Activity", OtherProfileInformationActivity.class);
        solo.sleep(100);
        solo.waitForView(solo.getView(R.id.other_username_textview));
        solo.waitForText(TEST_OTHER_USER);
        solo.waitForView(solo.getView(R.id.other_email_textview));
        solo.waitForText(TEST_OTHER_USER_EMAIL);
        solo.waitForView(solo.getView(R.id.other_phone_textview));
        solo.waitForText(TEST_OTHER_USER_PHONE_NUMBER);
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }
}
