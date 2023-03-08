package com.example.hashcache.models;

import java.util.UUID;

/**
 * Represents a user with an id, username, contact info, preferences, and scannable codes
 */
public class Player{
    private String username;
    private ContactInfo contactInfo;
    private PlayerPreferences playerPreferences;
    private PlayerWallet playerWallet;

    /**
     * Create a brand new player
     * @param username the username for the new player
     */
    public Player(String username){
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
    public Player(String username, ContactInfo contactInfo, PlayerPreferences playerPreferences,
                  PlayerWallet playerWallet){
        this.username = username;
        this.contactInfo = contactInfo;
        this.playerPreferences = playerPreferences;
        this.playerWallet = playerWallet;
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
     * Gets the player's current wallet of scannable codes
     * @return playerWallet The player's current wallet of scannable codes
     */
    public PlayerWallet getPlayerWallet(){
        return this.playerWallet;
    }
}