package com.example.hashcache.controllers;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.PlayerList;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddUserCommand {
    FirebaseFirestore db;

    public boolean addUser(String userName, BooleanCallback booleanCallback){
        boolean success = PlayerList.getInstance().addPlayer(userName, booleanCallback);
        boolean success2 = Player.createInstance(userName);
        return success && success2;
    }
}
