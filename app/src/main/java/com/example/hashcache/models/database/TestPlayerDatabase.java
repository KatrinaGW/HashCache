package com.example.hashcache.models.database;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database_connections.ScannableCodesConnectionHandler;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 
 * The TestPlayerDatabase class represents a test database of players that can
 * be interacted with.
 * It implements the IPlayerDatabase interface which defines the methods that
 * can be performed on the database.
 */

public class TestPlayerDatabase implements IPlayerDatabase {

    private static PlayerDatabase instance;
    private HashMap<String, Player> players;
    private HashMap<String, String> userNameToIdMapper;
    private HashMap<String, ScannableCode> scannableCodeHashMap;

    /**
     * Constructs a new TestPlayerDatabase object with empty maps for players,
     * usernames, and scannable codes.
     */
    public TestPlayerDatabase() {
        userNameToIdMapper = new HashMap<>();
        players = new HashMap<>();
        scannableCodeHashMap = new HashMap<>();
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username the username to check
     * @return a CompletableFuture that will return true if the username already
     *         exists, false otherwise
     */
    @Override
    public CompletableFuture<Boolean> usernameExists(String username) {

        System.out.println("Test -> usernameExists");
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (userNameToIdMapper.containsKey(username)) {
                cf.complete(true);
            } else {
                cf.complete(false);
            }
        });
        return cf;
    }

    /**
     * Get all the Scannable Codes whose ids are in a given list
     * @param scannableCodeIds the list of ids of scannable codes to get
     * @return cf the CompleteableFuture with the list of ScannableCodes
     */
    public CompletableFuture<ArrayList<ScannableCode>> getScannableCodesByIdInList(ArrayList<String> scannableCodeIds){
        CompletableFuture<ArrayList<ScannableCode>> cf = new CompletableFuture<>();
        ArrayList<ScannableCode> scannableCodes = new ArrayList<>();

        CompletableFuture.runAsync(() -> {
            for(ScannableCode scannableCode : scannableCodeHashMap.values()){
                scannableCodes.add(scannableCode);
            }

            cf.complete(scannableCodes);
        });
        return cf;
    }

    /**
     * Gets the userId associated with a given username.
     *
     * @param username the username to get the userId for
     * @return a CompletableFuture that will return the userId associated with the
     *         given username
     *         if it exists, or an exception if the username does not exist in the
     *         database
     */
    @Override
    public CompletableFuture<String> getIdByUsername(String username) {
        CompletableFuture<String> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (userNameToIdMapper.containsKey(username)) {
                cf.complete(userNameToIdMapper.get(username));
            } else {
                cf.completeExceptionally(new Exception("Username does not exist"));
            }
        });
        return cf;
    }

    /**
     * Creates a new player with the given username and adds it to the database.
     *
     * @param username the username of the new player to create
     * @return a CompletableFuture that will complete successfully if the player was
     *         created successfully,
     *         or an exception if the username already exists in the database
     */
    @Override
    public CompletableFuture<Void> createPlayer(String username) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (!userNameToIdMapper.containsKey(username)) {
                Player p = new Player(username);
                userNameToIdMapper.put(username, p.getUserId());
                players.put(p.getUserId(), p);
                cf.complete(null);
            } else {
                cf.completeExceptionally(new Exception("Username already exists"));
            }

        });
        return cf;
    }

    /**
     * Gets the player associated with a given userId.
     *
     * @param userId the userId of the player to get
     * @return a CompletableFuture that will return the Player object associated
     *         with the given userId
     *         if it exists, or an exception if the userId does not exist in the
     *         database
     */
    @Override
    public CompletableFuture<Player> getPlayer(String userId) {
        CompletableFuture<Player> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                cf.complete(players.get(userId));
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    /**
     * Gets the players in the database.
     *
     * @return a CompletableFuture that will return a HashMap of player userIds to
     *         usernames
     */
    @Override
    public CompletableFuture<HashMap<String, String>> getPlayers() {
        return null;
    }

    /**
     * Gets the total score of a player.
     *
     * @param userId the userId of the player to get the total score for
     * @return a CompletableFuture that will return the total score of the player
     *         with the given userId
     *         if it exists, or an exception if the userId does not exist in the
     *         database
     */
    @Override
    public CompletableFuture<Long> getTotalScore(String userId) {
        return null;
    }

    /**
     * Adds a comment to a scannable code.
     *
     * @param scannableCodeId the id of the scannable code to add the comment to
     * @param comment         the comment to add
     * @return a CompletableFuture that will complete successfully ifthe comment was
     *         added successfully, or an exception if the scannable code does not
     *         exist in the database
     */
    @Override
    public CompletableFuture<Void> addComment(String scannableCodeId, Comment comment) {
        return null;
    }

    /**
     * Updates the player preferences of a player.
     *
     * @param userId            the userId of the player to update the preferences
     *                          for
     * @param playerPreferences the updated player preferences
     * @return a CompletableFuture that will complete successfully if the player
     *         preferences were updated successfully,
     *         or an exception if the userId does not exist in the database
     */
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

    /**
     * Updates the contact information of a player.
     *
     * @param userId      the userId of the player to update the contact information
     *                    for
     * @param contactInfo the updated contact information
     * @return a CompletableFuture that will complete successfully if the contact
     *         information was updated successfully,
     *         or an exception if the userId does not exist in the database
     */

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

    /**
     * Adds a scannable code to database
     *
     * @param scannableCode the scannable code to add
     * @return a CompletableFuture that will complete successfully if the scannable
     *         code was added successfully
     */
    @Override
    public CompletableFuture<String> addScannableCode(ScannableCode scannableCode) {
        CompletableFuture<String> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            String scanId = scannableCode.getScannableCodeId();
            scannableCodeHashMap.put(scanId, scannableCode);
            cf.complete(scanId);
        });
        return cf;
    }

    @Override
    public CompletableFuture<Void> addScannableCodeToPlayerWallet(String userId, String scannableCodeId) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                Player p = players.get(userId);
                p.getPlayerWallet().addScannableCode(scannableCodeId, null);
                cf.complete(null);
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    @Override
    public CompletableFuture<Boolean> scannableCodeExists(String scannableCodeId) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if(scannableCodeHashMap.containsKey(scannableCodeId)){
                cf.complete(true);
            }else{
                cf.complete(false);
            }
        });
        return cf;
    }

    /**
     * Removes a scannable code from a player's wallet.
     *
     * @param userId          the userId of the player to remove the scannable code
     *                        from
     * @param scannableCodeId the id of the scannable code to remove
     * @return a CompletableFuture that will complete successfully if the scannable
     *         code was removed successfully,
     *         or an exception if the userId does not exist in the database
     */
    @Override
    public CompletableFuture<Void> removeScannableCode(String userId, String scannableCodeId) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if (players.containsKey(userId)) {
                if (scannableCodeHashMap.containsKey(scannableCodeId)) {

                    Player p = players.get(userId);
                    String scanId = scannableCodeId;
                    ScannableCode scannableCode = scannableCodeHashMap.get(scannableCodeId);
                    scannableCodeHashMap.remove(scanId);
                    p.getPlayerWallet().deleteScannableCode(scanId, scannableCode.getHashInfo().getGeneratedScore());
                    cf.complete(null);
                }
            } else {
                cf.completeExceptionally(new Exception("UserId does not exist."));
            }
        });
        return cf;
    }

    /**
     * Changes the username of a player.
     *
     * @param userId      the userId of the player to change the username for
     * @param newUsername the new username for the player
     * @return a CompletableFuture that will complete successfully if the username
     *         was changed successfully,
     *         or an exception if the userId does not exist in the database
     */
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

    /**
     * Get the player's highest scoring QR code
     * @param scannableCodeIds the scannableIds to find the highest scoring scannableId
     * @return cf the CompletableFuture with the highest scoring code
     */
    @Override
    public CompletableFuture<ScannableCode> getPlayerWalletTopScore(ArrayList<String> scannableCodeIds){
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            ScannableCode highestScoring = null;

            if(scannableCodeHashMap.size()>0){
                for(ScannableCode scannableCode : scannableCodeHashMap.values()){
                    if(highestScoring == null ||
                            scannableCode.getHashInfo().getGeneratedScore() > highestScoring
                                    .getHashInfo().getGeneratedScore()){
                        highestScoring = scannableCode;
                    }
                }

                cf.complete(highestScoring);
            }
        });
        return cf;
    }

    /**
     * Get the player's lowest scoring QR code
     * @param scannableCodeIds the scannableIds to find the lowest scoring scannableId
     * @return cf the CompletableFuture with the lowest scoring code
     */
    @Override
    public CompletableFuture<ScannableCode> getPlayerWalletLowScore(ArrayList<String> scannableCodeIds){
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            ScannableCode lowestScoring = null;

            if(scannableCodeHashMap.size()>0){
                for(ScannableCode scannableCode : scannableCodeHashMap.values()){
                    if(lowestScoring == null ||
                            scannableCode.getHashInfo().getGeneratedScore() < lowestScoring
                                    .getHashInfo().getGeneratedScore()){
                        lowestScoring = scannableCode;
                    }
                }

                cf.complete(lowestScoring);
            }
        });
        return cf;
    }

    /**
     * Gets the total score of all scannableCodeIds in a list
     * @param scannableCodeIds the ids of codes to sum
     * @return cf the CompletableFuture that contains the total score
     */
    @Override
    public CompletableFuture<Long> getPlayerWalletTotalScore(ArrayList<String> scannableCodeIds){
        CompletableFuture<Long> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            long runningScore = 0;

            if(scannableCodeHashMap.size()>0){
                for(ScannableCode scannableCode : scannableCodeHashMap.values()){
                    runningScore+=scannableCode.getHashInfo().getGeneratedScore();
                }

                cf.complete(runningScore);
            }
        });
        return cf;
    }

    /**
     * Gets a scannable code from the database with a specific id
     *
     * @param scannableCodeId          the id of the scannable code to get
     * @return cf the CompleteableFuture with the found scannableCode
     */
    @Override
    public CompletableFuture<ScannableCode> getScannableCodeById(String scannableCodeId){
        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            if(scannableCodeHashMap.containsKey(scannableCodeId)){
                cf.complete((scannableCodeHashMap.get(scannableCodeId)));
            }else{
                cf.completeExceptionally(new Exception("Could not find ScannableCode"));
            }
        });
        return cf;
    }
}
