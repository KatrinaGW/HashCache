package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.ScannableCodesDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.DatabaseAdapters.converters.ScannableCodeDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class ScannableCodesDatabaseAdapterTest {
        ScannableCodeDocumentConverter mockScannableCodeDocumentConverter;
        FireStoreHelper mockFireStoreHelper;
        FirebaseFirestore mockDb;

        private ScannableCodesDatabaseAdapter getMockScannableCodesDatabaseAdapter(){
            return ScannableCodesDatabaseAdapter.makeInstance(mockScannableCodeDocumentConverter,
                    mockFireStoreHelper, mockDb);
        }

        @BeforeEach
        void initializeMocks(){
            ScannableCodesDatabaseAdapter.resetInstance();
            this.mockScannableCodeDocumentConverter = Mockito.mock(ScannableCodeDocumentConverter.class);
            this.mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
            this.mockDb = Mockito.mock(FirebaseFirestore.class);
        }

        @Test
        void getScannableCodeTest() {
            CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
            DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
            String id = "123";
            ScannableCode mockScananbleCode = Mockito.mock(ScannableCode.class);
            CompletableFuture<ScannableCode> testCF = new CompletableFuture<>();
            testCF.complete(mockScananbleCode);

            when(mockCollection.document(id)).thenReturn(mockDocument);
            when(mockDb.collection(CollectionNames.SCANNABLE_CODES.collectionName)).thenReturn(mockCollection);
            when(mockScannableCodeDocumentConverter.getScannableCodeFromDocument(mockDocument))
                    .thenReturn(testCF);

            ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesDatabaseAdapter();

            ScannableCode result = scannableCodesDatabaseAdapter.getScannableCode(id).join();

            assertEquals(result, mockScananbleCode);



        }

    }

