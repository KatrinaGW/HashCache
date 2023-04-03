/*
    https://github.com/yuriy-budiyev/code-scanner

 */
package com.example.hashcache.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import android.Manifest;
import com.example.hashcache.R;
import com.example.hashcache.appContext.AppContext;
import com.example.hashcache.controllers.LocationController;
import com.example.hashcache.controllers.hashInfo.HashController;
import com.example.hashcache.controllers.hashInfo.HashExceptions;
import com.example.hashcache.controllers.hashInfo.HashInfoGenerator;
import com.example.hashcache.models.CodeMetadata;
import com.example.hashcache.models.database.Database;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.Result;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * 
 * QRScanActivity
 * An activity that uses the camera to scan QR codes. Displays a camera view and
 * scans any QR codes that are detected.
 * When a QR code is successfully scanned, a toast is displayed with the text
 * that was encoded in the code.
 */
public class QRScanActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Called when an error occurs in the scanning process
     * @param message the error message
     * @param e the error itself
     */
    public void handleError(String message, Throwable e){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(QRScanActivity.this,
                        message,
                        Toast.LENGTH_SHORT).show();
                Log.d("QRScanActivity", e.getMessage());
                e.printStackTrace();
                Intent intent = new Intent(QRScanActivity.this, AppHome.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Called whenever the activity start
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scan_activity);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            /**
             * Called when a QR code is successfully decoded.
             *
             * Displays a toast with the text that was encoded in the QR code.
             *
             * @param result the result of decoding the QR code
             */
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QRScanActivity.this, "Caching...", Toast.LENGTH_SHORT).show();
                            }
                        });

                        String qrContent = result.getText();
                        try {
                            Pair<String, byte[]> hashData = HashInfoGenerator.getHashFromQRContents(qrContent);
                            String scannableCodeId = hashData.first;
                            String userId = AppContext.get().getCurrentPlayer().getUserId();
                            Database.getInstance().codeMetadataEntryExists(userId, scannableCodeId)
                                    .thenAccept(exists -> {
                                        if (!exists) {
                                            HashController
                                                    .initCodeMetadata(scannableCodeId, userId,
                                                            fusedLocationProviderClient)
                                                    .thenCompose(codeMetadata -> Database.getInstance()
                                                            .addScannableCodeMetadata(codeMetadata))
                                                    .thenCompose(ex -> HashController.addScannableCode(qrContent)
                                                            .thenAccept(noReturn -> {
                                                                Log.d("QRScanActivity",
                                                                        "Successfully added Code Metadata!");
                                                                System.out.println("Have added QR code!!");
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(QRScanActivity.this,
                                                                                "Added QR code!",
                                                                                Toast.LENGTH_SHORT)
                                                                                .show();
                                                                    }
                                                                });
                                                                Intent intent = new Intent(QRScanActivity.this,
                                                                        NewMonsterActivity.class);
                                                                startActivity(intent);

                                                            }).exceptionally(new Function<Throwable, Void>() {
                                                                @Override
                                                                public Void apply(Throwable throwable) {
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            handleError("ERROR: There was an error adding the QR code", throwable);
                                                                        }
                                                                    });

                                                                    return null;
                                                                }
                                                            }));
                                        } else {
                                            handleError("ERROR: You have already scanned this QR code!", new Throwable("QR Code already scanned"));
                                        }

                                    }).exceptionally(new Function<Throwable, Void>() {
                                        @Override
                                        public Void apply(Throwable throwable) {
                                            handleError("ERROR: There was an error adding the QR code", throwable);
                                            return null;
                                        }
                                    });

                        } catch (NoSuchAlgorithmException e) {
                            handleError("ERROR: There was an error adding the QR code - SHA 256 ALgorithm not found", e);
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the camera view is clicked.
             *
             * Starts the scanner preview.
             *
             * @param view the view that was clicked (the camera view)
             */
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        // https://stackoverflow.com/questions/38552144/how-get-permission-for-camera-in-android-specifically-marshmallow
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, 10);
        }
    }

    /**
     * Called when the activity is resumed.
     *
     * Starts the scanner preview.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    /**
     * Called when the activity is paused.
     *
     * Releases the resources used by the scanner.
     */
    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
