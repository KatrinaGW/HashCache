package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.media.Image;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

public class ScannableCodeTest {
    private String testLocationId =UUID.randomUUID().toString();
    private Image mockImage = Mockito.mock(Image.class);
    private String fakeName = "faker!";
    private long testScore = 100;
    private HashInfo testHashInfo = new HashInfo(mockImage, fakeName, testScore);
    private String fakeHash = "123";

    @Test
    void getLocationIdTest(){
        ScannableCode scannableCode = new ScannableCode(testLocationId, new HashInfo(
                mockImage, fakeName, testScore));

        assertEquals(testLocationId, scannableCode.getScannableCodeId());
    }

    @Test
    void getHashInfoTest(){
        HashInfo testHashInfo = new HashInfo(mockImage, fakeName, testScore);
        ScannableCode scannableCode = new ScannableCode(testLocationId, testHashInfo);

        assertEquals(testHashInfo, scannableCode.getHashInfo());
    }

    @Test
    void addCommentTest(){
        Comment newComment = new Comment("hello", "world");
        ScannableCode scannableCode = new ScannableCode(testLocationId, new HashInfo(
                mockImage, fakeName, testScore));
        scannableCode.addComment(newComment);

        assertEquals(newComment, scannableCode.getComments().get(0));
    }

    @Test
    void getCommentsTest(){
        Comment newComment1 = new Comment("hello", "world");
        Comment newComment2 = new Comment("hello2", "world");
        ScannableCode scannableCode = new ScannableCode(testLocationId, new HashInfo(
                mockImage, fakeName, testScore));
        scannableCode.addComment(newComment1);
        scannableCode.addComment(newComment2);

        assertEquals(newComment1, scannableCode.getComments().get(0));
        assertEquals(newComment2, scannableCode.getComments().get(1));
    }
}
