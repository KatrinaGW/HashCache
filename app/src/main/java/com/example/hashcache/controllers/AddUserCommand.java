package com.example.hashcache.controllers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddUserCommand {
    FirebaseFirestore db;

    public AddUserCommand(){
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
    }

    public boolean addUser(String userName){
        boolean success = false;
        HashMap<String, String> data = new HashMap<>();

        if(!(userName == null) && userName != ""){
            //check for duplicate users

        }


        return success;
    }
}
