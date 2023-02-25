package com.example.hashcache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Coordinate;

import org.junit.jupiter.api.Test;

public class CoordinateTest {

    @Test
    void SetCoordinateThrows(){
        Coordinate coordinate = new Coordinate(0, 0, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            coordinate.setCoordinates(2, 3);
        });
    }

    @Test
    void GetCoordinates(){
        Coordinate coordinate = new Coordinate(1, 2, 3);

        assertEquals(1, coordinate.getCoordinates()[0]);
        assertEquals(2, coordinate.getCoordinates()[1]);
        assertEquals(3, coordinate.getCoordinates()[2]);
    }

    @Test
    void SetCoordinates(){
        Coordinate coordinate = new Coordinate(0, 0, 0);

        coordinate.setCoordinates(72.4, 1);

        assertEquals(72.4, (coordinate.getCoordinates())[1]);
    }
}
