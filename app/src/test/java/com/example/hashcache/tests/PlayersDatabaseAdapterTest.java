package com.example.hashcache.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.media.Image;
import android.util.Pair;

import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.PlayerWalletDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.converters.PlayerDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class PlayersDatabaseAdapterTest {
    private PlayerDocumentConverter mockPlayerDocumentConverter;
    private FireStoreHelper mockFireStoreHelper;
    private FirebaseFirestore mockDB;
    private PlayerWalletDatabaseAdapter mockPlayerWalletDatabaseAdapter;
    private CollectionReference mockCollectionReference;

    private PlayersDatabaseAdapter getPlayersDatabaseAdapter(){
        return PlayersDatabaseAdapter.makeInstance(mockPlayerDocumentConverter,
                mockFireStoreHelper, mockDB, mockPlayerWalletDatabaseAdapter);
    }

    @BeforeEach
    void resetMocks(){
        mockDB = Mockito.mock(FirebaseFirestore.class);
        mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        mockPlayerWalletDatabaseAdapter = Mockito.mock(PlayerWalletDatabaseAdapter.class);
        mockPlayerDocumentConverter = Mockito.mock(PlayerDocumentConverter.class);
        mockCollectionReference = Mockito.mock(CollectionReference.class);
        when(mockDB.collection(CollectionNames.PLAYERS.collectionName)).thenReturn(mockCollectionReference);
        PlayersDatabaseAdapter.resetInstance();
    }

    @Test
    void getPlayerTest(){
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

    @Test
    void getInstanceFailureTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            PlayersDatabaseAdapter.getInstance();
        });
    }

    @Test
    void getInstaceSuccessTest(){
        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();
        assertEquals(playersDatabaseAdapter, PlayersDatabaseAdapter.getInstance());
    }

    @Test
    void makeInstanceTest(){
        when(mockDB.collection(anyString())).thenReturn(mockCollectionReference);
        PlayersDatabaseAdapter playersDatabaseAdapter = PlayersDatabaseAdapter.makeInstance(
                mockPlayerDocumentConverter, mockFireStoreHelper, mockDB, mockPlayerWalletDatabaseAdapter
        );

        assertEquals(playersDatabaseAdapter, PlayersDatabaseAdapter.getInstance());
    }

    @Test
    void resetInstanceTest(){
        getPlayersDatabaseAdapter();
        PlayersDatabaseAdapter.resetInstance();
        assertThrows(IllegalArgumentException.class, () -> PlayersDatabaseAdapter.getInstance());
    }

    @Test
    void usernameExistsTest(){
        String testUsername = "NotHal";
        Query mockQuery = Mockito.mock(Query.class);
        Task<QuerySnapshot> mockTask = Mockito.mock(Task.class);
        QuerySnapshot mockSnapshot = Mockito.mock(QuerySnapshot.class);

        when(mockCollectionReference.whereEqualTo(FieldNames.USERNAME.fieldName, testUsername))
                .thenReturn(mockQuery);
        when(mockQuery.limit(1)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);
        when(mockSnapshot.isEmpty()).thenReturn(false);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();

        CompletableFuture<Boolean> result = playersDatabaseAdapter.usernameExists(testUsername);

        assertTrue(result.join());
    }

    @Test
    void getPlayerIdByUsernameFailureTest(){
        String testUsername = "NotHal";
        Query mockQuery = Mockito.mock(Query.class);
        Task<QuerySnapshot> mockTask = Mockito.mock(Task.class);
        QuerySnapshot mockSnapshot = Mockito.mock(QuerySnapshot.class);

        when(mockCollectionReference.whereEqualTo(FieldNames.USERNAME.fieldName, testUsername))
                .thenReturn(mockQuery);
        when(mockQuery.limit(1)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);
        when(mockSnapshot.isEmpty()).thenReturn(true);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();

        assertThrows(Exception.class, () -> {
            playersDatabaseAdapter.getPlayerIdByUsername(testUsername).join();
        });
    }

    @Test
    void getPlayersFailureTest(){
        Task<QuerySnapshot> mockTask = Mockito.mock(Task.class);

        when(mockDB.collection(CollectionNames.PLAYERS.collectionName)).thenReturn(mockCollectionReference);
        when(mockCollectionReference.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();

        assertThrows(Exception.class, () -> {
            playersDatabaseAdapter.getPlayers().join();
        });
    }

    @Test
    void updatePlayerPreferencesTest(){
        String testId = "Hello???";
        PlayerPreferences testPreferences = new PlayerPreferences();
        CompletableFuture<Boolean> mockCF = Mockito.mock(CompletableFuture.class);
        DocumentReference mockDoc = Mockito.mock(DocumentReference.class);

        when(mockCollectionReference.document(testId)).thenReturn(mockDoc);
        when(mockFireStoreHelper.addBooleanFieldToDocument(mockDoc,
                FieldNames.RECORD_GEOLOCATION.fieldName, testPreferences.getRecordGeolocationPreference()))
                .thenReturn(mockCF);

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();

        CompletableFuture<Boolean> result = playersDatabaseAdapter.updatePlayerPreferences(testId,
                testPreferences);

        assertEquals(mockCF, result);
        verify(mockFireStoreHelper, times(1)).addBooleanFieldToDocument(
                mockDoc, FieldNames.RECORD_GEOLOCATION.fieldName, testPreferences.getRecordGeolocationPreference()
        );
    }

    @Test
    void updateContactInfoTest(){
        String testId = "Hello???";
        ContactInfo testContactInfo = new ContactInfo();
        CompletableFuture<Boolean> testCF = new CompletableFuture<>();
        testCF.complete(true);
        DocumentReference mockDoc = Mockito.mock(DocumentReference.class);

        when(mockCollectionReference.document(testId)).thenReturn(mockDoc);
        when(mockFireStoreHelper.addStringFieldToDocument(mockDoc,
                FieldNames.EMAIL.fieldName, testContactInfo.getEmail()))
                .thenReturn(testCF);
        when(mockFireStoreHelper.addStringFieldToDocument(mockDoc,
                FieldNames.PHONE_NUMBER.fieldName, testContactInfo.getPhoneNumber()))
                .thenReturn(testCF);

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();

        CompletableFuture<Boolean> result = playersDatabaseAdapter.updateContactInfo(testId,
                testContactInfo);

        assertTrue(result.join());
        verify(mockFireStoreHelper, times(1)).addStringFieldToDocument(
                mockDoc, FieldNames.PHONE_NUMBER.fieldName, testContactInfo.getPhoneNumber()
        );
        verify(mockFireStoreHelper, times(1)).addStringFieldToDocument(
                mockDoc, FieldNames.EMAIL.fieldName, testContactInfo.getEmail()
        );
    }

    @Test
    void playerScannedCodeAddedTest(){
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        CollectionReference mockWalletCollection = Mockito.mock(CollectionReference.class);
        String testUserId = "12345666666";
        String testScannableCodeId = "no";
        Image locationImage = null;
        CompletableFuture<Void> mockCF = Mockito.mock(CompletableFuture.class);

        when(mockCollectionReference.document(testUserId)).thenReturn(mockDocument);
        when(mockDocument.collection(CollectionNames.PLAYER_WALLET.collectionName))
                .thenReturn(mockWalletCollection);
        when(mockPlayerWalletDatabaseAdapter.addScannableCodeDocument(mockWalletCollection,
                testScannableCodeId, locationImage))
                .thenReturn(mockCF);

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();
        assertEquals(mockCF, playersDatabaseAdapter.playerScannedCodeAdded(testUserId,
                testScannableCodeId, locationImage));
        verify(mockPlayerWalletDatabaseAdapter, times(1)).addScannableCodeDocument(
                mockWalletCollection, testScannableCodeId, locationImage
        );
    }

    @Test
    void playerScannedCodeDeletedTest(){
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        CollectionReference mockWalletCollection = Mockito.mock(CollectionReference.class);
        String testUserId = "12345666666";
        String testScannableCodeId = "no";
        CompletableFuture<Boolean> mockCF = Mockito.mock(CompletableFuture.class);

        when(mockCollectionReference.document(testUserId)).thenReturn(mockDocument);
        when(mockDocument.collection(CollectionNames.PLAYER_WALLET.collectionName))
                .thenReturn(mockWalletCollection);
        when(mockPlayerWalletDatabaseAdapter.deleteScannableCodeFromWallet(mockWalletCollection,
                testScannableCodeId))
                .thenReturn(mockCF);

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();
        assertEquals(mockCF, playersDatabaseAdapter.playerScannedCodeDeleted(testUserId,
                testScannableCodeId));
        verify(mockPlayerWalletDatabaseAdapter, times(1)).deleteScannableCodeFromWallet(
                mockWalletCollection, testScannableCodeId
        );
    }

    @Test
    void getUsernameByIdTest(){
        String testUserId = "this id is used";
        CompletableFuture<Boolean> boolCF = new CompletableFuture<>();
        boolCF.complete(true);
        DocumentReference mockDoc = Mockito.mock(DocumentReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);

        when(mockCollectionReference.document(testUserId)).thenReturn(mockDoc);
        when(mockDoc.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockFireStoreHelper.documentWithIDExists(mockCollectionReference, testUserId))
                .thenReturn(boolCF);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();

        CompletableFuture<Pair<String, String>> result = playersDatabaseAdapter.getUsernameById(
                testUserId
        );

        assertThrows(Exception.class, () -> {
            result.join();
        });
    }

    @Test
    void getUsernamesByIdsTest(){
        ArrayList<String> testIds = new ArrayList<>();
        String testUserId = "I have no IDea what I'm doing";
        testIds.add(testUserId);

        CompletableFuture<Boolean> boolCF = new CompletableFuture<>();
        boolCF.complete(true);
        DocumentReference mockDoc = Mockito.mock(DocumentReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);

        when(mockCollectionReference.document(testUserId)).thenReturn(mockDoc);
        when(mockDoc.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockFireStoreHelper.documentWithIDExists(mockCollectionReference, testUserId))
                .thenReturn(boolCF);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();

        CompletableFuture<ArrayList<Pair<String, String>>> result = playersDatabaseAdapter.getUsernamesByIds(
                testIds
        );

        assertThrows(Exception.class, () -> {
            result.join();
        });

    }

    @Test
    void getNumPlayersWithScannableCodeTest(){
        String testSCannableCodeId = "332589567";
        Task<QuerySnapshot> mockTask = Mockito.mock(Task.class);
        QuerySnapshot mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        List<DocumentSnapshot> fakeResults = new ArrayList<>();
        fakeResults.add(mockDocumentSnapshot);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CompletableFuture<Boolean> boolCF = new CompletableFuture<>();
        boolCF.complete(true);
        CollectionReference mockWalletCollection = Mockito.mock(CollectionReference.class);

        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockCollectionReference.get()).thenReturn(mockTask);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.getDocuments()).thenReturn(fakeResults);
        when(mockDocumentSnapshot.getReference()).thenReturn(mockDocumentReference);
        when(mockDocumentReference.collection(CollectionNames.PLAYER_WALLET.collectionName))
                .thenReturn(mockWalletCollection);
        when(mockFireStoreHelper.documentWithIDExists(mockWalletCollection, testSCannableCodeId))
                .thenReturn(boolCF);
        when(mockQuerySnapshot.size()).thenReturn(1);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        PlayersDatabaseAdapter playersDatabaseAdapter = getPlayersDatabaseAdapter();
        CompletableFuture<Integer> result = playersDatabaseAdapter.getNumPlayersWithScannableCode(
                testSCannableCodeId
        );

        int thing = result.join();

        assertTrue(result.join()==1);

    }
}
