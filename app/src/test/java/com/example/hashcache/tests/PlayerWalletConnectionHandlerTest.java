package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.database.database_connections.FireStoreHelper;
import com.example.hashcache.models.database.database_connections.PlayerWalletConnectionHandler;
import com.example.hashcache.models.database.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database.values.FieldNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

public class PlayerWalletConnectionHandlerTest {
    private FireStoreHelper mockFireStoreHelper;

    private PlayerWalletConnectionHandler getMockPlayerWalletConnectionHandler(){
        return new PlayerWalletConnectionHandler(mockFireStoreHelper);
    }

    @BeforeEach
    void initialize(){
        this.mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
    }

    @Test
    void addScannableCodeDocumentTest(){
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        String mockId = "0";
        HashMap<String, String> scannableCodeIdData = new HashMap<>();
        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, mockId);
        BooleanCallback mockBooleanCallback = new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                assertTrue(isTrue);
                verify(mockFireStoreHelper, times(1)).documentWithIDExists(any(CollectionReference.class), anyString(),
                        any(BooleanCallback.class));
                verify(mockFireStoreHelper, times(1)).setDocumentReference(any(DocumentReference.class),
                        any(), any(BooleanCallback.class));
            }
        };

        when(mockCollection.document(anyString())).thenReturn(mockDocument);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(false);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).when(mockFireStoreHelper).setDocumentReference(mockDocument, scannableCodeIdData,
                mockBooleanCallback);

        PlayerWalletConnectionHandler playerWalletConnectionHandler = getMockPlayerWalletConnectionHandler();
        playerWalletConnectionHandler.addScannableCodeDocument(mockCollection, mockId, null, mockBooleanCallback);
    }

    @Test
    void addScannableCodeDocumentThrowsTest(){
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        String mockId = "0";
        HashMap<String, String> scannableCodeIdData = new HashMap<>();
        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, mockId);
        BooleanCallback mockBooleanCallback = new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                assertTrue(isTrue);
                verify(mockFireStoreHelper, times(1)).documentWithIDExists(any(CollectionReference.class), anyString(),
                        any(BooleanCallback.class));
                verify(mockFireStoreHelper, times(1)).setDocumentReference(any(DocumentReference.class),
                        any(), any(BooleanCallback.class));
            }
        };

        when(mockCollection.document(anyString())).thenReturn(mockDocument);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        PlayerWalletConnectionHandler playerWalletConnectionHandler = getMockPlayerWalletConnectionHandler();
        assertThrows(IllegalArgumentException.class, () -> {
            playerWalletConnectionHandler.addScannableCodeDocument(mockCollection, mockId, null, mockBooleanCallback);
        });
    }

    @Test
    void deleteScannableCodeFromWalletTest(){
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        String mockId = "0";
        HashMap<String, String> scannableCodeIdData = new HashMap<>();
        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, mockId);
        Task<Void> mockTask = Mockito.mock(Task.class);
        BooleanCallback mockBooleanCallback = new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                assertTrue(isTrue);
                verify(mockFireStoreHelper, times(1)).documentWithIDExists(any(CollectionReference.class), anyString(),
                        any(BooleanCallback.class));
                verify(mockDocument, times(1)).delete();
            }
        };

        when(mockCollection.document(anyString())).thenReturn(mockDocument);
        when(mockDocument.delete()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any(OnSuccessListener.class))).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any(OnFailureListener.class))).thenReturn(mockTask);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        PlayerWalletConnectionHandler playerWalletConnectionHandler = getMockPlayerWalletConnectionHandler();
        playerWalletConnectionHandler.deleteScannableCodeFromWallet(mockCollection, mockId, mockBooleanCallback);
    }

    @Test
    void deleteScannableCodeFromWalletFailureTest(){
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        String mockId = "0";
        HashMap<String, String> scannableCodeIdData = new HashMap<>();
        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, mockId);
        Task<Void> mockTask = Mockito.mock(Task.class);
        BooleanCallback mockBooleanCallback = Mockito.mock(BooleanCallback.class);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(false);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        PlayerWalletConnectionHandler playerWalletConnectionHandler = getMockPlayerWalletConnectionHandler();
        assertThrows(IllegalArgumentException.class, () -> {
            playerWalletConnectionHandler.deleteScannableCodeFromWallet(mockCollection, mockId, mockBooleanCallback);
        });
    }
}
