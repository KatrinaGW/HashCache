package com.example.hashcache.models.database;

import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.ScannableCode;

import java.util.concurrent.CompletableFuture;

public interface IPlayerDatabase {

    CompletableFuture<Boolean> usernameExists(String username);

    CompletableFuture<String> getIdByUsername(String username);

    CompletableFuture<Void> createPlayer(String username);

    CompletableFuture<Player> getPlayer(String userId);

    CompletableFuture<Void> updatePlayerPreferences(String userId, PlayerPreferences playerPreferences);

    CompletableFuture<Void> updateContactInfo(String userId, ContactInfo contactInfo);

    CompletableFuture<Void> addScannableCode(String userId, ScannableCode scannableCode);

    CompletableFuture<Void> removeScannableCode(String userId, String scannableCodeId);


    CompletableFuture<Void> changeUserName(String userId, String newUsername);
}
