package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

public class FireStoreHelperTest {
    private FireStoreHelper getFireStoreHelper(){
        return new FireStoreHelper();
    }

    @Test
    void addBooleanFieldToDocumentTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        String mockKey = "key";
        boolean mockValue = true;
        Task<Void> mockTask = Mockito.mock(Task.class);
        BooleanCallback mockBooleanCallback = Mockito.mock(BooleanCallback.class);

        doAnswer(invocation -> {
            return mockTask;
        }).when(mockDocumentReference).update(mockKey, mockValue);
        doAnswer(invocation -> {
            OnSuccessListener onSuccessListener = invocation.getArgumentAt(0, OnSuccessListener.class);
            onSuccessListener.onSuccess(any());
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));
        doAnswer(invocation -> {
            return null;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.addBooleanFieldToDocument(mockDocumentReference, mockKey, mockValue, mockBooleanCallback);

        verify(mockTask, times(1)).addOnSuccessListener(any(OnSuccessListener.class));
        verify(mockTask, times(1)).addOnFailureListener(any(OnFailureListener.class));
        verify(mockBooleanCallback, times(1)).onCallback(true);
    }

    @Test
    void addStringFieldToDocumentTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        String mockKey = "key";
        String mockValue = "StringValue";
        Task<Void> mockTask = Mockito.mock(Task.class);
        BooleanCallback mockBooleanCallback = Mockito.mock(BooleanCallback.class);

        doAnswer(invocation -> {
            return mockTask;
        }).when(mockDocumentReference).update(mockKey, mockValue);
        doAnswer(invocation -> {
            OnSuccessListener onSuccessListener = invocation.getArgumentAt(0, OnSuccessListener.class);
            onSuccessListener.onSuccess(any());
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));
        doAnswer(invocation -> {
            return null;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.addStringFieldToDocument(mockDocumentReference, mockKey, mockValue, mockBooleanCallback);

        verify(mockTask, times(1)).addOnSuccessListener(any(OnSuccessListener.class));
        verify(mockTask, times(1)).addOnFailureListener(any(OnFailureListener.class));
        verify(mockBooleanCallback, times(1)).onCallback(true);
    }

    @Test
    void setDocumentReferenceFailureTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        HashMap<String, String> mockData = new HashMap<>();
        mockData.put("A", "B");
        Task<Void> mockTask = Mockito.mock(Task.class);
        BooleanCallback mockBooleanCallback = Mockito.mock(BooleanCallback.class);

        doAnswer(invocation -> {
            return mockTask;
        }).when(mockDocumentReference).set(mockData);
        doAnswer(invocation -> {
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));
        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgumentAt(0, OnFailureListener.class);
            onFailureListener.onFailure(any(Exception.class));
            return null;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.setDocumentReference(mockDocumentReference, mockData, mockBooleanCallback);

        verify(mockTask, times(1)).addOnSuccessListener(any(OnSuccessListener.class));
        verify(mockTask, times(1)).addOnFailureListener(any(OnFailureListener.class));
        verify(mockBooleanCallback, times(1)).onCallback(false);
    }

    @Test
    void setDocumentReferenceSuccessTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        HashMap<String, String> mockData = new HashMap<>();
        mockData.put("A", "B");
        Task<Void> mockTask = Mockito.mock(Task.class);
        BooleanCallback mockBooleanCallback = Mockito.mock(BooleanCallback.class);

        doAnswer(invocation -> {
            return mockTask;
        }).when(mockDocumentReference).set(mockData);
        doAnswer(invocation -> {
            return mockTask;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));
        doAnswer(invocation -> {
            OnSuccessListener onSuccessListener = invocation.getArgumentAt(0, OnSuccessListener.class);
            onSuccessListener.onSuccess(any());
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.setDocumentReference(mockDocumentReference, mockData, mockBooleanCallback);

        verify(mockTask, times(1)).addOnSuccessListener(any(OnSuccessListener.class));
        verify(mockTask, times(1)).addOnFailureListener(any(OnFailureListener.class));
        verify(mockBooleanCallback, times(1)).onCallback(true);
    }

    @Test
    void documentWithIDExistsTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        String mockId = "1";
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        BooleanCallback mockBooleanCallback = Mockito.mock(BooleanCallback.class);

        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockCollectionReference.document(mockId)).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.documentWithIDExists(mockCollectionReference, mockId, mockBooleanCallback);

        verify(mockBooleanCallback, times(1)).onCallback(true);
    }

    @Test
    void documentWithIDExistsFalseTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        String mockId = "1";
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        BooleanCallback mockBooleanCallback = Mockito.mock(BooleanCallback.class);

        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(false);
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockCollectionReference.document(mockId)).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.documentWithIDExists(mockCollectionReference, mockId, mockBooleanCallback);

        verify(mockBooleanCallback, times(1)).onCallback(false);
    }
}

