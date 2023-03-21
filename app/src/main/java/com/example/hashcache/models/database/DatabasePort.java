package com.example.hashcache.models.database;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetPlayerCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public interface DatabasePort {

    CompletableFuture<Boolean> usernameExists(String username);

    CompletableFuture<String> getIdByUsername(String username);

    CompletableFuture<Void> createPlayer(String username);

    CompletableFuture<Player> getPlayer(String userId);
    CompletableFuture<HashMap<String, String>> getPlayers();

    CompletableFuture<Long> getTotalScore(String userId);

    CompletableFuture<Void> addComment(String scannableCodeId, Comment comment);

    CompletableFuture<Boolean> deleteComment(String scannableCodeId, String commentId);

    CompletableFuture<Void> updatePlayerPreferences(String userId, PlayerPreferences playerPreferences);

    CompletableFuture<String> addScannableCode(ScannableCode scannableCode);
    CompletableFuture<Void> addScannableCodeToPlayerWallet(String userId, String scannableCodeId);
    CompletableFuture<Boolean> scannableCodeExistsOnPlayerWallet(String userId, String scannableCodeId);
    CompletableFuture<Boolean> scannableCodeExists(String scannableCodeId);

    CompletableFuture<Boolean> removeScannableCodeFromWallet(String userId, String scannableCodeId);

    CompletableFuture<Void> changeUserName(String userId, String newUsername);

    CompletableFuture<ScannableCode> getPlayerWalletTopScore(ArrayList<String> scannableCodeIds);
    CompletableFuture<ScannableCode> getPlayerWalletLowScore(ArrayList<String> scannableCodeIds);
    CompletableFuture<Long> getPlayerWalletTotalScore(ArrayList<String> scannableCodeIds);
    CompletableFuture<ArrayList<ScannableCode>> getScannableCodesByIdInList(ArrayList<String> scannableCodeIds);
    CompletableFuture<ScannableCode> getScannableCodeById(String scannableCodeId);
    CompletableFuture<Boolean> updateContactInfo(ContactInfo contactInfo, String userId);
    CompletableFuture<String> getUsernameById(String userId);
    void onPlayerDataChanged(String userId, GetPlayerCallback callback);
    void onPlayerWalletChanged(String playerId, BooleanCallback callback);
}
