package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.database.data_adapters.CodeLocationDataAdapter;
import com.example.hashcache.models.database.database_connections.CodeLocationConnectionHandler;
import com.example.hashcache.models.database.database_connections.FireStoreHelper;
import com.example.hashcache.models.database.database_connections.callbacks.BooleanCallback;
import com.example.hashcache.models.database.database_connections.callbacks.GetCodeLocationCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

public class CodeLocationConnectionHandlerTest {
    FireStoreHelper mockFireStoreHelper;
    FirebaseFirestore mockDb;
    CodeLocationDataAdapter mockCodeLocationDataAdapter;

    private CodeLocationConnectionHandler getMockCodeLocationConnectionHandler(){
        return CodeLocationConnectionHandler.makeInstance(mockFireStoreHelper, mockCodeLocationDataAdapter,
                mockDb);
    }

    @BeforeEach
    void initializeMocks(){
        CodeLocationConnectionHandler.resetInstance();
        mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        mockDb = Mockito.mock(FirebaseFirestore.class);
        mockCodeLocationDataAdapter = Mockito.mock(CodeLocationDataAdapter.class);
    }

    @Test
    void addCodeLocationTest(){
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CodeLocation mockCodeLocation = new CodeLocation("fakeName", 1, 2, 3);
        HashMap<String, String> mockData = new HashMap<>();

        BooleanCallback mockBooleanCallback = new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                verify(mockFireStoreHelper, times(1)).documentWithIDExists(
                        any(CollectionReference.class), anyString(), any(BooleanCallback.class));
                verify(mockFireStoreHelper, times(1)).setDocumentReference(
                        any(DocumentReference.class), any(), any(BooleanCallback.class)
                );

            }
        };

        when(mockDb.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(false);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        CodeLocationConnectionHandler codeLocationConnectionHandler = getMockCodeLocationConnectionHandler();
        codeLocationConnectionHandler.addCodeLocation(mockCodeLocation, mockBooleanCallback);
    }

    @Test
    void getCodeLocationTest(){
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CodeLocation mockCodeLocation = new CodeLocation("fakeName", 1, 2, 3);

        GetCodeLocationCallback mockGetCodeLocationCallback = new GetCodeLocationCallback() {
            @Override
            public void onCallback(CodeLocation codeLocation) {
                verify(mockCodeLocationDataAdapter, times(1)).getCodeLocationFromDocument(
                        any(DocumentReference.class), any(GetCodeLocationCallback.class)
                );

                assertEquals(mockCodeLocation, codeLocation);
            }
        };

        when(mockDb.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        doAnswer(invocation -> {
            GetCodeLocationCallback getCodeLocationCallback = invocation.getArgumentAt(1, GetCodeLocationCallback.class);
            getCodeLocationCallback.onCallback(mockCodeLocation);
            return null;
        }).when(mockCodeLocationDataAdapter).getCodeLocationFromDocument(any(DocumentReference.class),
                any(GetCodeLocationCallback.class));

        CodeLocationConnectionHandler codeLocationConnectionHandler = getMockCodeLocationConnectionHandler();
        codeLocationConnectionHandler.getCodeLocation(mockCodeLocation.getId(), mockGetCodeLocationCallback);
    }
}
