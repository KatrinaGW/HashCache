package com.example.hashcache.models;

import java.util.ArrayList;

/**
 * Represents a container for all the players in game right now
 */
public class PlayerList {
    private ArrayList<Player> players;

    /**
     * Get all the players in the database right now and store them here
     */
    public void retrievePlayersFromDB(){
        //Retrive the players from the database
    }

    /**
     * Returns a list of the game-wide players, sorted in a specified manner
     * @param filter The way to sort the players
     * @return players The playerlist sorted in the specified manner
     */
    public ArrayList<Player> getPlayersSortedBy(Object filter){
        //return the players sorted by a specific method
        return this.players;
    }

    /**
     * Gets the first n players in the playerlist sorted by a specified manner
     * @param filter The way to sort the players
     * @param n The top number of players to get
     * @return this.players sublist The top n players sorted in a specified manner
     */
    public ArrayList<Player> getFirstNPlayersSortedBy(Object filter, int n){
        return (ArrayList<Player>) (this.getPlayersSortedBy(filter)).subList(0, n);
    }
}
