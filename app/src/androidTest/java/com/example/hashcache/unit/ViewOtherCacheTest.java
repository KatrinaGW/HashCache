package com.example.hashcache.unit;

import static com.example.hashcache.unit.TestData.TEST_OTHER_USER;

import static org.junit.Assert.assertTrue;

import android.widget.EditText;
import android.widget.ListView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.hashcache.R;
import com.example.hashcache.views.DisplayMonsterActivity;
import com.example.hashcache.views.MainActivity;
import com.example.hashcache.views.OtherCacheActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;
public class ViewOtherCacheTest {
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
        solo.clickOnView(solo.getView(R.id.menu_community_button));
        solo.sleep(100);
        solo.enterText((EditText) solo.getView(R.id.search_bar_edittext), TEST_OTHER_USER.substring(0, 4));
        solo.clickOnView(solo.getView(R.id.search_button));
        solo.sleep(100);
        solo.waitForText(TEST_OTHER_USER);
        solo.clickOnText(TEST_OTHER_USER);
        solo.sleep(100);
        solo.clickOnView(solo.getView(R.id.view_other_player_codes));
    }

    private void logout(){
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }

    @Test
    public void otherCodesVisibleTest(){
        solo.assertCurrentActivity("Wrong Activity", OtherCacheActivity.class);
        solo.waitForText(TEST_OTHER_USER);
        ArrayList<ListView> list = solo.getCurrentViews(ListView.class);
        solo.sleep(100);
        assertTrue(list.size()>0);
        logout();
    }

    @Test
    public void otherPlayerMonsterClickableTest(){
        solo.assertCurrentActivity("Wrong Activity", OtherCacheActivity.class);
        solo.waitForText(TEST_OTHER_USER);
        ArrayList<ListView> list = solo.getCurrentViews(ListView.class);
        assertTrue(list.size()>0);
        solo.clickInList(0, 0);
        solo.assertCurrentActivity("Wrong Activity", DisplayMonsterActivity.class);
        logout();
    }
}
