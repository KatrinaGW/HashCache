package com.example.hashcache.models.database.DatabaseAdapters.converters;

import androidx.annotation.NonNull;

import com.example.hashcache.models.Comment;
import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.callbacks.BooleanCallback;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.models.database.values.FieldNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ScannableCodeDocumentConverter {

    public static CompletableFuture<String> addScannableCodeToCollection(ScannableCode scannableCode,
                                                                       CollectionReference collectionReference,
                                                                       FireStoreHelper fireStoreHelper){
        CompletableFuture<String> cf = new CompletableFuture<>();
        HashInfo hashInfo = scannableCode.getHashInfo();
        ArrayList<Comment> comments = scannableCode.getComments();

        HashMap<String, String> data = new HashMap<>();
        data.put(FieldNames.SCANNABLE_CODE_ID.fieldName, scannableCode.getScannableCodeId());
        data.put(FieldNames.CODE_LOCATION_ID.fieldName, scannableCode.getCodeLocationId());
        data.put(FieldNames.GENERATED_NAME.fieldName, hashInfo.getGeneratedName());
        data.put(FieldNames.GENERATED_SCORE.fieldName, Long.toString(hashInfo.getGeneratedScore()));

        /**
         * Create a new document with the ScannableCode data and whose id is the
         * scannableCodeId, and put the document into the scannableCodes collection
         */
        fireStoreHelper.setDocumentReference(
                collectionReference.document(scannableCode.getScannableCodeId()), data)
                        .thenAccept(successful -> {
                            if(successful){
                                if(comments.size()>0){
                                    addCommentToScannableCodeDocument(comments.get(0),
                                            collectionReference.document(scannableCode.getScannableCodeId()));
                                }
                                cf.complete(scannableCode.getScannableCodeId());
                            }else{
                                cf.completeExceptionally(new Exception("Something went wrong" +
                                        "while adding a scannable code to the collection"));
                            }
                        })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });
        return cf;
    }

    /**
     * Add a comment to a scananbleCodeDocument
     * @param comment the comment to add to the document
     * @param documentReference the reference to the scannableCodeDocument
     */
    public static void addCommentToScannableCodeDocument(Comment comment,
                                                         DocumentReference documentReference){
        documentReference
                .collection(CollectionNames.COMMENTS.collectionName)
                .document(comment.getCommentId())
                .set(getCommentData(comment));
    }

    /**
     * Creates the data map to put onto a scannableCode document
     *
     * @param comment the comment to convert into fields for a document
     * @return commentData a HashMap which maps the comment values to field names
     * that
     * match the variable names
     */
    private static HashMap<String, String> getCommentData(Comment comment) {
        HashMap<String, String> commentData = new HashMap<>();
        commentData.put(FieldNames.COMMENTATOR_ID.fieldName, comment.getCommentatorId());
        commentData.put(FieldNames.COMMENT_BODY.fieldName, comment.getBody());

        return commentData;
    }

    /**
     * Get a scannableCode from a document reference
     * @param documentReference the document reference to get the scannableCode object from
     * @return cf the CompleteableFuture with the ScannableCode object
     */
    public CompletableFuture<ScannableCode> getScannableCodeFromDocument(DocumentReference documentReference){
        String[] scannableCodeId = new String[1];
        String[] codeLocationId  = new String[1];
        String[] generatedName = new String[1];
        int[] generatedScore = new int[1];

        CompletableFuture<ScannableCode> cf = new CompletableFuture<>();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try{
                            scannableCodeId[0] = document.getId();
                            codeLocationId[0] = (String) document.getData().get(FieldNames.CODE_LOCATION_ID.fieldName);
                            generatedName[0] = (String) document.getData().get(FieldNames.GENERATED_NAME.fieldName);
                            generatedScore[0] = Integer.parseInt((String) document.getData()
                                    .get(FieldNames.GENERATED_SCORE.fieldName));

                            getAllComments(documentReference.collection(CollectionNames.COMMENTS.collectionName))
                                    .thenAccept(comments -> {
                                        cf.complete(new ScannableCode(scannableCodeId[0],
                                                codeLocationId[0], new HashInfo(null, generatedName[0],
                                                generatedScore[0]), comments));
                                    }).exceptionally(new Function<Throwable, Void>() {
                                        @Override
                                        public Void apply(Throwable throwable) {
                                            cf.completeExceptionally(throwable);
                                            return null;
                                        }
                                    });

                            //TODO: Store the image once we figure out how to do it
                        }catch (NullPointerException e){
                            cf.completeExceptionally(new Exception("Scannable Code missing fields!"));
                        }
                    } else {
                        cf.completeExceptionally(new Exception("No such scannable code exists!"));
                    }
                } else {
                    cf.completeExceptionally(task.getException());
                }
            }
        });

        return  cf;
    }

    /**
     * Gets all the comments on a scannable code document
     * @param collectionReference the reference to the comments collection
     * @return cf the CompleteableFuture with an arraylist of comments
     */
    private static CompletableFuture<ArrayList<Comment>> getAllComments(CollectionReference collectionReference){
        ArrayList<Comment> comments = new ArrayList<>();
        CompletableFuture<ArrayList<Comment>> cf = new CompletableFuture<>();

        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size()>0){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    comments.add(new Comment((String) document.getData().get(FieldNames.COMMENT_BODY.fieldName),
                                            (String) document.getData().get(FieldNames.COMMENTATOR_ID.fieldName)));
                                }
                            }

                            cf.complete(comments);
                        } else {
                            cf.completeExceptionally(task.getException());
                        }
                    }
                });
        return cf;
    }
}
