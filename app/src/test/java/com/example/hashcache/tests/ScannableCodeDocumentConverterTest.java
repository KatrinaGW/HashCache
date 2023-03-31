package com.example.hashcache.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.converters.ScannableCodeDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ScannableCodeDocumentConverterTest {
    @Test
    void addScannableCodeToCollectionTest(){
        ScannableCode testScannableCode = new ScannableCode("mockCodeLocationId",
                new HashInfo(null, "name", 123), "HelloWorld");
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        FireStoreHelper mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CompletableFuture<Boolean> testCF = new CompletableFuture<>();
        testCF.complete(true);

        when(mockCollectionReference.document(testScannableCode.getScannableCodeId())).thenReturn(mockDocumentReference);
        when(mockFireStoreHelper.setDocumentReference(any(DocumentReference.class), any(HashMap.class)))
                .thenReturn(testCF);

        String result = ScannableCodeDocumentConverter.addScannableCodeToCollection(
                testScannableCode, mockCollectionReference, mockFireStoreHelper
        ).join();

        assertEquals(result, testScannableCode.getScannableCodeId());
    }
}
