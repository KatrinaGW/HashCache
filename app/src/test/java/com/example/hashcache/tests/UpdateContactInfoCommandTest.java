package com.example.hashcache.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.context.Context;
import com.example.hashcache.controllers.AddUserCommand;
import com.example.hashcache.controllers.UpdateContactInfoCommand;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.DatabasePort;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class UpdateContactInfoCommandTest {
    private Context mockContext;
    private DatabasePort mockDB;

    @BeforeEach
    void resetMocks(){
        mockContext = Mockito.mock(Context.class);
        mockDB = Mockito.mock(DatabasePort.class);
    }

    @Test
    void updateContactInfoCommandTest(){
        String testId = "feg";
        ContactInfo testContactInfo = new ContactInfo();
        Player testPlayer = Mockito.mock(Player.class);
        CompletableFuture<Boolean> testCF = new CompletableFuture<>();
        testCF.complete(true);

        when(mockContext.getCurrentPlayer()).thenReturn(testPlayer);
        when(testPlayer.getUserId()).thenReturn(testId);
        when(mockDB.updateContactInfo(testContactInfo, testId)).thenReturn(testCF);

        assertTrue(UpdateContactInfoCommand.updateContactInfoCommand(testId, testContactInfo,
                mockDB, mockContext).join());

        verify(testPlayer, times(1)).setContactInfo(testContactInfo);
    }
}
