package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.media.Image;

import com.example.hashcache.models.HashInfo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HashInfoTest {
    private Image testImage = Mockito.mock(Image.class);

    @Test
    void testGetGeneratedImage(){
        HashInfo testHashInfo = new HashInfo(testImage, "name", 123);

        assertEquals(testHashInfo.getGeneratedImage(), testImage);
    }

    @Test
    void testGetGeneratedScore(){
        long testScore = 193;
        HashInfo testHashInfo = new HashInfo(testImage, "name", testScore);

        assertEquals(testHashInfo.getGeneratedScore(), testScore);
    }

    @Test
    void testGetGeneratedName(){
        String testName = "???????";
        HashInfo testHashInfo = new HashInfo(testImage, testName, 123);

        assertEquals(testHashInfo.getGeneratedName(), testName);
    }
}
