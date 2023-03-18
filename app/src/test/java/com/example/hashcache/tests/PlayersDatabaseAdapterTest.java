package com.example.hashcache.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.Player;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.PlayerWalletConnectionHandler;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.converters.PlayerDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class PlayersDatabaseAdapterTest {
    private PlayerDocumentConverter mockPlayerDocumentConverter;
    private FireStoreHelper mockFireStoreHelper;
    private FirebaseFirestore mockDB;
    private PlayerWalletConnectionHandler mockPlayerWalletConnectionHandler;
    private CollectionReference mockCollectionReference;

    private PlayersDatabaseAdapter getPlayersDatabaseAdapter(){
        return PlayersDatabaseAdapter.makeInstance(mockPlayerDocumentConverter,
                mockFireStoreHelper, mockDB, mockPlayerWalletConnectionHandler);
    }

    @BeforeEach
    void resetMocks(){
        mockDB = Mockito.mock(FirebaseFirestore.class);
        mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        mockPlayerWalletConnectionHandler = Mockito.mock(PlayerWalletConnectionHandler.class);
        mockPlayerDocumentConverter = Mockito.mock(PlayerDocumentConverter.class);
        mockCollectionReference = Mockito.mock(CollectionReference.class);
        when(mockDB.collection(CollectionNames.PLAYERS.collectionName)).thenReturn(mockCollectionReference);
        PlayersDatabaseAdapter.resetInstance();
    }

    @Test
    void getPlayer(){
        Player testPlayer = new Player("Hello?");
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        CompletableFuture<Player> testCF = new CompletableFuture<>();
        testCF.complete(testPlayer);

        when(mockCollectionReference.document(testPlayer.getUserId()))
                .thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any());
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockPlayerDocumentConverter.getPlayerFromDocument(mockDocumentReference))
                .thenReturn(testCF);


        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();
        Player result = playersDatabaseAdapter.getPlayer(testPlayer.getUserId()).join();

        assertEquals(result, testPlayer);
    }

    @Test
    void createPlayerTest(){
        String username = "Rumpelstilskin";
        AtomicReference<String> testUserId = new AtomicReference<>();
        CompletableFuture<Boolean> fireStoreCF = new CompletableFuture<>();
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        fireStoreCF.complete(true);

        when(mockCollectionReference.document(anyString())).thenReturn(mockDocument);
        when(mockFireStoreHelper.setDocumentReference(any(DocumentReference.class), any(HashMap.class)))
                .thenReturn(fireStoreCF);

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();

        String result = playersDatabaseAdapter.createPlayer(username).join();
        assertEquals(result.getClass(), String.class);

        verify(mockFireStoreHelper, times(1)).setDocumentReference(
                any(DocumentReference.class), any(HashMap.class));
    }
}
