package com.example.hashcache.models;

import com.example.hashcache.models.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.database_connections.callbacks.GetPlayerCallback;

import java.util.UUID;

/**
 * Represents a user with an id, username, contact info, preferences, and scannable codes
 */
public class Player{
    private static Player INSTANCE;
    private String username;
    private String userId;
    private ContactInfo contactInfo;
    private PlayerPreferences playerPreferences;
    private PlayerWallet playerWallet;

    /**
     * Create a brand new player
     * @param username the username for the new player
     */
    public Player(String username){
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.contactInfo = new ContactInfo();
        this.playerPreferences = new PlayerPreferences();
        this.playerWallet = new PlayerWallet();
    }


    /**
     * Creates an object for an existing player
     * @param username the username for the player
     * @param contactInfo the contact information for the player
     * @param playerPreferences the preferences for the player
     * @param playerWallet the player's wallet of scannable codes
     */
    public Player(String userId, String username, ContactInfo contactInfo,
                  PlayerPreferences playerPreferences, PlayerWallet playerWallet){
        this.userId = userId;
        this.username = username;
        this.contactInfo = contactInfo;
        this.playerPreferences = playerPreferences;
        this.playerWallet = playerWallet;
    }

    /**
     *
     * @param username: the user name of the user.
     */
    public static boolean createInstance(String username) {
        if(!PlayersConnectionHandler.getInstance().getInAppPlayerUserNames().contains(username)) {
            INSTANCE = new Player(username);
            return true;
        } else {
            // Put code to fetch from the database the user information
            PlayersConnectionHandler.getInstance().getPlayer(username, new GetPlayerCallback() {
                @Override
                public void onCallback(Player player) {
                    INSTANCE = player;
                }
            });
            return true;
        }
    }

    public static Player getInstance() {
        if(INSTANCE == null) {
            throw new IllegalArgumentException("INSTANCE is not defined");
        }

        return INSTANCE;
    }

    /**
     * Gets the userId for the user
     * @return userId the user's id
     */
    public String getUserId(){
        return this.userId;
    }

    /**
     * Update the user's username across the app
     * @param newUserName the new username for the user
     */
    public void updateUserName(String newUserName){
        this.username = newUserName;
    }

    /**
     * Gets the player's username
     * @return username The player's current username
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Gets the player's current contact information
     * @return contactInfo The player's current contact information
     */
    public ContactInfo getContactInfo(){
        return this.contactInfo;
    }

    /**
     * Gets the player's current preferences
     *
     * @return playerPreferences The player's current preferences
     */
    public PlayerPreferences getPlayerPreferences(){
        return this.playerPreferences;
    }

    public void setPlayerPreferences(PlayerPreferences preferences){
        this.playerPreferences = preferences;
    }

    public void setContactInfo(ContactInfo contactInfo){
        this.contactInfo = contactInfo;
    }

    /**
     * Gets the player's current wallet of scannable codes
     * @return playerWallet The player's current wallet of scannable codes
     */
    public PlayerWallet getPlayerWallet(){
        return this.playerWallet;
    }
}