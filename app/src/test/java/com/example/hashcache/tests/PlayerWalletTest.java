package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.media.Image;

import com.example.hashcache.models.HashInfo;
import com.example.hashcache.models.PlayerList;
import com.example.hashcache.models.PlayerWallet;
import com.example.hashcache.models.ScannableCode;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerWalletTest {

    @Test
    void getScannableCodeLocationImageThrows(){
        PlayerWallet playerWallet= new PlayerWallet();

        assertThrows(IllegalArgumentException.class, () -> {
            playerWallet.getScannableCodeLocationImage(UUID.randomUUID().toString());
        });
    }

    @Test
    void getScannableCodeLocationImage(){
        PlayerWallet playerWallet= new PlayerWallet();
        ScannableCode scannableCode = new ScannableCode("123", new HashInfo(null, "name", 321));

        playerWallet.addScannableCode(scannableCode.getScannableCodeId());

        assertNull(playerWallet.getScannableCodeLocationImage(scannableCode.getScannableCodeId()));
    }

    @Test
    void getScannedCodeIds(){
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

    @Test
    void addScannableCodeNoImageTest(){
        String randomId = "randomId";
        PlayerWallet playerWallet = new PlayerWallet();
        playerWallet.addScannableCode(randomId);

        assertEquals(playerWallet.getScannedCodeIds().get(0), randomId);
    }

    @Test
    void addScannableCodeWithImageTest(){
        String randomId = "randomId";
        Image mockImage = Mockito.mock(Image.class);
        PlayerWallet playerWallet = new PlayerWallet();
        playerWallet.addScannableCode(randomId, mockImage);

        assertEquals(playerWallet.getScannedCodeIds().get(0), randomId);
        assertEquals(mockImage, playerWallet.getScannableCodeLocationImage(randomId));
    }

    @Test
    void getTotalScoreTest(){
        PlayerWallet playerWallet = new PlayerWallet();
        long fakeScore = 4918;
        playerWallet.setTotalScore(fakeScore);

        assertEquals(fakeScore, playerWallet.getTotalScore());
    }

    @Test
    void setTotalScoreTest(){
        PlayerWallet playerWallet = new PlayerWallet();
        long fakeScore = 4918;
        playerWallet.setTotalScore(48953);
        playerWallet.setTotalScore(fakeScore);

        assertEquals(fakeScore, playerWallet.getTotalScore());
    }

    @Test
    void getSizeTest(){
        PlayerWallet playerWallet= new PlayerWallet();

        playerWallet.setQRCount(2);

        assertEquals(2, playerWallet.getQrCount());
    }

    @Test
    void deleteScannableCodeTest(){
        PlayerWallet playerWallet= new PlayerWallet();
        ScannableCode firstMockScannableCode = new ScannableCode("123", new HashInfo(null, "name1", 321), String.valueOf(12234));
        ScannableCode secondMockScannableCode = new ScannableCode("124", new HashInfo(null, "name2", 321), String.valueOf(1234));

        playerWallet.addScannableCode(firstMockScannableCode.getScannableCodeId());
        playerWallet.addScannableCode(secondMockScannableCode.getScannableCodeId());

        playerWallet.deleteScannableCode(firstMockScannableCode.getScannableCodeId());

        assertTrue(playerWallet.getScannedCodeIds().get(0) == secondMockScannableCode.getScannableCodeId());
    }
}
