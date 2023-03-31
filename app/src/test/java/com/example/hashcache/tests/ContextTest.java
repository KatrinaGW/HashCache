package com.example.hashcache.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.ResetCommand;
import com.example.hashcache.models.Player;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.ScannableCode;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.PlayersDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.ScannableCodesDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.converters.ScannableCodeDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ContextTest {
    private static FireStoreHelper mockFireStoreHelper;
    private static ScannableCodeDocumentConverter mockScannableCodeDocumentConverter;
    private static FirebaseFirestore mockFirebaseFirestore;

    @BeforeAll
    static void setStaticInstances(){
        mockFirebaseFirestore = Mockito.mock(FirebaseFirestore.class);
        mockFireStoreHelper = Mockito.mock(FireStoreHelper.class);
        mockScannableCodeDocumentConverter = Mockito.mock(ScannableCodeDocumentConverter.class);
        ScannableCodesDatabaseAdapter.makeInstance(mockScannableCodeDocumentConverter,
                mockFireStoreHelper, mockFirebaseFirestore);
    }

    @Test
    void setDeviceIdTest(){
        String deviceId = "02489";
        AppContext.get().setDeviceId("notReal");
        AppContext.get().setDeviceId(deviceId);
        assertEquals(deviceId, AppContext.get().getDeviceId());
    }

    @Test
    void getDeviceId(){
        String deviceId = "02489";
        AppContext.get().setDeviceId(deviceId);
        assertEquals(deviceId, AppContext.get().getDeviceId());
    }

    @Test
    void setNullCurrentScannableCode(){
        ScannableCode mockScananbleCode = null;
        AppContext.get().setCurrentScannableCode(mockScananbleCode);
        assertEquals(mockScananbleCode, AppContext.get().getCurrentScannableCode());
        verify(mockFirebaseFirestore, times(1)).collection(CollectionNames
                .SCANNABLE_CODES.collectionName);
    }

    @Test
    void setCurrentPlayerTest(){
        Player mockPlayer = Mockito.mock(Player.class);
        AppContext.get().setCurrentPlayer(Mockito.mock(Player.class));
        AppContext.get().setCurrentPlayer(mockPlayer);

        assertEquals(AppContext.get().getCurrentPlayer(), mockPlayer);
    }

    @Test
    void getCurrentPlayerTest(){
        Player mockPlayer = Mockito.mock(Player.class);
        AppContext.get().setCurrentPlayer(mockPlayer);

        assertEquals(AppContext.get().getCurrentPlayer(), mockPlayer);
    }

    @Test
    void setSelectedPlayer(){
        Player mockPlayer = Mockito.mock(Player.class);
        AppContext.get().setSelectedPlayer(Mockito.mock(Player.class));
        AppContext.get().setSelectedPlayer(mockPlayer);

        assertEquals(AppContext.get().getSelectedPlayer(), mockPlayer);
    }

    @Test
    void getSelectedPlayerTest(){
        Player mockPlayer = Mockito.mock(Player.class);
        AppContext.get().setSelectedPlayer(mockPlayer);

        assertEquals(AppContext.get().getSelectedPlayer(), mockPlayer);
    }

    @Test
    void resetTest(){
        AppContext.get().setDeviceId("random number");
        Player mockPLayer = Mockito.mock(Player.class);
        AppContext.get().setCurrentPlayer(mockPLayer);

        AppContext.get().resetContext();

        assertNull(AppContext.get().getCurrentPlayer());
        assertNull(AppContext.get().getDeviceId());
        assertNull(AppContext.get().getCurrentScannableCode());
    }

    @Test
    void getTest(){
        AppContext result = AppContext.get();
        assertNotNull(result);
        assertEquals(AppContext.class, result.getClass());
    }
}
