package com.example.hashcache.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.converters.ScannableCodeDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
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

    @Test
    void addCommentToScannableCodeDocumentTest(){
        Comment testComment = new Comment("I must make a comment", "Do you know who I am?", "Of course" +
                "you do");
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);

        when(mockDocumentReference.collection(CollectionNames.COMMENTS.collectionName))
                .thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(testComment.getCommentId()))
                .thenReturn(mockDocumentReference);

        ScannableCodeDocumentConverter.addCommentToScannableCodeDocument(testComment, mockDocumentReference);

        verify(mockDocumentReference, times(1)).set(any(HashMap.class));
    }

    @Test
    void getScannableCodeFromDocumentTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        Task<QuerySnapshot> mockQueryTask = Mockito.mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        QuerySnapshot mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
        String testId = "Id or ID?";
        String testCodeLocationId = "This location is definitely real";
        String testName = "Rumpelstilskin";
        long testScore = 244466666;
        Map<String, Object> testData = new HashMap<>();
        testData.put(FieldNames.CODE_LOCATION_ID.fieldName, testCodeLocationId);
        testData.put(FieldNames.GENERATED_NAME.fieldName, testName);
        testData.put(FieldNames.GENERATED_SCORE.fieldName, testScore);

        when(mockDocumentReference.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentReference.collection(CollectionNames.COMMENTS.collectionName))
                .thenReturn(mockCollectionReference);
        when(mockCollectionReference.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.isSuccessful()).thenReturn(true);
        when(mockQueryTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.size()).thenReturn(0);
        when(mockDocumentSnapshot.getId()).thenReturn(testId);
        when(mockDocumentSnapshot.getData()).thenReturn(testData);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));
        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockQueryTask);
            return null;
        }).when(mockQueryTask).addOnCompleteListener(any(OnCompleteListener.class));

        ScannableCode result = ScannableCodeDocumentConverter
                .getScannableCodeFromDocument(mockDocumentReference)
                .join();

        assertEquals(result.getScannableCodeId(), testId);
        assertEquals(result.getCodeLocationId(), testCodeLocationId);
        assertEquals(result.getHashInfo().getGeneratedScore(), testScore);
        assertEquals(result.getHashInfo().getGeneratedName(), testName);
    }
}
