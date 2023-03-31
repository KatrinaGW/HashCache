package com.example.hashcache.tests;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.PlayerWalletDatabaseAdapter;
import com.example.hashcache.models.database.values.FieldNames;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PlayerWalletDatabaseAdapterTest {
    private FireStoreHelper mockFireStoreHelper;

    private PlayerWalletDatabaseAdapter getMockPlayerDatabaseAdapter(){
        return new PlayerWalletDatabaseAdapter(mockFireStoreHelper);
    }

    @BeforeEach
    void initialize(){
        this.mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
    }

    @Test
    void addScannableCodeDocumentTest(){
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        String testId = "123";
        CompletableFuture<Boolean> fireStoreExistsCF = new CompletableFuture<>();
        fireStoreExistsCF.complete(false);
        CompletableFuture<Boolean> fireStoreSetCF = new CompletableFuture<>();
        fireStoreSetCF.complete(true);

        when(mockFireStoreHelper.documentWithIDExists(mockCollectionReference,
                testId)).thenReturn(fireStoreExistsCF);
        when(mockFireStoreHelper.setDocumentReference(any(DocumentReference.class),
                any(HashMap.class))).thenReturn(fireStoreSetCF);

        PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter = getMockPlayerDatabaseAdapter();

        /**
         * THE EXPECTED RETURN VALUE IS NULL, THIS IS NOT A TRIVIAL ASSERTION
         */
        assertNull(playerWalletDatabaseAdapter.addScannableCodeDocument(mockCollectionReference,
                testId, null).join());

        verify(mockFireStoreHelper, times(1)).setDocumentReference(
                any(DocumentReference.class), any(HashMap.class)
        );

    }
}
