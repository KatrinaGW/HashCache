/*
    https://github.com/yuriy-budiyev/code-scanner

 */
package com.example.hashcache.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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
import com.example.hashcache.models.CodeMetadata;
import com.example.hashcache.models.database.Database;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.Result;

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
     * Called when the activity is created.
     *
     * Initializes the activity by setting up the camera view and scanner, and
     * adding a callback function for when a
     * QR code is successfully scanned.
     *
     * @param savedInstanceState saved state of the activity, if it was previously
     *                           closed.
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
                        HashController.addScannableCode(result.getText()).thenCompose(scannableCode -> {
                            Log.d("QRScanActivity", "Trying to add metadata...");
                            String scannableCodeId = AppContext.get().getCurrentScannableCode().getScannableCodeId();
                            String userId = AppContext.get().getCurrentPlayer().getUserId();
                            return HashController.initCodeMetadata(scannableCodeId, userId,
                                    fusedLocationProviderClient);
                        }).thenCompose(codeMetadata -> Database.getInstance().addScannableCodeMetadata(codeMetadata))
                                .thenAccept(val -> {
                                    Log.d("QRScanActivity", "Successfully added Code Metadata!");
                                    System.out.println("Have added QR code!!");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(QRScanActivity.this, "Added QR code!", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                    Intent intent = new Intent(QRScanActivity.this, NewMonsterActivity.class);
                                    startActivity(intent);

                                }).exceptionally(new Function<Throwable, Void>() {
                                    @Override
                                    public Void apply(Throwable throwable) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                if (throwable instanceof HashExceptions.AlreadyHasCodeEx) {
                                                    Toast.makeText(QRScanActivity.this,
                                                            "ERROR: You already have QR code on your wallet!",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(QRScanActivity.this,
                                                            "ERROR: There was an error adding QR code!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                Log.d("QRScanActivity", throwable.getMessage());
                                                Intent intent = new Intent(QRScanActivity.this, AppHome.class);
                                                startActivity(intent);
                                            }
                                        });
                                        System.out.println("Could not add QR code" + throwable.getMessage());
                                        return null;
                                    }
                                });

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
        //https://stackoverflow.com/questions/38552144/how-get-permission-for-camera-in-android-specifically-marshmallow
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
