package com.example.hashcache.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.hashcache.models.database.DatabaseAdapters.CodeLocationDatabaseAdapter;
import com.example.hashcache.models.database.DatabaseAdapters.FireStoreHelper;
import com.example.hashcache.models.database.DatabaseAdapters.converters.CodeLocationDocumentConverter;
import com.example.hashcache.models.database.values.CollectionNames;
import com.example.hashcache.views.AppHome;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MapTest {

    AppHome appHome;
    GoogleMap mockMap;

    @BeforeEach
    void initializeMocks(){
        appHome = Mockito.mock(AppHome.class);
        mockMap = Mockito.mock(GoogleMap.class);
        when(appHome.getMap()).thenReturn(mockMap);
    }


    @Test
    public void testMapNotNull() {
        assertNotNull(appHome.getMap());
    }

    @Test
    public void testMapZoom() {
        //when(mockMap.animateCamera(Mockito.any(CameraUpdateFactory.newLatLngZoom(Mockito.any(LatLng.class), Mockito.anyFloat())))).thenReturn(null);
        mockMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-50,
                        -50), 13));
        Mockito.verify(mockMap).animateCamera(Mockito.any(CameraUpdate.class));
    }

}
