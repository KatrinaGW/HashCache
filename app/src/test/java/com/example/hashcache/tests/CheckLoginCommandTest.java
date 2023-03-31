package com.example.hashcache.tests;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.CheckLoginCommand;
import com.example.hashcache.controllers.SetupUserCommand;
import com.example.hashcache.models.database.DatabasePort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class CheckLoginCommandTest {
    private DatabasePort mockDb;
    private SetupUserCommand mockSetUpUserCommand;

    @BeforeEach
    void resetMocks(){
        mockDb = Mockito.mock(DatabasePort.class);
        mockSetUpUserCommand = Mockito.mock(SetupUserCommand.class);
    }

    @Test
    void checkLoginTest(){
        String testUsername = "This is not the username you are looking for";
        CompletableFuture<String> usernameCf = new CompletableFuture<>();
        usernameCf.complete(testUsername);
        CompletableFuture<Void> nullCf = new CompletableFuture<>();
        nullCf.complete(null);

        when(mockDb.getUsernameForDevice()).thenReturn(usernameCf);
        when(mockSetUpUserCommand.setupUser(anyString(), any(DatabasePort.class),
                any(AppContext.class))).thenReturn(nullCf);

        CompletableFuture<Boolean> result = CheckLoginCommand.checkLogin(mockDb,
                mockSetUpUserCommand);

        assertTrue(result.join());
        verify(mockSetUpUserCommand, times((1))).setupUser(anyString(), any(DatabasePort.class),
                any(AppContext.class));
    }
}
