package com.example.hashcache.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.CodeLocationDatabaseAdapter;
import com.example.hashcache.models.data_exchange.data_adapters.CodeLocationDataAdapter;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetCodeLocationCallback;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.converters.CodeLocationDocumentConverter;
import com.example.hashcache.models.data_exchange.database.values.CollectionNames;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CodeLocationDatabaseAdapterTest {
    FireStoreHelper mockFireStoreHelper;
    FirebaseFirestore mockDb;
    CodeLocationDocumentConverter mockCodeLocationDocumentConverter;
    CollectionReference mockCollectionReference;

    private CodeLocationDatabaseAdapter getMockCodeLocationConnectionHandler(){
        return CodeLocationDatabaseAdapter.makeInstance(mockFireStoreHelper, mockDb, mockCodeLocationDocumentConverter);
    }

    @BeforeEach
    void initializeMocks(){
        CodeLocationDatabaseAdapter.resetInstance();
        mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        mockDb = Mockito.mock(FirebaseFirestore.class);
        mockCodeLocationDocumentConverter = Mockito.mock(CodeLocationDocumentConverter.class);
        mockCollectionReference = Mockito.mock(CollectionReference.class);

        when(mockDb.collection(CollectionNames.CODE_LOCATIONS.collectionName)).thenReturn(mockCollectionReference);
    }

    @Test
    void addCodeLocationTest(){
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CodeLocation mockCodeLocation = new CodeLocation("fakeName", 1, 2, 3);
        CompletableFuture<Boolean> mockCF = new CompletableFuture<>();
        mockCF.complete(true);

        when(mockDb.collection(CollectionNames.CODE_LOCATIONS.collectionName)).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(mockCodeLocation.getId())).thenReturn(mockDocumentReference);
        when(mockCodeLocationDocumentConverter.addCodeLocationToCollection(mockCodeLocation,
                mockCollectionReference, mockFireStoreHelper)).thenReturn(mockCF);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(false);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        CodeLocationDatabaseAdapter codeLocationDatabaseAdapter = getMockCodeLocationConnectionHandler();
        codeLocationDatabaseAdapter.addCodeLocation(mockCodeLocation).thenAccept(success-> {
            assertTrue(success);
        });

        verify(mockFireStoreHelper, times(1)).documentWithIDExists(any(CollectionReference.class),
                anyString(), any(BooleanCallback.class));
        verify(mockCodeLocationDocumentConverter, times(1)).addCodeLocationToCollection(
                mockCodeLocation, mockCollectionReference, mockFireStoreHelper);
    }
}
