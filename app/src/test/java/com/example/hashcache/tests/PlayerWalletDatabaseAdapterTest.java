package com.example.hashcache.tests;

import static org.junit.Assert.assertNull;
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

import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.PlayerWalletDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabasePort;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PlayerWalletDatabaseAdapterTest {
    private FireStoreHelper mockFireStoreHelper;
    private FirebaseFirestore mockDB;

    private PlayerWalletDatabaseAdapter getMockPlayerDatabaseAdapterWithFirestoreHelper(){
        return new PlayerWalletDatabaseAdapter(mockFireStoreHelper);
    }

    private PlayerWalletDatabaseAdapter getMockPlayerDatabaseAdapterWithDB(){
        return new PlayerWalletDatabaseAdapter(mockDB);
    }

    @BeforeEach
    void initialize(){
        this.mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        this.mockDB = Mockito.mock(FirebaseFirestore.class);
    }

    @Test
    void addScannableCodeDocumentTest(){
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        String testId = "How to get a fake ID 101";
        CompletableFuture<Boolean> fireStoreExistsCF = new CompletableFuture<>();
        fireStoreExistsCF.complete(false);
        CompletableFuture<Boolean> fireStoreSetCF = new CompletableFuture<>();
        fireStoreSetCF.complete(true);

        when(mockFireStoreHelper.documentWithIDExists(mockCollectionReference,
                testId)).thenReturn(fireStoreExistsCF);
        when(mockFireStoreHelper.setDocumentReference(any(DocumentReference.class),
                any(HashMap.class))).thenReturn(fireStoreSetCF);

        PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter = getMockPlayerDatabaseAdapterWithFirestoreHelper();

        /**
         * THE EXPECTED RETURN VALUE IS NULL, THIS IS NOT A TRIVIAL ASSERTION
         */
        assertNull(playerWalletDatabaseAdapter.addScannableCodeDocument(mockCollectionReference,
                testId, null).join());

        verify(mockFireStoreHelper, times(1)).setDocumentReference(
                any(DocumentReference.class), any(HashMap.class)
        );

    }

    @Test
    void scannableCodeExistsOnPlayerWalletTest(){
        String testUserId = "Id please";
        String testScannableCodeId = "Not another ID!";
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);

        when(mockDB.collection(CollectionNames.PLAYERS.collectionName)).thenReturn(mockCollection);
        when(mockCollection.document(testUserId)).thenReturn(mockDocumentReference);
        when(mockDocumentReference.collection(CollectionNames.PLAYER_WALLET.collectionName)).thenReturn(mockCollection);
        when(mockCollection.document(testScannableCodeId)).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockDocumentSnapshot.exists()).thenReturn(true);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter = getMockPlayerDatabaseAdapterWithDB();

        boolean result = playerWalletDatabaseAdapter.scannableCodeExistsOnPlayerWallet(testUserId,
                testScannableCodeId).join();

        assertTrue(result);
    }

    @Test
    void deleteScannableCodeFromWalletTest(){
        CollectionReference mockWalletCollection = Mockito.mock(CollectionReference.class);
        String testId = "Doing the tests was a bad IDea";
        CompletableFuture<Boolean> boolCF = new CompletableFuture<>();
        boolCF.complete(true);
        DocumentReference mockDoc = Mockito.mock(DocumentReference.class);
        Task<Void> mockTask = Mockito.mock(Task.class);

        when(mockFireStoreHelper.documentWithIDExists(mockWalletCollection, testId))
                .thenReturn(boolCF);
        when(mockWalletCollection.document(testId)).thenReturn(mockDoc);
        when(mockDoc.delete()).thenReturn(mockTask);

        doAnswer(invocation -> {
            OnSuccessListener onSuccessListener = invocation.getArgumentAt(0, OnSuccessListener.class);
            onSuccessListener.onSuccess(null);
            return null;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));

        PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter = getMockPlayerDatabaseAdapterWithFirestoreHelper();

        boolean result = playerWalletDatabaseAdapter.deleteScannableCodeFromWallet(mockWalletCollection,
                testId).join();

        assertTrue(result);
    }

    @Test
    void getPlayerWalletTotalScoreTest(){
        String scannableCodeId1 = "Number one reason to not be QA";
        String scannableCodeId2 = "Second reason to not leave tests to the last minute";
        ArrayList<String> fakeData = new ArrayList<>();
        fakeData.add(scannableCodeId1);
        fakeData.add(scannableCodeId2);
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        Task<QuerySnapshot> mockTask = Mockito.mock(Task.class);

        when(mockDB.collection(CollectionNames.SCANNABLE_CODES.collectionName))
                .thenReturn(mockCollection);
        when(mockCollection.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter = getMockPlayerDatabaseAdapterWithDB();
        assertThrows(Exception.class, () ->
                playerWalletDatabaseAdapter.getPlayerWalletTotalScore(fakeData).join());
    }

    @Test
    void getPlayerWalletLowScoreTest(){
        ScannableCode testScananbleCode1 = new ScannableCode("fakeId", new HashInfo(
                null, "fake", 123
        ));
        ScannableCode testScannableCode2 = new ScannableCode("notFake", new HashInfo(
                null, "notFake", 1234
        ));
        ArrayList<String> fakeData = new ArrayList<>();
        fakeData.add(testScananbleCode1.getScannableCodeId());
        fakeData.add(testScannableCode2.getScannableCodeId());
        ArrayList<ScannableCode> fakeResults = new ArrayList<>();
        fakeResults.add(testScananbleCode1);
        fakeResults.add(testScannableCode2);
        CompletableFuture<ArrayList<ScannableCode> >fakeCF = new CompletableFuture<>();
        fakeCF.complete(fakeResults);
        DatabasePort mockDB = Mockito.mock(DatabasePort.class);

        when(mockDB.getScannableCodesByIdInList(fakeData)).thenReturn(fakeCF);

        PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter = getMockPlayerDatabaseAdapterWithDB();
        ScannableCode result = playerWalletDatabaseAdapter.getPlayerWalletLowScore(fakeData,
                mockDB).join();

        assertEquals(testScananbleCode1, result);
    }

    @Test
    void getPlayerWalletTopScoreTest(){
        ScannableCode testScananbleCode1 = new ScannableCode("fakeId", new HashInfo(
                null, "fake", 123
        ));
        ScannableCode testScannableCode2 = new ScannableCode("notFake", new HashInfo(
                null, "notFake", 1234
        ));
        ArrayList<String> fakeData = new ArrayList<>();
        fakeData.add(testScananbleCode1.getScannableCodeId());
        fakeData.add(testScannableCode2.getScannableCodeId());
        ArrayList<ScannableCode> fakeResults = new ArrayList<>();
        fakeResults.add(testScananbleCode1);
        fakeResults.add(testScannableCode2);
        CompletableFuture<ArrayList<ScannableCode> >fakeCF = new CompletableFuture<>();
        fakeCF.complete(fakeResults);
        DatabasePort mockDB = Mockito.mock(DatabasePort.class);

        when(mockDB.getScannableCodesByIdInList(fakeData)).thenReturn(fakeCF);

        PlayerWalletDatabaseAdapter playerWalletDatabaseAdapter = getMockPlayerDatabaseAdapterWithDB();
        ScannableCode result = playerWalletDatabaseAdapter.getPlayerWalletTopScore(fakeData,
                mockDB).join();

        assertEquals(testScannableCode2, result);
    }
}
