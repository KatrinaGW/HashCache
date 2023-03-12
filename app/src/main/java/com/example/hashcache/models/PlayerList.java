package com.example.hashcache.models;

import android.util.Log;

import com.example.hashcache.controllers.DependencyInjector;
import com.example.hashcache.models.database_connections.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.database_connections.callbacks.GetStringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Represents a container for all the players in game right now
 */
public class PlayerList {
    private static PlayerList INSTANCE;
    private ArrayList<String> playerUserNames;
    private HashMap<String, String> playerIdsNamesMapping;
    private PlayersConnectionHandler playersConnectionHandler;
    /**
     * Private constructor for creating a new instance of PlayerList
     */
    private PlayerList(){
        playerUserNames = new ArrayList<>();
        playerIdsNamesMapping = new HashMap<>();
        playersConnectionHandler = DependencyInjector
                .makePlayersConnectionHandler(playerIdsNamesMapping);
    }
    /**
     * Private constructor for creating a new instance of PlayerList with a given PlayersConnectionHandler
     *
     * @param playersConnectionHandler The PlayersConnectionHandler used to interact with the database
     */
    private PlayerList(PlayersConnectionHandler playersConnectionHandler){
        this.playersConnectionHandler = playersConnectionHandler;
        playerUserNames = playersConnectionHandler.getInAppPlayerUserNames();
    }
    /**
     * Gets the singleton instance of the PlayerList
     *
     * @return INSTANCE The singleton instance of the PlayerList
     */
    public static PlayerList getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PlayerList();
        }

        return INSTANCE;
    }
    /**
     * Gets the singleton instance of the PlayerList with a given PlayersConnectionHandler
     *
     * @param playersConnectionHandler The PlayersConnectionHandler used to interact with the database
     *
     * @return INSTANCE The singleton instance of the PlayerList
     */
    public static PlayerList getInstance(PlayersConnectionHandler playersConnectionHandler) {
        if(INSTANCE == null) {
            INSTANCE = new PlayerList(playersConnectionHandler);
        }

        return INSTANCE;
    }
    /**
     * Resets the singleton instance of the PlayerList
     */
    public static void resetInstance(){
        INSTANCE = null;
    }

    /**
     * Gets the usernames of all players
     * @return playerUserNames the usernames of all players
     */
    public ArrayList<String> getPlayerUserNames(){
        ArrayList<String> usernames = new ArrayList<>();
        playersConnectionHandler.getPlayers()
                .thenAccept(map -> {
                    Object[] list = map.keySet().toArray();
                    for(int i = 0; i < list.length; i++) {
                        usernames.add(list[i].toString());
                    }

                }
        );
        return usernames;
    }

    /*
     *
     *
     * Adds a player to the database
     * @param username the username of the player to add
     * @return success indicates if the user was successfully added or not
     */
    public boolean addPlayer(String username, GetStringCallback getStringCallback){
        boolean success = true;

        if(!this.playerUserNames.contains(username)){
            try{
                this.playersConnectionHandler.createPlayer(username, getStringCallback);
            }catch (IllegalArgumentException e){
                success = false;
            }
        }else{
            throw new IllegalArgumentException("Given username already exists!");
        }

        return success;
    }

    /**
     * Gets a player with a given username
     * @param username the username of the player to find
     * @param getPlayerCallback callback function to get username after asynchronous firestore call
     * @return player The player with the given username
     */
    public void getPlayer(String username, GetPlayerCallback getPlayerCallback){
        this.playersConnectionHandler.getPlayer(username, getPlayerCallback);
    }

    /**
     * Returns a list of the game-wide players, sorted in a specified manner
     * @param filter used to tell the sort function how to sort the array
     * @return players The playerlist sorted in the specified manner
     */
    public ArrayList<String> getPlayersSortedBy(Comparator filter){
        Collections.sort(this.playerUserNames, filter);
        return this.playerUserNames;
    }

    /**
     * Will return a sorted list of players of those who in some since match the search term.
     * Will sort by how closely the players match the term.
     * Will
     * @param searchTerm string enter in to the search box by the user
     * @param k number of names to return
     * @return list of user who closely match the search term
     */
    public ArrayList<String> searchPlayers(String searchTerm, int k) {
        if(INSTANCE == null) {
            throw new IllegalArgumentException("The playerList singleton object has yet to be created");
        }
        Integer distance;
        ArrayList<Username> foundPlayers = new ArrayList<>();
        ArrayList<String> userNames = PlayerList.getInstance().getPlayerUserNames();

        for(String name: userNames) {
            distance = getInstance().computeLevenshteinDistance(name, searchTerm);
            if(distance != -1) {
                foundPlayers.add(new Username(name, distance));
            }
        }

        // Sorts the collection by distance
        Collections.sort(foundPlayers, new Comparator<Username>() {
            @Override
            public int compare(Username o1, Username o2) {
                return o1.distance.compareTo(o2.distance);
            }
        });
        ArrayList<String> searchedPlayers = new ArrayList<>();
        for (int i = 0; i < k && i < foundPlayers.size(); i++) {
            searchedPlayers.add(foundPlayers.get(i).name);
        }
        return searchedPlayers;
    }

    /**
     * Gets the first n players in the playerlist sorted by a specified manner
     * @param filter The way to sort the players
     * @param n The top number of players to get
     * @return this.players sublist The top n players sorted in a specified manner
     */
    public ArrayList<String> getFirstNPlayersSortedBy(Comparator filter, int n){
        return (ArrayList<String>) (this.getPlayersSortedBy(filter)).subList(0, n);
    }

    /**
     * Compares string a with string b. Getting the edit distance between the two strings.
     * The smaller the number the closer the strings match. Will return -1 if the string don't
     * have one letter that matches
     * CITE: https://en.wikipedia.org/wiki/Levenshtein_distance use to learn about levenshtein
     * @param a String one
     * @param b String two
     * @return Edit distance between the two distance
     */
    public Integer computeLevenshteinDistance(String a, String b) {
        int count = 0;

        // Make sure at least one character is the same
        for(int i = 0; i < a.length(); i++) {
            for (int j = 0; j < b.length(); j++) {
                if(a.charAt(i) == b.charAt(j)) {
                    count++;
                }
            }
        }

        // Don't even consider comparing two if zero matching letters
        if(count == 0) {
            return - 1;
        }

        // Create the matrix to be length + 1 in order to account for empty strings
        int[][] dynProMat = new int[a.length() + 1][b.length() + 1];

        // Fill out the rows that deal with the empty string
        for(int i = 0; i < a.length() + 1; i++) {
           dynProMat[i][0] = i;
        }
        for(int i = 0; i < b.length() + 1; i++) {
            dynProMat[0][i] = i;
        }
        int cost;
        int min;
        for(int i = 1; i < a.length() + 1; i++) {
            for(int j = 1; j < b.length() + 1; j++) {
                if(a.charAt(i - 1) == b.charAt(j - 1)) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                // Insert
                min = dynProMat[i - 1][j - 1] + cost;

                // Del
                if(dynProMat[i-1][j] + 1 < min) {
                    min = dynProMat[i - 1][j] + 1;
                }

                // Sub
                if(dynProMat[i][j-1] + 1 < min) {
                    min = dynProMat[i][j-1] + 1;
                }

                dynProMat[i][j] = min;
            }

        }
        return dynProMat[a.length()][b.length()];
    }


}

/*
 * Class to hold userName info for sorting
 */
class Username {
    public String name;
    public Integer distance;
    public Username(String name, Integer distance) {
        this.name = name;
        this.distance = distance;
    }
}

