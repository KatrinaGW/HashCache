package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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
        ScannableCode scannableCode = new ScannableCode("123", new HashInfo(null, "name", 321));

        playerWallet.addScannableCode(scannableCode.getScannableCodeId());

        assertNull(playerWallet.getScannableCodeLocationImage(scannableCode.getScannableCodeId()));
    }

    @Test
    void GetScannedCodeIds(){
        PlayerWallet playerWallet= new PlayerWallet();
        ScannableCode firstMockScannableCode = new ScannableCode("123", new HashInfo(null, "name1", 321), String.valueOf(1234));
        ScannableCode secondMockScannableCode = new ScannableCode("124", new HashInfo(null, "name2", 321), String.valueOf(1234));

        playerWallet.addScannableCode(firstMockScannableCode.getScannableCodeId());
        playerWallet.addScannableCode(secondMockScannableCode.getScannableCodeId());

        ArrayList<String> expectedUUIDs = new ArrayList<>();
        expectedUUIDs.add(firstMockScannableCode.getScannableCodeId());
        expectedUUIDs.add(secondMockScannableCode.getScannableCodeId());

        assertTrue(expectedUUIDs.containsAll(playerWallet.getScannedCodeIds()) &&
                playerWallet.getScannedCodeIds().containsAll(expectedUUIDs));

    }
}
