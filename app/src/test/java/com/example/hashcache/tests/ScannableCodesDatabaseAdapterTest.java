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

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.ScannableCodesDatabaseAdapter;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.data_exchange.database.DatabaseAdapters.callbacks.GetScannableCodeCallback;
import com.example.hashcache.models.data_exchange.data_adapters.ScannableCodeDataAdapter;
import com.example.hashcache.models.data_exchange.database.values.CollectionNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ScannableCodesDatabaseAdapterTest {
        ScannableCodeDataAdapter mockScannableCodeDocumentConverter;
        FireStoreHelper mockFireStoreHelper;
        FirebaseFirestore mockDb;

        private ScannableCodesDatabaseAdapter getMockScannableCodesConnectionHandler(){
            return ScannableCodesDatabaseAdapter.makeInstance(mockScannableCodeDocumentConverter,
                    mockFireStoreHelper, mockDb);
        }

        @BeforeEach
        void initializeMocks(){
            ScannableCodesDatabaseAdapter.resetInstance();
            this.mockScannableCodeDocumentConverter = Mockito.mock(ScannableCodeDataAdapter.class);
            this.mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
            this.mockDb = Mockito.mock(FirebaseFirestore.class);
        }

        @Test
        void getScannableCodeTest(){
            CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
            DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
            GetScannableCodeCallback mockGetScannableCodeCallback = Mockito.mock(GetScannableCodeCallback.class);


            when(mockDb.collection(anyString())).thenReturn(mockCollection);
            when(mockCollection.document(anyString())).thenReturn(mockDocument);

            doAnswer(invocation -> {
                return null;
            }).when(mockScannableCodeDocumentConverter).getScannableCodeFromDocument(mockDocument,
                    mockGetScannableCodeCallback);

            ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesConnectionHandler();

            scannableCodesDatabaseAdapter.getScannableCode("id", mockGetScannableCodeCallback);
            verify(mockScannableCodeDocumentConverter, times(1))
                    .getScannableCodeFromDocument(mockDocument, mockGetScannableCodeCallback);
        }

        @Test
        void addScanableCodeTest(){
            CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
            DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
            ScannableCode mockScannableCode = new ScannableCode("mockId", new HashInfo(null,
                    "mockName", 1));
            Comment mockComment = new Comment("body", "id");
            mockScannableCode.addComment(mockComment);

            when(mockDb.collection(anyString())).thenReturn(mockCollection);
            when(mockCollection.document(mockScannableCode.getScannableCodeId())).thenReturn(mockDocument);
            when(mockDocument.collection(CollectionNames.COMMENTS.collectionName))
                    .thenReturn(mockCollection);
            when(mockCollection.document(mockComment.getCommentId())).thenReturn(mockDocument);

            doAnswer(invocation -> {
                return null;
            }).when(mockDocument).set(any());
            doAnswer(invocation -> {
                BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
                booleanCallback.onCallback(false);
                return null;
            }).doAnswer(invocation -> {
                BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
                booleanCallback.onCallback(true);
                return null;
            }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                    any(BooleanCallback.class));
            doAnswer(invocation -> {
                BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
                booleanCallback.onCallback(true);
                return null;
            }).when(mockFireStoreHelper).setDocumentReference(any(DocumentReference.class), any(),
                    any(BooleanCallback.class));

            ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesConnectionHandler();

            scannableCodesDatabaseAdapter.addScannableCode(mockScannableCode, new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {
                    assertTrue(isTrue);
                    verify(mockFireStoreHelper, times(2)).documentWithIDExists(
                            any(CollectionReference.class), anyString(),
                            any(BooleanCallback.class)
                    );
                    verify(mockFireStoreHelper, times(1)).setDocumentReference(
                            any(DocumentReference.class), any(),
                            any(BooleanCallback.class)
                    );
                }
            });
        }

    @Test
    void addScanableCodeThrowsTest(){
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        ScannableCode mockScannableCode = new ScannableCode("mockId", new HashInfo(null,
                "mockName", 1));

        when(mockDb.collection(anyString())).thenReturn(mockCollection);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesConnectionHandler();

        assertThrows(IllegalArgumentException.class, () -> {
            scannableCodesDatabaseAdapter.addScannableCode(mockScannableCode, new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {}
            });
        });
    }

    @Test
    void addCommentTest(){
            CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
            DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
            Comment mockComment = new Comment("body", "id");
            String mockId = "codeId";
            BooleanCallback mockBooleanCallback = new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {
                    assertTrue(isTrue);
                    verify(mockDocument, times(1)).set(any());
                    verify(mockFireStoreHelper, times(1))
                            .documentWithIDExists(any(CollectionReference.class), anyString(),
                            any(BooleanCallback.class));
                }
            };

            when(mockDb.collection(anyString())).thenReturn(mockCollection);
            when(mockCollection.document(mockId)).thenReturn(mockDocument);
            when(mockDocument.collection(CollectionNames.COMMENTS.collectionName))
                    .thenReturn(mockCollection);
            when(mockCollection.document(mockComment.getCommentId())).thenReturn(mockDocument);

            doAnswer(invocation -> {
                return null;
            }).when(mockDocument).set(any());
        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesConnectionHandler();
        scannableCodesDatabaseAdapter.addComment(mockId, mockComment, mockBooleanCallback);
    }

    @Test
    void addCommentThrowsTest(){
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        Comment mockComment = new Comment("body", "id");
        String mockId = "codeId";
        BooleanCallback mockBooleanCallback = new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {}
        };

        when(mockDb.collection(anyString())).thenReturn(mockCollection);

        doAnswer(invocation -> {
            return null;
        }).when(mockDocument).set(any());
        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(false);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesConnectionHandler();
        assertThrows(IllegalArgumentException.class, () -> {
            scannableCodesDatabaseAdapter.addComment(mockId, mockComment, mockBooleanCallback);
        });
    }

    @Test
    void deleteCommentTest(){
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        String mockCodeId = "codeId";
        String mockCommentId = "commentId";
        Task<Void> mockTask = Mockito.mock(Task.class);

        when(mockDb.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.document(mockCodeId)).thenReturn(mockDocument);
        when(mockDocument.collection(CollectionNames.COMMENTS.collectionName)).thenReturn(mockCollection);
        when(mockCollection.document(mockCommentId)).thenReturn(mockDocument);
        when(mockDocument.delete()).thenReturn(mockTask);
        doAnswer(invocation -> {
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any(OnSuccessListener.class));
        doAnswer(invocation -> {
            return null;
        }).when(mockTask).addOnFailureListener(any(OnFailureListener.class));


        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesConnectionHandler();

        scannableCodesDatabaseAdapter.deleteComment(mockCodeId, mockCommentId, new BooleanCallback() {
            @Override
            public void onCallback(Boolean isTrue) {
                assertTrue(isTrue);
                verify(mockFireStoreHelper, times(2)).documentWithIDExists(
                        any(CollectionReference.class), anyString(),
                        any(BooleanCallback.class)
                );
            }
        });
    }

    @Test
    void deleteCommentThrowsFirstTest(){
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        String mockCodeId = "codeId";
        String mockCommentId = "commentId";

        when(mockDb.collection(anyString())).thenReturn(mockCollection);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(false);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesConnectionHandler();

        assertThrows(IllegalArgumentException.class, () -> {
            scannableCodesDatabaseAdapter.deleteComment(mockCodeId, mockCommentId, new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {
                }
            });
        });
    }

    @Test
    void deleteCommentThrowsSecondTest(){
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        String mockCodeId = "codeId";
        String mockCommentId = "commentId";

        when(mockDb.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.document(mockCodeId)).thenReturn(mockDocument);
        when(mockDocument.collection(CollectionNames.COMMENTS.collectionName)).thenReturn(mockCollection);

        doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(true);
            return null;
        }).doAnswer(invocation -> {
            BooleanCallback booleanCallback = invocation.getArgumentAt(2, BooleanCallback.class);
            booleanCallback.onCallback(false);
            return null;
        }).when(mockFireStoreHelper).documentWithIDExists(any(CollectionReference.class), anyString(),
                any(BooleanCallback.class));

        ScannableCodesDatabaseAdapter scannableCodesDatabaseAdapter = getMockScannableCodesConnectionHandler();

        assertThrows(IllegalArgumentException.class, () -> {
            scannableCodesDatabaseAdapter.deleteComment(mockCodeId, mockCommentId, new BooleanCallback() {
                @Override
                public void onCallback(Boolean isTrue) {
                }
            });
        });
    }
}
