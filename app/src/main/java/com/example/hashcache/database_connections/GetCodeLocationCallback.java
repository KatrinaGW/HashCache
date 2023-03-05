package com.example.hashcache.database_connections;

import com.example.hashcache.models.CodeLocation;

public interface GetCodeLocationCallback {
    void onCallback(CodeLocation codeLocation);
}
