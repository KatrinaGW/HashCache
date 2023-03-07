package com.example.hashcache.controllers;

import com.example.hashcache.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.PlayerList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddUserCommand {
    FirebaseFirestore db;

    public boolean addUser(String userName, BooleanCallback booleanCallback){
        boolean success = PlayerList.getInstance().addPlayer(userName, booleanCallback);

        return success;
    }
}
