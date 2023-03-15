package com.example.hashcache.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.database_connections.callbacks.GetPlayerCallback;
import com.example.hashcache.models.database.data_adapters.PlayerDataAdapter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
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

public class PlayerDataAdapterTest {

    private PlayerDataAdapter getPlayerDocumentConverter(){
        return new PlayerDataAdapter();
    }

    @Test
    void getPlayerFromDocumentTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        String mockUserId = "123";
        String mockEmail = "fakeEmail";
        String mockPhoneNumber = "fakePhoneNumber";
        String mockUsername = "name";
        String mockGeolocationPreference = "true";
        String mockScannableCodeId = "321";
        Map<String, Object> mockData = new HashMap<>();
        Map<String, Object> mockWalletData = new HashMap<>();
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        Task<QuerySnapshot> mockQueryTask = Mockito.mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        GetPlayerCallback mockGetPlayerCallback = Mockito.mock(GetPlayerCallback.class);

        QuerySnapshot mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
        when(mockQuerySnapshot.size()).thenReturn(0);

        mockData.put(FieldNames.EMAIL.fieldName, mockEmail);
        mockData.put(FieldNames.PHONE_NUMBER.fieldName, mockPhoneNumber);
        mockData.put(FieldNames.RECORD_GEOLOCATION.fieldName, mockGeolocationPreference);
        mockData.put(FieldNames.USERNAME.fieldName, mockUsername);
        mockWalletData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, mockScannableCodeId);

        when(mockDocumentReference.getId()).thenReturn(mockUserId);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.getData()).thenReturn(mockData);
        when(mockDocumentReference.collection(CollectionNames.PLAYER_WALLET.collectionName))
                .thenReturn(mockCollectionReference);
        when(mockCollectionReference.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.isSuccessful()).thenReturn(false);
        when(mockQueryTask.getResult()).thenReturn(mockQuerySnapshot);
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

        PlayerDataAdapter playerDataAdapter = getPlayerDocumentConverter();

        playerDataAdapter.getPlayerFromDocument(mockDocumentReference, mockGetPlayerCallback);

        verify(mockGetPlayerCallback, times(1)).onCallback(any(Player.class));
    }
}
