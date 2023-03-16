package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.hashcache.models.Comment;

import org.junit.jupiter.api.Test;

public class CommentTest {

    @Test
    void getBodyTest(){
        String testBody = "Frankenstein";
        Comment testComment = new Comment(testBody, "randomId");

        assertEquals(testBody, testComment.getBody());
    }

    @Test
    void setBodyTest(){
        String newBody = "Frankenstein 2.0";
        Comment testComment = new Comment("Frankenstein 1.0", "randomId");
        testComment.setBody(newBody);

        assertEquals(newBody, testComment.getBody());
    }

    @Test
    void getCommentatorIdTest(){
        String commentatorId = "SomeoneWithALotOfOpinions";
        Comment testComment = new Comment("wordzzzz", commentatorId);

        assertEquals(commentatorId, testComment.getCommentatorId());
    }
}
