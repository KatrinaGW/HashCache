package com.example.hashcache.models.database.values;

/**
 * Names of all the collections in the FireStore database
 */
public enum CollectionNames {
    PLAYERS("players"),
    CODE_LOCATIONS("codeLocations"),
    SCANNABLE_CODES("scannableCodes"),
    CONTACT_INFO("contactInfo"),
    PLAYER_PREFERENCES("playerPreferences"),
    PLAYER_WALLET("playerWallet"),
    LOGINS("logins"),
    COMMENTS("comments"),
    CODE_METADATA("codeLocations");
    public final String collectionName;

    private CollectionNames(String collectionName) {
        this.collectionName = collectionName;
    }
}
