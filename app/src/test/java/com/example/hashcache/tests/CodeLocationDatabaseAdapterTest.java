package com.example.hashcache.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.location.Location;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.database.DatabaseAdapters.CodeLocationDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.DatabaseAdapters.converters.CodeLocationDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.Code;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class CodeLocationDatabaseAdapterTest {
    FireStoreHelper mockFireStoreHelper;
    FirebaseFirestore mockDb;
    CodeLocationDocumentConverter mockCodeLocationDocumentConverter;
    CollectionReference mockCollectionReference;

    private CodeLocationDatabaseAdapter getCodeLocationDatabaseAdapter(){
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
        CompletableFuture<Boolean> fireStoreCF = new CompletableFuture<>();
        fireStoreCF.complete(false);
        CompletableFuture<Boolean> converterCF = new CompletableFuture<>();
        converterCF.complete(true);
        CodeLocation testCodeLocation = new CodeLocation("My Swamp!", Mockito.mock(Location.class));

        when(mockFireStoreHelper.documentWithIDExists(mockCollectionReference, testCodeLocation.getId()))
                .thenReturn(fireStoreCF);
        when(mockCodeLocationDocumentConverter.addCodeLocationToCollection(testCodeLocation,
                mockCollectionReference, mockFireStoreHelper)).thenReturn(converterCF);

        CodeLocationDatabaseAdapter codeLocationDatabaseAdapter = getCodeLocationDatabaseAdapter();
        boolean success = codeLocationDatabaseAdapter.addCodeLocation(testCodeLocation).join();

        assertTrue(success);
    }

    @Test
    void getCodeLocationTest(){
        String mockId = "This is not the ID you are looking for";
        CodeLocation mockCodeLocation = Mockito.mock(CodeLocation.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CompletableFuture<CodeLocation> testCF = new CompletableFuture<>();
        testCF.complete(mockCodeLocation);

        when(mockCollectionReference.document(mockId)).thenReturn(mockDocumentReference);
        when(mockCodeLocationDocumentConverter.convertDocumentReferenceToCodeLocation(mockDocumentReference))
                .thenReturn(testCF);

        CodeLocationDatabaseAdapter codeLocationDatabaseAdapter = getCodeLocationDatabaseAdapter();

        CodeLocation result = codeLocationDatabaseAdapter.getCodeLocation(mockId).join();

        assertEquals(result, mockCodeLocation);
    }
}
