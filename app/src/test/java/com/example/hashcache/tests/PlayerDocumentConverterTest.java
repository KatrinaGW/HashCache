package com.example.hashcache.tests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.database_connections.converters.PlayerDocumentConverter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PlayerDocumentConverterTest {

    private PlayerDocumentConverter getPlayerDocumentConverter(){
        return new PlayerDocumentConverter();
    }

    @Test
    void getPlayerFromDocumentTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        String mockUserId = "123";
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);

        when(mockDocumentReference.getId()).thenReturn(mockUserId);

    }
}
