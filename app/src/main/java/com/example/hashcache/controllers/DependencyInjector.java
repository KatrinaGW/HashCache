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

public class DependencyInjector {

    public static ScannableCodesConnectionHandler makeScannableCodesConnectionHandler(){
        return ScannableCodesConnectionHandler.makeInstance(new ScannableCodeDocumentConverter(),
                new FireStoreHelper(), FirebaseFirestore.getInstance());
    }

    public static PlayersConnectionHandler makePlayersConnectionHandler(HashMap<String,
            String> inAppUsernamesIds){
        return PlayersConnectionHandler.makeInstance(inAppUsernamesIds, new PlayerDocumentConverter(),
                new FireStoreHelper(), FirebaseFirestore.getInstance(), new PlayerWalletConnectionHandler());
    }

    public static CodeLocationConnectionHandler makeCodeLocationConnectionHandler(){
        return CodeLocationConnectionHandler.makeInstance(new FireStoreHelper(), new CodeLocationDocumentConverter(),
                FirebaseFirestore.getInstance());
    }
}
