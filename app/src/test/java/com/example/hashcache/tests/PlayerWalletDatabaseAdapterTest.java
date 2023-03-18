package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.PlayerWalletDatabaseAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PlayerWalletDatabaseAdapterTest {
    private FireStoreHelper mockFireStoreHelper;

    private PlayerWalletDatabaseAdapter getMockPlayerWalletConnectionHandler(){
        return new PlayerWalletDatabaseAdapter(mockFireStoreHelper);
    }

    @BeforeEach
    void initialize(){
        this.mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
    }

    @Test
    void addScannableCodeDocumentTest(){
//        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
//        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
//        String mockId = "0";
//        HashMap<String, String> scannableCodeIdData = new HashMap<>();
//        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, mockId);
//        BooleanCallback mockBooleanCallback = new BooleanCallback() {
//            @Override
//            public void onCallback(Boolean isTrue) {
//                assertTrue(isTrue);
//                verify(mockFireStoreHelper, times(1)).documentWithIDExists(any(CollectionReference.class), anyString(),
//                        any(BooleanCallback.class));
//                verify(mockFireStoreHelper, times(1)).setDocumentReference(any(DocumentReference.class),
//                        any(), any(BooleanCallback.class));
//            }
//        };
//
//        when(mockCollection.document(anyString())).thenReturn(mockDocument);
//
//        doAnswer(invocation -> {
//            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
//            booleanCallback.onCallback(false);
//            return null;
//        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
//                any(BooleanCallback.class));
//
//        doAnswer(invocation -> {
//            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
//            booleanCallback.onCallback(true);
//            return null;
//        }).when(mockFireStoreHelper).setDocumentReference(mockDocument, scannableCodeIdData,
//                mockBooleanCallback);
//
//        PlayerWalletConnectionHandler playerWalletConnectionHandler = getMockPlayerWalletConnectionHandler();
//        playerWalletConnectionHandler.addScannableCodeDocument(mockCollection, mockId, null, mockBooleanCallback);
    }

//    @Test
//    void addScannableCodeDocumentThrowsTest(){
//        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
//        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
//        String mockId = "0";
//        HashMap<String, String> scannableCodeIdData = new HashMap<>();
//        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, mockId);
//        BooleanCallback mockBooleanCallback = new BooleanCallback() {
//            @Override
//            public void onCallback(Boolean isTrue) {
//                assertTrue(isTrue);
//                verify(mockFireStoreHelper, times(1)).documentWithIDExists(any(CollectionReference.class), anyString(),
//                        any(BooleanCallback.class));
//                verify(mockFireStoreHelper, times(1)).setDocumentReference(any(DocumentReference.class),
//                        any(), any(BooleanCallback.class));
//            }
//        };
//
//        when(mockCollection.document(anyString())).thenReturn(mockDocument);
//
//        doAnswer(invocation -> {
//            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
//            booleanCallback.onCallback(true);
//            return null;
//        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
//                any(BooleanCallback.class));
//
//        PlayerWalletConnectionHandler playerWalletConnectionHandler = getMockPlayerWalletConnectionHandler();
//        assertThrows(IllegalArgumentException.class, () -> {
//            playerWalletConnectionHandler.addScannableCodeDocument(mockCollection, mockId, null, mockBooleanCallback);
//        });
//    }
//
//    @Test
//    void deleteScannableCodeFromWalletTest(){
//        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
//        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
//        String mockId = "0";
//        HashMap<String, String> scannableCodeIdData = new HashMap<>();
//        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, mockId);
//        Task<Void> mockTask = Mockito.mock(Task.class);
//        BooleanCallback mockBooleanCallback = new BooleanCallback() {
//            @Override
//            public void onCallback(Boolean isTrue) {
//                assertTrue(isTrue);
//                verify(mockFireStoreHelper, times(1)).documentWithIDExists(any(CollectionReference.class), anyString(),
//                        any(BooleanCallback.class));
//                verify(mockDocument, times(1)).delete();
//            }
//        };
//
//        when(mockCollection.document(anyString())).thenReturn(mockDocument);
//        when(mockDocument.delete()).thenReturn(mockTask);
//        when(mockTask.addOnSuccessListener(any(OnSuccessListener.class))).thenReturn(mockTask);
//        when(mockTask.addOnFailureListener(any(OnFailureListener.class))).thenReturn(mockTask);
//
//        doAnswer(invocation -> {
//            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
//            booleanCallback.onCallback(true);
//            return null;
//        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
//                any(BooleanCallback.class));
//
//        PlayerWalletConnectionHandler playerWalletConnectionHandler = getMockPlayerWalletConnectionHandler();
//        playerWalletConnectionHandler.deleteScannableCodeFromWallet(mockCollection, mockId, mockBooleanCallback);
//    }
//
//    @Test
//    void deleteScannableCodeFromWalletFailureTest(){
//        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
//        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
//        String mockId = "0";
//        HashMap<String, String> scannableCodeIdData = new HashMap<>();
//        scannableCodeIdData.put(FieldNames.SCANNABLE_CODE_ID.fieldName, mockId);
//        Task<Void> mockTask = Mockito.mock(Task.class);
//        BooleanCallback mockBooleanCallback = Mockito.mock(BooleanCallback.class);
//
//        doAnswer(invocation -> {
//            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
//            booleanCallback.onCallback(false);
//            return null;
//        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
//                any(BooleanCallback.class));
//
//        PlayerWalletConnectionHandler playerWalletConnectionHandler = getMockPlayerWalletConnectionHandler();
//        assertThrows(IllegalArgumentException.class, () -> {
//            playerWalletConnectionHandler.deleteScannableCodeFromWallet(mockCollection, mockId, mockBooleanCallback);
//        });
//    }
}
