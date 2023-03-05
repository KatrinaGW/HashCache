package com.example.hashcache.database_connections;

import com.example.hashcache.models.ScannableCode;

public interface GetScannableCodeCallback {
    void onCallback(ScannableCode scannableCode);
}
