package com.example.hashcache.controllers;

import com.example.hashcache.models.database.DatabaseAdapters.CodeLocationDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.PlayerWalletDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.ScannableCodesDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.converters.PlayerDocumentConverter;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.converters.CodeLocationDocumentConverter;
import com.example.hashcache.models.database.DatabaseAdapters.converters.ScannableCodeDocumentConverter;
import com.google.firebase.firestore.FirebaseFirestore;

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
    public static ScannableCodesDatabaseAdapter makeScannableCodesConnectionHandler(){
        return ScannableCodesDatabaseAdapter.makeInstance(new ScannableCodeDocumentConverter(),
                new FireStoreHelper(), FirebaseFirestore.getInstance());
    }

    /**
     * Create or get a current instance of the ScannableCodesConnectionHandlers
     * @return scannableCodesConnectionHandler a static instance of the ScannableCodesConnectionHandler class
     */
    public static ScannableCodesDatabaseAdapter getOrMakeScannableCodesConnectionHandler(){
        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter;
        try{
            scannableCodesDatabaseAdapter = ScannableCodesDatabaseAdapter.getInstance();
        }catch(IllegalArgumentException e){
            scannableCodesDatabaseAdapter = makeScannableCodesConnectionHandler();
        }

        return scannableCodesDatabaseAdapter;
    }

    /**
     * Create and return an instance of the PlayersConnectionHandler with its necessary
     * dependencies
     *
     * @return PlayersConnectionHandler.INSTANCE the static INSTANCE of the PlayersConnectionHandler
     *      class
     */
    public static PlayersDatabaseAdapter makePlayersConnectionHandler(){
        FireStoreHelper fireStoreHelper = new FireStoreHelper();

        return PlayersDatabaseAdapter.makeInstance(new PlayerDocumentConverter(),
                fireStoreHelper, FirebaseFirestore.getInstance(), new PlayerWalletDatabaseAdapter(fireStoreHelper));
    }

    /**
     * Create and return an instance of the CodeLocationConnectionHandler with its necessary
     * dependencies
     * @return CodeLocationConnectionHandler.INSTANCE the static INSTANCE of the CodeLocationConnectionHandler
     * class
     */
    public static CodeLocationDatabaseAdapter makeCodeLocationConnectionHandler(){
        return CodeLocationDatabaseAdapter.makeInstance(new FireStoreHelper(),
                FirebaseFirestore.getInstance(), new CodeLocationDocumentConverter());
    }

}
