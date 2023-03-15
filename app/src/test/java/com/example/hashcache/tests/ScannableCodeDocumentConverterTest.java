package com.example.hashcache.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.database_connections.callbacks.GetScannableCodeCallback;
import com.example.hashcache.models.database.data_adapters.ScannableCodeDataAdapter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class ScannableCodeDocumentConverterTest {

    private ScannableCodeDataAdapter getScannableCodeDocumentConverter(){
        return new ScannableCodeDataAdapter();
    }

    @Test
    void getScannableCodeFromDocumentTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        GetScannableCodeCallback mockGetScannableCodeCallback = Mockito.mock(GetScannableCodeCallback.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        Task<QuerySnapshot> mockQueryTask = Mockito.mock(Task.class);
        Map<String, Object> mockData = new HashMap<>();

        QuerySnapshot mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
        when(mockQuerySnapshot.size()).thenReturn(0);

        mockData.put("generatedScore", "123");
        mockData.put("codeLocationId", "123");
        mockData.put("generatedName", "codename");

        when(mockDocumentReference.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getData()).thenReturn(mockData);
        when(mockDocumentReference.getId()).thenReturn("222");
        when(mockCollectionReference.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.isSuccessful()).thenReturn(true);
        when(mockQueryTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockDocumentReference.collection(CollectionNames.COMMENTS.collectionName)).thenReturn(mockCollectionReference);
        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));
        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockQueryTask);
            return mockQueryTask;
        }).when(mockQueryTask).addOnCompleteListener(any(OnCompleteListener.class));

        ScannableCodeDataAdapter scannableCodeDocumentConverter = getScannableCodeDocumentConverter();
        scannableCodeDocumentConverter.getScannableCodeFromDocument(mockDocumentReference, mockGetScannableCodeCallback);

        verify(mockGetScannableCodeCallback, times(1)).onCallback(any(ScannableCode.class));
    }
}
