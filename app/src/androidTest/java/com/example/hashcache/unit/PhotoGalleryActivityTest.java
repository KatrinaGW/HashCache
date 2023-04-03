package com.example.hashcache.unit;
import static com.example.hashcache.unit.TestData.TEST_OTHER_USER;
import static com.example.hashcache.unit.TestData.TEST_OTHER_USER_MONSTER_NAME;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;


import com.example.hashcache.views.DisplayMonsterActivity;
import com.example.hashcache.views.MainActivity;
import com.example.hashcache.views.PhotoGalleryActivity;
import com.example.hashcache.R;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

/**
 * Test class for PhotoGalleryActivity. All the UI tests are written here. Robotium test framework is
 used
 */
public class PhotoGalleryActivityTest {

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
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.sleep(100);
        solo.clickOnText(TEST_OTHER_USER_MONSTER_NAME);
        solo.clickOnView(solo.getView(R.id.view_photos_button));
    }

    void logout(){
        solo.clickOnView(solo.getView(R.id.menu_button));
        solo.clickOnView(solo.getView(R.id.my_codes_button));
        solo.clickOnView(solo.getView(R.id.logo_button));
        solo.clickOnView(solo.getView(R.id.logout_button));
    }

    /**
     * Checks that back button returns to Display Monster activity
     */
    @Test
    public void checkBackButton(){
        solo.assertCurrentActivity("Wrong Activity", PhotoGalleryActivity.class);
        solo.clickOnView(solo.getView(R.id.back_button));
        solo.assertCurrentActivity("Wrong Activity", DisplayMonsterActivity.class);
        logout();
    }

}