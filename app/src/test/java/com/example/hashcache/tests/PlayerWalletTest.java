package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.PlayerWallet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerWalletTest {

    @Test
    void GetScannableCodeLocationImageThrows(){
        PlayerWallet playerWallet= new PlayerWallet();

        assertThrows(IllegalArgumentException.class, () -> {
            playerWallet.getScannableCodeLocationImage(UUID.randomUUID().toString());
        });
    }

    @Test
    void GetScannableCodeLocationImage(){
        PlayerWallet playerWallet= new PlayerWallet();
        String uuid = UUID.randomUUID().toString();

        playerWallet.addScannableCode(uuid);

        assertNull(playerWallet.getScannableCodeLocationImage(uuid));
    }

    @Test
    void GetScannedCodeIds(){
        PlayerWallet playerWallet= new PlayerWallet();
        String firstUUID = UUID.randomUUID().toString();
        String secondUUID = UUID.randomUUID().toString();

        playerWallet.addScannableCode(firstUUID);
        playerWallet.addScannableCode(secondUUID);

        ArrayList<String> expectedUUIDs = new ArrayList<>();
        expectedUUIDs.add(firstUUID.toString());
        expectedUUIDs.add(secondUUID.toString());

        assertTrue(expectedUUIDs.containsAll(playerWallet.getScannedCodeIds()) &&
                playerWallet.getScannedCodeIds().containsAll(expectedUUIDs));

    }
}
