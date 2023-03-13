package com.example.hashcache.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.hashcache.R;
import com.example.hashcache.models.database_connections.CodeLocationConnectionHandler;
import com.example.hashcache.models.database_connections.FireStoreHelper;
import com.example.hashcache.models.database_connections.converters.CodeLocationDocumentConverter;
import com.example.hashcache.views.AppHome;
import com.example.hashcache.views.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import java.util.Random;


/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */
public class MainActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }


    @Test
    public void checkEmptyInput(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("START CACHING");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

    }

    @Test
    public void checkUsedUsername(){
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "a");
        solo.clickOnButton("START CACHING");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }



    @Test
    public void checkCorrectInput(){

        //random string generator
        final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(15);
        for(int i=0;i<15;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));

        solo.enterText((EditText) solo.getView(R.id.username_edittext), sb.toString());
        solo.clickOnButton("START CACHING");
        solo.sleep(100);
        solo.assertCurrentActivity("Wrong Activity", AppHome.class);

    }



}































