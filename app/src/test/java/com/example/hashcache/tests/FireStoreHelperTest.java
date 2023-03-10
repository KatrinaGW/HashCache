package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.database_connections.FireStoreHelper;
import com.example.hashcache.models.database_connections.callbacks.BooleanCallback;
import com.google.android.gms.tasks.OnCanceledListener;
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

        doAnswer(invocation -> {
            return mockTask;
        }).when(mockDocumentReference).update(mockKey, mockValue);
        doAnswer(invocation -> {
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));
        doAnswer(invocation -> {
            return null;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.addBooleanFieldToDocument(mockDocumentReference, mockKey, mockValue,
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        assertTrue(isTrue);
                    }
                });
    }

    @Test
    void addStringFieldToDocumentTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        String mockKey = "key";
        String mockValue = "StringValue";
        Task<Void> mockTask = Mockito.mock(Task.class);

        doAnswer(invocation -> {
            return mockTask;
        }).when(mockDocumentReference).update(mockKey, mockValue);
        doAnswer(invocation -> {
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));
        doAnswer(invocation -> {
            return null;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.addStringFieldToDocument(mockDocumentReference, mockKey, mockValue,
                new BooleanCallback() {
                    @Override
                    public void onCallback(Boolean isTrue) {
                        assertTrue(isTrue);
                        verify(mockDocumentReference, times(1)).update(
                                mockKey, mockValue
                        );
                    }
                });
    }

    @Test
    void setDocumentReferenceTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        HashMap<String, String> mockData = new HashMap<>();
        mockData.put("A", "B");
        Task<Void> mockTask = Mockito.mock(Task.class);

        doAnswer(invocation -> {
            return mockTask;
        }).when(mockDocumentReference).set(mockData);
        doAnswer(invocation -> {
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));
        doAnswer(invocation -> {
            return null;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.setDocumentReference(mockDocumentReference, mockData, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                assertTrue(isTrue);
                verify(mockDocumentReference, times(1)).set(mockData);
            }
        });

    }

    @Test
    void documentWithIDExistsTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        String mockId = "1";
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);

        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockCollectionReference.document(mockId)).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        doAnswer(invocation -> {
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.documentWithIDExists(mockCollectionReference, mockId, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                assertTrue(isTrue);
                verify(mockDocumentSnapshot, times(1)).exists();
            }
        });
    }

    @Test
    void documentWithIDExistsFalseTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        String mockId = "1";
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);

        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(false);
        when(mockCollectionReference.document(mockId)).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        doAnswer(invocation -> {
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        FireStoreHelper fireStoreHelper = getFireStoreHelper();
        fireStoreHelper.documentWithIDExists(mockCollectionReference, mockId, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                assertTrue(!isTrue);
                verify(mockDocumentSnapshot, times(1)).exists();
            }
        });
    }
}

