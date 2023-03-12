package com.example.hashcache.models.database;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.IPlayerDatabase;
import com.example.hashcache.models.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database_connections.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database_connections.callbacks.GetStringCallback;
import com.example.hashcache.store.AppStore;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PlayerDatabase implements IPlayerDatabase {

    private static PlayerDatabase instance;
    private HashMap<String, Player> players;
    private HashMap<String, String> userNameToIdMapper;
    private HashMap<String, ScannableCode> scannableCodeHashMap;

    @Override
    public CompletableFuture<Boolean> usernameExists(String username) {
        return PlayersConnectionHandler.getInstance().usernameExists(username);

    }

    @Override
    public CompletableFuture<String> getIdByUsername(String username) {

        return PlayersConnectionHandler.getInstance().getPlayerIdByUsername(username);
    }

    @Override
    public CompletableFuture<Void> createPlayer(String username) {

        CompletableFuture<Void> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            usernameExists(username).thenAccept(exists -> {
                if(!exists){
                    PlayersConnectionHandler.getInstance().createPlayer(username, new GetStringCallback() {
                        @Override
                        public void onCallback(String callbackString) {
                            System.out.println("User has been created" + username);
                            cf.complete(null);
                        }
                    });
                }
                else{
                    cf.completeExceptionally(new Exception("Username already exists"));
                }
            });
        });
        return cf;
    }

    @Override
    public CompletableFuture<Player> getPlayer(String userId) {
        CompletableFuture<Player> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            PlayersConnectionHandler.getInstance().getPlayerAsync(userId).thenAccept(playa -> {
                cf.complete(playa);
            }).completeExceptionally(new Exception("Could not get player"));

        });
        return cf;
    }

    @Override
    public CompletableFuture<HashMap<String, String>> getPlayers() {
        return null;
    }

    @Override
    public CompletableFuture<Integer> getTotalScore(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<Void> addComment(String scannableCodeId, Comment comment) {
        return null;
    }

    @Override
    public CompletableFuture<Void> updatePlayerPreferences(String userId, PlayerPreferences playerPreferences) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {

            if (players.containsKey(userId)) {
                Player p = players.get(userId);
                p.setPlayerPreferences(playerPreferences);
                cf.complete(null);
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    @Override
    public CompletableFuture<Void> updateContactInfo(String userId, ContactInfo contactInfo) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                Player p = players.get(userId);
                p.setContactInfo(contactInfo);
                cf.complete(null);
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    @Override
    public CompletableFuture<Void> addScannableCode(String userId, ScannableCode scannableCode) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                Player p = players.get(userId);
                String scanId = scannableCode.getScannableCodeId();
                scannableCodeHashMap.put(scanId, scannableCode);
                p.getPlayerWallet().addScannableCode(scanId);
                cf.complete(null);
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    @Override
    public CompletableFuture<Void> removeScannableCode(String userId, String scannableCodeId) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                Player p = players.get(userId);
                String scanId = scannableCodeId;
                scannableCodeHashMap.remove(scanId);
                p.getPlayerWallet().deleteScannableCode(scanId);
                cf.complete(null);
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    @Override
    public CompletableFuture<Void> changeUserName(String userId, String newUsername) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                Player p = players.get(userId);
                String oldUserName = p.getUsername();
                userNameToIdMapper.remove(oldUserName);
                userNameToIdMapper.put(newUsername, p.getUserId());
                p.updateUserName(newUsername);
                cf.complete(null);
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }
}
