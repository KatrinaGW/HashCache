package com.example.hashcache.models;

import java.util.UUID;

/**
 * Represents a user with an id, username, contact info, preferences, and scannable codes
 */
public class Player{
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

    /**
     * Sets the player's current preferences
     * @param preferences the preferences to use for the user
     */
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