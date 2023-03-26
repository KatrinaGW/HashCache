package com.example.hashcache.tests;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.controllers.LogoutCommand;
import com.example.hashcache.models.Comment;
import com.example.hashcache.models.database.DatabasePort;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class LogoutCommandTest {
    @Test
    void logoutTest(){
        DatabasePort mockDb = Mockito.mock(DatabasePort.class);
        CompletableFuture<Void> nullCF = new CompletableFuture<>();
        nullCF.complete(null);

        when(mockDb.deleteLogin()).thenReturn(nullCF);

        CompletableFuture<Void> result = LogoutCommand.logout(mockDb);

        assertNull(result.join());
        verify(mockDb, times((1))).deleteLogin();
    }
}
