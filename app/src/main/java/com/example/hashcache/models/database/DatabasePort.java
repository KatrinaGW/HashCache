package com.example.hashcache.models.database;

import android.util.Pair;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetScannableCodeCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    CompletableFuture<ScannableCode> getPlayerWalletTopScore(ArrayList<String> scannableCodeIds);
    CompletableFuture<ScannableCode> getPlayerWalletLowScore(ArrayList<String> scannableCodeIds);
    CompletableFuture<Long> getPlayerWalletTotalScore(ArrayList<String> scannableCodeIds);
    CompletableFuture<ArrayList<ScannableCode>> getScannableCodesByIdInList(ArrayList<String> scannableCodeIds);
    CompletableFuture<ScannableCode> getScannableCodeById(String scannableCodeId);
    CompletableFuture<Boolean> updateContactInfo(ContactInfo contactInfo, String userId);
    CompletableFuture<Pair<String, String>> getUsernameById(String userId);
    CompletableFuture<Integer> getNumPlayersWithScannableCode(String scannableCodeId);
    CompletableFuture<Void> addLoginRecord(String username);
    CompletableFuture<String> getUsernameForDevice();
    CompletableFuture<Void> deleteLogin();
    void resetInstances();
    CompletableFuture<ArrayList<Pair<String, String>>> getUsernamesByIds(ArrayList<String> userIds);
    void onPlayerDataChanged(String userId, GetPlayerCallback callback);
    void onPlayerWalletChanged(String playerId, BooleanCallback callback);
    void onScannableCodeCommentsChanged(String scannableCodeId, GetScannableCodeCallback callback);
    ArrayList<Pair<String, Long>> getTopKUsers(String filter, int k);
    CompletableFuture<Boolean> updatePlayerScores(String userId, PlayerWallet playerWallet);
}
