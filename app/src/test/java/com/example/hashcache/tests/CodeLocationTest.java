package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.Coordinate;

import org.junit.jupiter.api.Test;

public class CodeLocationTest {

    @Test
    void getLocationNameTest(){
        String name = "testname";
        CodeLocation codeLocation = new CodeLocation(name, 1, 2, 3);
        assertEquals(name, codeLocation.getLocationName());
    }

    @Test
    void setLocationNameTest(){
        String newName = "Genesis";
        CodeLocation codeLocation = new CodeLocation("hello?", 1, 2, 3);
        codeLocation.setLocationName(newName);

        assertEquals(newName, codeLocation.getLocationName());
    }

    @Test
    void getCoordinateTest(){
        double x = 1;
        double y = 2;
        double z = 3;
        CodeLocation codeLocation = new CodeLocation("Goodbye?", x, y, z);
        double[] coordinates = codeLocation.getCoordinates().getCoordinates();

        assertEquals(x, coordinates[0]);
        assertEquals(y, coordinates[1]);
        assertEquals(z, coordinates[2]);
    }

    @Test
    void setCoordinateTest(){
        double x = 1;
        double y = 2;
        double z = 3;
        double newY = 7;
        CodeLocation codeLocation = new CodeLocation("Adele?", x, y, z);
        Coordinate newCoordinate = new Coordinate(x, newY, z);
        codeLocation.setCoordinates(newCoordinate);
        double[] coordinates = codeLocation.getCoordinates().getCoordinates();

        assertEquals(x, coordinates[0]);
        assertEquals(newY, coordinates[1]);
        assertEquals(z, coordinates[2]);
    }

    @Test
    void getIdTest(){
        double x = 1;
        double y = 2;
        double z = 3;

        CodeLocation codeLocation = new CodeLocation("Lionel?", x, y, z);

        String expectedId = Double.toString(x) + Double.toString(y) + Double.toString(z);

        assertEquals(expectedId, codeLocation.getId());
    }
}
