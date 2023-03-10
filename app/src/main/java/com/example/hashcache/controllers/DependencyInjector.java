package com.example.hashcache.controllers;

import com.example.hashcache.models.database_connections.CodeLocationConnectionHandler;
import com.example.hashcache.models.database_connections.FireStoreHelper;
import com.example.hashcache.models.database_connections.PlayerWalletConnectionHandler;
import com.example.hashcache.models.database_connections.PlayersConnectionHandler;
import com.example.hashcache.models.database_connections.ScannableCodesConnectionHandler;
import com.example.hashcache.models.database_connections.converters.CodeLocationDocumentConverter;
import com.example.hashcache.models.database_connections.converters.PlayerDocumentConverter;
import com.example.hashcache.models.database_connections.converters.ScannableCodeDocumentConverter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Creates and returns instances of classes that require dependencies
 */
public class DependencyInjector {

    /**
     * Create and return an instance of the ScannableCodesConnectionHandler with its necessary
     * dependencies
     * @return ScannableCodesConnectionHandler.INSTANCE the static INSTANCE of the ScannableCodesConnectionHandler
     * class
     */
    public static ScannableCodesConnectionHandler makeScannableCodesConnectionHandler(){
        return ScannableCodesConnectionHandler.makeInstance(new ScannableCodeDocumentConverter(),
                new FireStoreHelper(), FirebaseFirestore.getInstance());
    }

    /**
     * Create and return an instance of the PlayersConnectionHandler with its necessary
     * dependencies
     * @param inAppUsernamesIds the ids of all users mapped to their usernames
     * @return PlayersConnectionHandler.INSTANCE the static INSTANCE of the PlayersConnectionHandler
     *      class
     */
    public static PlayersConnectionHandler makePlayersConnectionHandler(HashMap<String,
            String> inAppUsernamesIds){
        FireStoreHelper fireStoreHelper = new FireStoreHelper();

        return PlayersConnectionHandler.makeInstance(inAppUsernamesIds, new PlayerDocumentConverter(),
                fireStoreHelper, FirebaseFirestore.getInstance(), new PlayerWalletConnectionHandler(fireStoreHelper));
    }

    /**
     * Create and return an instance of the CodeLocationConnectionHandler with its necessary
     * dependencies
     * @return CodeLocationConnectionHandler.INSTANCE the static INSTANCE of the CodeLocationConnectionHandler
     * class
     */
    public static CodeLocationConnectionHandler makeCodeLocationConnectionHandler(){
        return CodeLocationConnectionHandler.makeInstance(new FireStoreHelper(), new CodeLocationDocumentConverter(),
                FirebaseFirestore.getInstance());
    }
}
