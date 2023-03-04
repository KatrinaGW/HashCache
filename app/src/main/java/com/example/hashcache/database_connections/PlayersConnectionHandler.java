package com.example.hashcache.database_connections;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hashcache.models.Player;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles all calls to the Firebase Players database
 */
public class PlayersConnectionHandler {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private ArrayList<String> inAppPlayerUserNames;
    private HashMap<String, Player> cachedPlayers;
    final String TAG = "Sample";

    /**
     * Creates a new instance of the class and initializes the connection to the database
     * @param inAppPlayerUserNames used to keep the app up to date on the current usernames
     *                             in the database
     */
    public PlayersConnectionHandler(ArrayList<String> inAppPlayerUserNames){
        this.inAppPlayerUserNames = inAppPlayerUserNames;
        this.cachedPlayers = new HashMap<>();

        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection("players");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {
                inAppPlayerUserNames.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    Log.d(TAG, String.valueOf(doc.getData().get("username")));
                    String username = doc.getId();
                    inAppPlayerUserNames.add(username);
                }
            }
        });
    }

    /**
     * Gets the in app player usernames
     * @return inAppPlayerUserNames gets the usernames of all players in the app
     */
    public ArrayList<String> getInAppPlayerUserNames(){
        return this.inAppPlayerUserNames;
    }

    /**
     * Gets a Player from the Players database, if the given username belongs to a player
     *
     * @param userName the username to use to pull the player with
     * @return player the player in the database with that username
     * @throws IllegalArgumentException if the given username does not belong to a player
     */
    public Player getPlayer(String userName, GetPlayerCallback getPlayerCallback){
        final Player[] player = new Player[1];

        if(cachedPlayers.keySet().contains(userName)){
            player[0] = cachedPlayers.get(userName);
        }else {
            DocumentReference documentReference = collectionReference.document(userName);

            System.out.println("LOOKY" + documentReference.getId());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Document exists!");

                            player[0] = new Player(document.getId());
                            cachedPlayers.put(userName, player[0]);
                            Log.d(TAG, "FIND DONE");
                            getPlayerCallback.onCallback(player[0]);
                        } else {
                            Log.d(TAG, "Document does not exist!");
                             throw new IllegalArgumentException("Given username does not exist!");
                        }
                    } else {
                        Log.d(TAG, "Failed with: ", task.getException());
                    }
                }
            });
        }

        return player[0];
    }

    /**
     * Adds a player to the database
     *
     * @param username the username of the player to add
     * @throws IllegalArgumentException if the username is empty, too long, or already belongs
     * to a player
     */
    public void addPlayer(String username){
        if(username == null || username.equals("")|| username.length()>=50){
            throw new IllegalArgumentException("Username null, empty, or too long");
        }

        if(inAppPlayerUserNames.contains(username)){
            throw new IllegalArgumentException("Username taken!");
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        collectionReference
                .document(username)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });
    }
}
