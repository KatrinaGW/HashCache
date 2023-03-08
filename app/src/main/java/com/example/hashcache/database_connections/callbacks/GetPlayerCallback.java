package com.example.hashcache.database_connections.callbacks;

//Code from https://stackoverflow.com/a/48500679 add proper reference later

import com.example.hashcache.models.Player;

public interface GetPlayerCallback {
    void onCallback(Player player);
}