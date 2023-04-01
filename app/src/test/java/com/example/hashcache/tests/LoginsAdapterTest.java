package com.example.hashcache.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.LoginsAdapter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LoginsAdapterTest {
    private FirebaseFirestore mockDB;
    private FireStoreHelper mockFireStoreHelper;
    private CollectionReference mockCollectionReference;

    private LoginsAdapter getLoginsAdapter(){
        LoginsAdapter.resetInstance();
        LoginsAdapter.makeOrGetInstance(mockFireStoreHelper, mockDB);

        return LoginsAdapter.getInstance();
    }

    @BeforeEach
    void resetMocks(){
        mockDB = Mockito.mock(FirebaseFirestore.class);
        mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        mockCollectionReference = Mockito.mock(CollectionReference.class);

        when(mockDB.collection(CollectionNames.LOGINS.collectionName)).thenReturn(mockCollectionReference);
    }

    @Test
    void addLoginRecordTest(){
        String testDeviceId = "12345";
        AppContext.get().setDeviceId(testDeviceId);
        CompletableFuture<Boolean> booleanCF = new CompletableFuture<>();
        booleanCF.complete(true);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);

        when(mockCollectionReference.document(testDeviceId)).thenReturn(mockDocument);
        when(mockFireStoreHelper.setDocumentReference(any(DocumentReference.class), any()))
                .thenReturn(booleanCF);

        LoginsAdapter loginsAdapter = getLoginsAdapter();


        CompletableFuture<Void> result = loginsAdapter.addLoginRecord("FakeName");

        assertNull(result.join());
        verify(mockFireStoreHelper, times(1)).setDocumentReference(
                any(DocumentReference.class), any(HashMap.class)
        );
    }

    @Test
    void getUsernameForDeviceTest(){
        String testDeviceId = "12345";
        String testUsername = "Hal";
        AppContext.get().setDeviceId(testDeviceId);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CompletableFuture<Boolean> boolCF = new CompletableFuture<>();
        boolCF.complete(true);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        Map<String, Object> testData = new HashMap<>();
        testData.put(FieldNames.USERNAME.fieldName, testUsername);

        when(mockCollectionReference.document(testDeviceId)).thenReturn(mockDocumentReference);
        when(mockFireStoreHelper.documentWithIDExists(mockCollectionReference, testDeviceId))
                .thenReturn(boolCF);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.getData()).thenReturn(testData);
        when(mockDocumentSnapshot.exists()).thenReturn(true);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);

            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        LoginsAdapter loginsAdapter = getLoginsAdapter();
        CompletableFuture<String> result = loginsAdapter.getUsernameForDevice();

        assertEquals(result.join(), testUsername);
    }

    @Test
    void deleteLoginTest(){
        String testDeviceId = "12345";
        AppContext.get().setDeviceId(testDeviceId);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CompletableFuture<Boolean> boolCF = new CompletableFuture<>();
        boolCF.complete(true);
        Task<Void> mockTask = Mockito.mock(Task.class);

        when(mockCollectionReference.document(testDeviceId)).thenReturn(mockDocumentReference);
        when(mockDocumentReference.delete()).thenReturn(mockTask);
        when(mockFireStoreHelper.documentWithIDExists(mockCollectionReference, testDeviceId))
                .thenReturn(boolCF);

        doAnswer(invocation -> {
            OnSuccessListener onSuccessListener = invocation.getArgumentAt(0, OnSuccessListener.class);
            onSuccessListener.onSuccess(null);
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));
        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgumentAt(0, OnFailureListener.class);
            onFailureListener.onFailure(null);
            return mockTask;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));

        LoginsAdapter loginsAdapter = getLoginsAdapter();

        CompletableFuture<Void> result = loginsAdapter.deleteLogin();

        assertNull(result.join());
        verify(mockDocumentReference, times(1)).delete();
        verify(mockFireStoreHelper, times(1)).documentWithIDExists(
                mockCollectionReference, testDeviceId
        );

    }
}
