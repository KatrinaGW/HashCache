package com.example.hashcache.tests;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.context.Context;
import com.example.hashcache.controllers.LoginUserCommand;
import com.example.hashcache.controllers.SetupUserCommand;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.DatabasePort;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class LoginUserCommandTest {
    DatabasePort mockDB;
    Context mockContext;

    @BeforeEach
    void resetMocks(){
        mockDB = Mockito.mock(DatabasePort.class);
        mockContext = Mockito.mock(Context.class);
    }
    @Test
    void loginUserTest(){
        String testUsername = "this is not a username";
        Player testPlayer = new Player(testUsername);
        SetupUserCommand mockSetUpUserCommand = Mockito.mock(SetupUserCommand.class);
        CompletableFuture<Boolean> existsCF = new CompletableFuture<>();
        CompletableFuture<Void> nullCF = new CompletableFuture<>();
        nullCF.complete(null);
        existsCF.complete(true);
        CompletableFuture<Player> playerCF = new CompletableFuture<>();
        playerCF.complete(testPlayer);
        CompletableFuture<String> idCF = new CompletableFuture<>();
        idCF.complete(testPlayer.getUserId());

        when(mockDB.usernameExists(testUsername)).thenReturn(existsCF);
        when(mockDB.getIdByUsername(testUsername)).thenReturn(idCF);
        when(mockDB.getPlayer(testPlayer.getUserId())).thenReturn(playerCF);
        when(mockSetUpUserCommand.setupUser(testUsername, mockDB, mockContext))
                .thenReturn(nullCF);

        LoginUserCommand loginUserCommand = new LoginUserCommand();

        assertNull(loginUserCommand.loginUser(testUsername, mockDB, mockContext, mockSetUpUserCommand)
                .join());

        verify(mockSetUpUserCommand, times(1)).setupUser(testUsername,
                mockDB, mockContext);
    }
}
