package com.example.hashcache.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.CodeLocation;
import com.example.hashcache.models.data_exchange.data_adapters.CodeLocationDataAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.CodeLocationDatabaseAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class CodeLocationDataAdapterTest {
    private CodeLocationDatabaseAdapter mockCodeLocationDatabaseAdapter;

    private CodeLocationDataAdapter getCodeLocationDataAdapter(){
        return new CodeLocationDataAdapter();
    }

    @BeforeEach
    void initializeMocking(){
        mockCodeLocationDatabaseAdapter = Mockito.mock(CodeLocationDatabaseAdapter.class);
    }

    @Test
    void addCodeLocationTest(){
        CompletableFuture<Boolean> mockCF = new CompletableFuture<>();
        CodeLocation testCodeLocation = new CodeLocation("name", 1, 2, 3);

        when(mockCodeLocationDatabaseAdapter.addCodeLocation(testCodeLocation)).thenReturn(mockCF);

        CodeLocationDataAdapter codeLocationDataAdapter = getCodeLocationDataAdapter();
        codeLocationDataAdapter.addCodeLocation(testCodeLocation, mockCodeLocationDatabaseAdapter);

        verify(mockCodeLocationDatabaseAdapter, times(1)).addCodeLocation(testCodeLocation);
    }


}
