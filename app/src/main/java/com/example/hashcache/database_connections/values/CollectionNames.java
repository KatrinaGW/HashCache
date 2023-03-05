package com.example.hashcache.database_connections.values;

public enum CollectionNames {
    PLAYERS("players"),
    CODE_LOCATIONS("codeLocations"),
    SCANNABLE_CODES("scannableCodes"),
    CONTACT_INFO("contactInfo"),
    PLAYER_PREFERENCES("playerPreferences"),
    PLAYER_WALLET("playerWallet"),
    COMMENTS("comments");
    public final String collectionName;

    private CollectionNames(String collectionName) {
        this.collectionName = collectionName;
    }
}
