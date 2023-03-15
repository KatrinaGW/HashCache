package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.ContactInfo;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerPreferences;
import com.example.hashcache.models.database.data_exchange.data_adapters.PlayerDataAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.PlayerWalletConnectionHandler;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetPlayerCallback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayersConnectionHandlerTest {
    private HashMap<String, String> mockInAppNamesIds;
    private PlayerDataAdapter mockPlayerDataAdapter;
    private FireStoreHelper mockFireStoreHelper;
    private FirebaseFirestore mockDB;
    private PlayerWalletConnectionHandler mockPlayerWalletConnectionHandler;
    private CollectionReference mockCollectionReference;

    private PlayersDatabaseAdapter getMockPlayersConnectionHandler(){
        return PlayersDatabaseAdapter.makeInstance(mockInAppNamesIds, mockPlayerDataAdapter,
                mockFireStoreHelper, mockDB, mockPlayerWalletConnectionHandler);
    }

    @BeforeEach
    void resetMocks(){
        PlayersDatabaseAdapter.resetInstance();
        mockInAppNamesIds = new HashMap<>();
        mockPlayerDataAdapter = Mockito.mock(PlayerDataAdapter.class);
        mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        mockDB = Mockito.mock(FirebaseFirestore.class);
        mockPlayerWalletConnectionHandler = Mockito.mock(PlayerWalletConnectionHandler.class);
        mockCollectionReference = Mockito.mock(CollectionReference.class);
    }

    @Test
    void getInstanceThrowsTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            PlayersDatabaseAdapter.getInstance();
        });
    }

    @Test
    void getInstanceSuccessTest(){
        when(mockDB.collection(anyString())).thenReturn(mockCollectionReference);
        PlayersDatabaseAdapter playersConnectionHandler = getMockPlayersConnectionHandler();
        assertEquals(PlayersDatabaseAdapter.getInstance(), playersConnectionHandler);
    }

    @Test
    void getInAppUserNamesTest(){
        when(mockDB.collection(anyString())).thenReturn(mockCollectionReference);
        PlayersDatabaseAdapter playersConnectionHandler = getMockPlayersConnectionHandler();
        mockInAppNamesIds.put("first", "1");
        mockInAppNamesIds.put("second", "2");

        ArrayList<String> expectedList = new ArrayList<>();
        expectedList.add("first");
        expectedList.add("second");

        assertEquals(expectedList, playersConnectionHandler.getInAppPlayerUserNames());
    }

    @Test
    void getPlayerTest(){ //FIXME
        Player mockPlayer = new Player("name");
        GetPlayerCallback mockGetPlayerCallback = Mockito.mock(GetPlayerCallback.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        Task<DocumentSnapshot> mockThing = Mockito.mock(Task.class);

        when(mockDB.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockThing);
        when(mockThing.addOnCompleteListener(any())).thenAnswer(new Answer<Task<DocumentSnapshot>>() {
            @Override
            public Task<DocumentSnapshot> answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        });

        doAnswer(invocation -> {
            GetPlayerCallback getPlayerCallback = invocation.getArgumentAt(0, GetPlayerCallback.class);
            getPlayerCallback.onCallback(mockPlayer);
            return null;
        })
                .when(mockPlayerDataAdapter)
                .getPlayerFromDocument(any(DocumentReference.class), any(GetPlayerCallback.class));

        PlayersDatabaseAdapter playersConnectionHandler = getMockPlayersConnectionHandler();
        playersConnectionHandler.getPlayer("name", mockGetPlayerCallback);

        verify(mockThing, times(1)).addOnCompleteListener(any());
    }

    @Test
    void updatePlayerPreferencesSuccessTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        when(mockDB.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(3, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).when(mockFireStoreHelper).addBooleanFieldToDocument(any(DocumentReference.class), anyString(), anyBoolean(), any(BooleanCallback.class));

        PlayersDatabaseAdapter playersConnectionHandler = getMockPlayersConnectionHandler();

        playersConnectionHandler.updatePlayerPreferences("blah", new PlayerPreferences(), new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                verify(mockFireStoreHelper, times(1)).addBooleanFieldToDocument(any(DocumentReference.class),
                        anyString(), anyBoolean(), any(BooleanCallback.class));
                assertTrue(isTrue);
            }
        });

    }

    @Test
    void updateContactInfoSuccessTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        when(mockDB.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(3, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).when(mockFireStoreHelper).addStringFieldToDocument(any(DocumentReference.class), anyString(), anyString(), any(BooleanCallback.class));

        PlayersDatabaseAdapter playersConnectionHandler = getMockPlayersConnectionHandler();

        playersConnectionHandler.updateContactInfo("blah", new ContactInfo(), new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                verify(mockFireStoreHelper, times(2)).addStringFieldToDocument(any(DocumentReference.class),
                        anyString(), anyString(), any(BooleanCallback.class));
                assertTrue(isTrue);
            }
        });

    }

    @Test
    void updateUsernameSuccessTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        when(mockDB.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        mockInAppNamesIds.put("old", "1");

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(3, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).when(mockFireStoreHelper).addStringFieldToDocument(any(DocumentReference.class), anyString(), anyString(), any(BooleanCallback.class));

        PlayersDatabaseAdapter playersConnectionHandler = getMockPlayersConnectionHandler();

        playersConnectionHandler.updateUserName("old", "new", new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                verify(mockFireStoreHelper, times(1)).addStringFieldToDocument(any(DocumentReference.class),
                        anyString(), anyString(), any(BooleanCallback.class));
                assertTrue(isTrue);
            }
        });

    }

    @Test
    void playerScannedCodeAddedTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        when(mockDB.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.collection(anyString())).thenReturn(mockCollectionReference);

        String mockUserId = "userId";
        String  mockScannableCodeId = "scannableCodeId";
        mockInAppNamesIds.put("name", mockUserId);

        PlayersDatabaseAdapter playersConnectionHandler = getMockPlayersConnectionHandler();

        playersConnectionHandler.playerScannedCodeAdded(mockUserId, mockScannableCodeId, null, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                verify(mockPlayerWalletConnectionHandler, times(1)).addScannableCodeDocument(mockCollectionReference,
                        mockScannableCodeId, null, any(BooleanCallback.class));
            }
        });
    }

    @Test
    void playerScannedCodeDeletedTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        when(mockDB.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.collection(anyString())).thenReturn(mockCollectionReference);

        String mockUserId = "userId";
        String  mockScannableCodeId = "scannableCodeId";
        mockInAppNamesIds.put("name", mockUserId);

        PlayersDatabaseAdapter playersConnectionHandler = getMockPlayersConnectionHandler();

        playersConnectionHandler.playerScannedCodeDeleted(mockUserId, mockScannableCodeId,  new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                verify(mockPlayerWalletConnectionHandler, times(1)).deleteScannableCodeFromWallet(mockCollectionReference,
                        mockScannableCodeId,  any(BooleanCallback.class));
            }
        });
    }
}
