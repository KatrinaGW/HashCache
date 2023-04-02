package com.example.hashcache.tests;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import android.location.Location;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.converters.CodeLocationDocumentConverter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CodeLocationDocumentConverterTest {
    private CodeLocationDocumentConverter getCodeLocationDocumentConverter(){
        return new CodeLocationDocumentConverter();
    }

    @Test
    void addCodeLocationToCollectionTest(){
        CodeLocation testCodeLocation = new CodeLocation("CodeLocationName", any(Location.class));
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        FireStoreHelper mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CompletableFuture<Boolean> testCF = new CompletableFuture<>();
        testCF.complete(true);

        when(mockCollectionReference.document(testCodeLocation.getId())).thenReturn(mockDocumentReference);
        when(mockFireStoreHelper.setDocumentReference(any(DocumentReference.class), any(HashMap.class)))
                .thenReturn(testCF);

        CodeLocationDocumentConverter codeLocationDocumentConverter = getCodeLocationDocumentConverter();

        Boolean result = codeLocationDocumentConverter
                .addCodeLocationToCollection(testCodeLocation, mockCollectionReference,
                        mockFireStoreHelper).join();

        assertTrue(result);
    }
}
