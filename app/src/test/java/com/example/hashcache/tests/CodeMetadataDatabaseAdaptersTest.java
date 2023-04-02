package com.example.hashcache.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.database.DatabaseAdapters.CodeMetadataDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class CodeMetadataDatabaseAdaptersTest {
    FireStoreHelper mockFireStoreHelper;
    FirebaseFirestore mockDb;

    @BeforeEach
    private void initializeMocks(){
        mockDb = Mockito.mock(FirebaseFirestore.class);
        mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
    }

    private CodeMetadataDatabaseAdapter getCodeMetaDatabaseAdapter(){
        return CodeMetadataDatabaseAdapter.makeOrGetInstance(mockFireStoreHelper, mockDb);
    }

    @Test
    void removeScannableCodeMetadataTest(){
        Query mockQuery = Mockito.mock(Query.class);
        String testUsername = "Jerry Seinfield";
        String testScannableCodeId = "B";
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        Task<QuerySnapshot> mockTaskQS = Mockito.mock(Task.class);
        QuerySnapshot mockQS = Mockito.mock(QuerySnapshot.class);
        DocumentSnapshot mockDS = Mockito.mock(DocumentSnapshot.class);
        List<DocumentSnapshot> testList = new ArrayList<>();
        testList.add(mockDS);
        DocumentReference mockDocRef = Mockito.mock(DocumentReference.class);
        Task<Void> mockTaskVoid = Mockito.mock(Task.class);

        when(mockDb.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.whereEqualTo(FieldNames.SCANNABLE_CODE_ID.fieldName, testScannableCodeId))
                .thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTaskQS);
        when(mockQuery.whereEqualTo(FieldNames.USER_ID.fieldName, testUsername))
                .thenReturn(mockQuery);
        when(mockTaskQS.isSuccessful()).thenReturn(true);
        when(mockTaskQS.getResult()).thenReturn(mockQS);
        when(mockQS.size()).thenReturn(1);
        when(mockQS.getDocuments()).thenReturn(testList);
        when(mockDS.getReference()).thenReturn(mockDocRef);
        when(mockDocRef.delete()).thenReturn(mockTaskVoid);

        doAnswer(invocation -> {
            OnCompleteListener onCompleteListener = invocation.getArgumentAt(0, OnCompleteListener.class);
            onCompleteListener.onComplete(mockTaskQS);
            return null;
        }).when(mockTaskQS).addOnCompleteListener(any(OnCompleteListener.class));

        doAnswer(invocation -> {
            OnSuccessListener onSuccessListener = invocation.getArgumentAt(0, OnSuccessListener.class);
            onSuccessListener.onSuccess(null);
            return mockTaskVoid;
        }).when(mockTaskVoid).addOnSuccessListener(any(OnSuccessListener.class));

        boolean result = getCodeMetaDatabaseAdapter().removeScannableCodeMetadata(testScannableCodeId,
                testUsername).join();

        assert(result);
    }
}
