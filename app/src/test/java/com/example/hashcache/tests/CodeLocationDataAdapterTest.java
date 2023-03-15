package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.database.data_exchange.data_adapters.CodeLocationDataAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.GetCodeLocationCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CodeLocationDataAdapterTest {
    private CodeLocationDataAdapter getCodeLocationDocumentConverter(){
        return new CodeLocationDataAdapter();
    }

    @Test
    void getCodeLocationFromDocumentSuccessfulTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        GetCodeLocationCallback mockGetCodeLocationCallback = Mockito.mock(GetCodeLocationCallback.class);
        Task mockTask2 = Mockito.mock(Task.class);

        when(mockDocumentReference.get()).thenReturn(mockTask);
        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(true);

        CodeLocationDataAdapter codeLocationDataAdapter = getCodeLocationDocumentConverter();
        codeLocationDataAdapter.getCodeLocationFromDocument(mockDocumentReference,
                mockGetCodeLocationCallback);

        verify(mockGetCodeLocationCallback, times(1)).onCallback(
                any(CodeLocation.class)
        );
    }

    @Test
    void getCodeLocationFromDocumentTaskFailureTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        GetCodeLocationCallback mockGetCodeLocationCallback = Mockito.mock(GetCodeLocationCallback.class);

        when(mockDocumentReference.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);
        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        CodeLocationDataAdapter codeLocationDataAdapter = getCodeLocationDocumentConverter();

        codeLocationDataAdapter.getCodeLocationFromDocument(mockDocumentReference, mockGetCodeLocationCallback);
        verify(mockGetCodeLocationCallback, times(1)).onCallback(null);
    }

    @Test
    void getCodeLocationFromDocumentNonExistantFailureTest(){
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
        GetCodeLocationCallback mockGetCodeLocationCallback = Mockito.mock(GetCodeLocationCallback.class);


        when(mockDocumentReference.get()).thenReturn(mockTask);
        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
        when(mockDocumentSnapshot.exists()).thenReturn(false);

        CodeLocationDataAdapter codeLocationDataAdapter = getCodeLocationDocumentConverter();

        assertThrows(IllegalArgumentException.class, () -> {
            codeLocationDataAdapter.getCodeLocationFromDocument(mockDocumentReference, mockGetCodeLocationCallback);
        });
    }


}
