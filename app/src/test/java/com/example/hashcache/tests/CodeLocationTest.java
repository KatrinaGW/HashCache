package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.location.Location;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.Coordinate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CodeLocationTest {
    private Location mockLocation = Mockito.mock(Location .class);

    @Test
    void getLocationTest(){
        String name = "testname";
        CodeLocation codeLocation = new CodeLocation(name, mockLocation);
        assertEquals(mockLocation, codeLocation.getLocation());
    }

    @Test
    void setLocationNameTest(){
        String newName = "Genesis";
        CodeLocation codeLocation = new CodeLocation("hello?", null);
        codeLocation.setLocation(mockLocation);

        assertEquals(mockLocation, codeLocation.getLocation());
    }

    @Test
    void getIdTest(){
        String testId = "oifhg";

        CodeLocation codeLocation = new CodeLocation(testId, mockLocation);

        assertEquals(testId, codeLocation.getId());
    }
}
