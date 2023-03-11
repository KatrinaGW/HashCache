package com.example.hashcache;


import androidx.test.core.app.ActivityScenario;

import com.example.hashcache.controllers.HashInfoGenerator;

import org.junit.Test;

import java.util.Random;

/**
 * Tests name generator test
 * NOTE: is a android test as we need the app to run to access the name generator
 */
public class generatorTest {

    // CITATION: used https://developer.android.com/reference/androidx/test/core/app/ActivityScenario
    // to learn how to launch the main activity so I can test the name generator
    @Test
    public void setup() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            // Your test code goes here
            String name = HashInfoGenerator.NameGenerator.generateName(new Random().nextLong());
            assert(name.length() > 1);
        }

    }
}
