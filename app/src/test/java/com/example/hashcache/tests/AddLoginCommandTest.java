package com.example.hashcache.tests;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.AddLoginCommand;
import com.example.hashcache.models.database.DatabasePort;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class AddLoginCommandTest {
    private DatabasePort mockDb;

    @BeforeEach
    void resetMocks(){
        mockDb = Mockito.mock(DatabasePort.class);
    }

    @Test
    void addLoginTest(){
        CompletableFuture<Void> cf = new CompletableFuture<>();
        cf.complete(null);
        String testUsername = "I am groot";
        AppContext fakeContext = Mockito.mock(AppContext.class);

        when(mockDb.addLoginRecord(testUsername)).thenReturn(cf);

        CompletableFuture<Void> result = AddLoginCommand.addLogin(testUsername,
                fakeContext, mockDb);

        assertNull(result.join());
        verify(mockDb, times(1)).addLoginRecord(testUsername);
    }
}
