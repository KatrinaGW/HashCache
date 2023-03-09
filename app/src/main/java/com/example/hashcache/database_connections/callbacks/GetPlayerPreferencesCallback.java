package com.example.hashcache.database_connections.callbacks;

import com.example.hashcache.models.PlayerPreferences;

public interface GetPlayerPreferencesCallback {
    void onCallback(PlayerPreferences playerPreferences);
}
