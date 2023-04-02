package com.example.hashcache.tests;

import com.example.hashcache.models.database.DatabaseAdapters.CodeMetadataDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

public class CodeMetadataDatabaseAdaptersTest {
    FireStoreHelper mockFireStoreHelper;
    FirebaseFirestore mockDb;

    @BeforeEach
    private void initializeMocks(){
        mockDb = Mockito.mock(FirebaseFirestore.class);
        mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
    }

    private CodeMetadataDatabaseAdapter getCodeMetaDatabaseAdapter(){
        return CodeMetadataDatabaseAdapter.makeOrGetInstance(mockFireStoreHelper, mockDb);
    }

    @Test
    void removeScannableCodeMetadataTest(){

    }
}
