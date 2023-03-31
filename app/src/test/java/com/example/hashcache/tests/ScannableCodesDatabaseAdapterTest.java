package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
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
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);

        when(mockCollection.document(id)).thenReturn(mockDocument);
        when(mockDocument.get()).thenReturn(mockTask);
        when(mockDb.collection(CollectionNames.SCANNABLE_CODES.collectionName)).thenReturn(mockCollection);
        when(mockScannableCodeDocumentConverter.getScannableCodeFromDocument(mockDocument))
                .thenReturn(testCF);

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesDatabaseAdapter();

        ScannableCode result = scannableCodesDatabaseAdapter.getScannableCode(id).join();

        assertEquals(result, mockScananbleCode);
    }

    @Test
    void makeInstanceTest(){
        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter =
                ScannableCodesDatabaseAdapter.makeInstance(mockScannableCodeDocumentConverter,
                        mockFireStoreHelper, mockDb);

        assertNotNull(scannableCodesDatabaseAdapter);
        assertEquals(scannableCodesDatabaseAdapter.getClass(), ScannableCodesDatabaseAdapter.class);
    }

    @Test
    void getInstanceTest(){
        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesDatabaseAdapter();
        assertEquals(scannableCodesDatabaseAdapter, ScannableCodesDatabaseAdapter.getInstance());
    }

    @Test
    void scannableCodeIdExistsTest(){
        String testId = "Schrodigners ID";
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);

        when(mockDb.collection(CollectionNames.SCANNABLE_CODES.collectionName))
                .thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(testId)).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(true);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesDatabaseAdapter();
        boolean result = scannableCodesDatabaseAdapter.scannableCodeIdExists(testId).join();

        assertTrue(result);
    }

    @Test
    void resetInstanceTest(){
        getMockScannableCodesDatabaseAdapter();
        assertNotNull(ScannableCodesDatabaseAdapter.getInstance());
        ScannableCodesDatabaseAdapter.resetInstance();
        assertThrows(IllegalArgumentException.class, () -> {
            ScannableCodesDatabaseAdapter.getInstance();
        });
    }

    @Test
    void getScannableCodesByIdInListTest(){
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        QuerySnapshot mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
        Task<QuerySnapshot> mockQueryTask = Mockito.mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot1 = Mockito.mock(DocumentSnapshot.class);
        DocumentSnapshot mockDocumentSnapshort2 = Mockito.mock(DocumentSnapshot.class);
        String scannableCodeId1 = "One too many";
        String scannableCodeId2 = "Two too many";
        ArrayList<String> testData = new ArrayList<>();
        testData.add(scannableCodeId1);
        testData.add(scannableCodeId2);
        List fakeDocuments = new ArrayList<>();
        fakeDocuments.add(mockDocumentSnapshort2);
        fakeDocuments.add(mockDocumentSnapshot1);
        ScannableCode mockScannableCode1 = Mockito.mock(ScannableCode.class);
        ScannableCode mockScannableCode2 = Mockito.mock(ScannableCode.class);
        CompletableFuture<ScannableCode> firstCF = new CompletableFuture<>();
        CompletableFuture<ScannableCode> secondCF = new CompletableFuture<>();
        firstCF.complete(mockScannableCode1);
        secondCF.complete(mockScannableCode2);
        DocumentReference mockDocumentReference1 = Mockito.mock(DocumentReference.class);
        DocumentReference mockDocumentReference2 = Mockito.mock(DocumentReference.class);

        when(mockDb.collection(CollectionNames.SCANNABLE_CODES.collectionName))
                .thenReturn(mockCollectionReference);
        when(mockCollectionReference.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.getDocuments()).thenReturn(fakeDocuments);
        when(mockDocumentSnapshot1.getId()).thenReturn(scannableCodeId1);
        when(mockDocumentSnapshort2.getId()).thenReturn(scannableCodeId2);
        when(mockDocumentSnapshot1.getReference()).thenReturn(mockDocumentReference1);
        when(mockDocumentSnapshort2.getReference()).thenReturn(mockDocumentReference2);
        when(mockQueryTask.isSuccessful()).thenReturn(true);

        when(mockScannableCodeDocumentConverter.getScannableCodeFromDocument(mockDocumentReference1))
                .thenReturn(firstCF);
        when(mockScannableCodeDocumentConverter.getScannableCodeFromDocument(mockDocumentReference2))
                .thenReturn(secondCF);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockQueryTask);
            return null;
        }).when(mockQueryTask).addOnCompleteListener(any(OnCompleteListener.class));

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesDatabaseAdapter();
        ArrayList<ScannableCode> result = scannableCodesDatabaseAdapter.getScannableCodesByIdInList(testData)
                .join();

        assertTrue(result.size() == 2);
        assertTrue(result.contains(mockScannableCode1));
        assertTrue(result.contains(mockScannableCode2));
    }

    @Test
    void addScannableCodeTest(){
        CompletableFuture<Boolean> boolCF = new CompletableFuture<>();
        boolCF.complete(false);
        ScannableCode testScannableCode = new ScannableCode("first", new HashInfo(null, "name", 4289));
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        CompletableFuture<String> strCF = new CompletableFuture<>();
        strCF.complete(testScannableCode.getScannableCodeId());

        when(mockFireStoreHelper.documentWithIDExists(mockCollectionReference,
                testScannableCode.getScannableCodeId())).thenReturn(boolCF);
        when(mockDb.collection(CollectionNames.SCANNABLE_CODES.collectionName))
                .thenReturn(mockCollectionReference);
        when(mockScannableCodeDocumentConverter.addScannableCodeToCollection(testScannableCode,
                mockCollectionReference, mockFireStoreHelper))
                .thenReturn(strCF);

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesDatabaseAdapter();
        String result = scannableCodesDatabaseAdapter.addScannableCode(testScannableCode).join();

        assertEquals(result, testScannableCode.getScannableCodeId());
    }

    @Test
    void addCommentTest(){
        String fakeId = "fake!";
        Comment testComment = new Comment("This is definitely not a body", "I am definitely " +
                "not a person");
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        CompletableFuture<Boolean> boolCF = new CompletableFuture<>();
        boolCF.complete(true);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);

        when(mockFireStoreHelper.documentWithIDExists(mockCollectionReference, fakeId))
                .thenReturn(boolCF);
        when(mockDb.collection(CollectionNames.SCANNABLE_CODES.collectionName))
                .thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(fakeId)).thenReturn(mockDocumentReference);

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesDatabaseAdapter();
        boolean result = scannableCodesDatabaseAdapter.addComment(fakeId, testComment).join();

        assertTrue(result);
        verify(mockScannableCodeDocumentConverter, times(1))
                .addCommentToScannableCodeDocument(testComment, mockDocumentReference);

    }

    @Test
    void deleteCommentTest(){
        String fakeId = "fake!";
        Comment testComment = new Comment("This is definitely not a body", "I am definitely " +
                "not a person");
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        CompletableFuture<Boolean> boolCF = new CompletableFuture<>();
        boolCF.complete(true);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
    }


}

