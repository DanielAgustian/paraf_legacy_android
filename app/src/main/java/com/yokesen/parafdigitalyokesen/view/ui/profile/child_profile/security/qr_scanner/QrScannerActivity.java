package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.qr_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;


import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class QrScannerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 0;
    boolean openingSnackBar = false;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

//    private Button qrCodeFoundButton;
    private String qrCode;
    PreferencesRepo preferencesRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        previewView = findViewById(R.id.activity_main_previewView);
        preferencesRepo = new PreferencesRepo(this);
//        qrCodeFoundButton = findViewById(R.id.activity_main_qrCodeFoundButton);
//        qrCodeFoundButton.setVisibility(View.INVISIBLE);
//        qrCodeFoundButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), qrCode, Toast.LENGTH_SHORT).show();
//                Log.i(MainActivity.class.getSimpleName(), "QR Code Found: " + qrCode);
//            }
//        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();
    }

    long milisStart = 0;
    @Override
    protected void onPause() {
        super.onPause();
        milisStart = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String passcode = preferencesRepo.getPasscode();
        int isActive = preferencesRepo.getAllowPasscode();
        long intervetion = 30 * 60 * 1000;
        long milisNow = Calendar.getInstance().getTimeInMillis();
        long milisSelisih = milisNow - milisStart;

        if(isActive == 1 && passcode!= null && !passcode.equals("")){

            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                Intent intent = new Intent(this, PasscodeView.class);
                startActivity(intent);
            }
        }

        int isBiometricActive = preferencesRepo.getBiometric();
        if(isBiometricActive == 1){
            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                UtilWidget uw = new UtilWidget(this);
                uw.biometricPrompt();
            }
        }

    }




    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(QrScannerActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {
                qrCode = _qrCode;
                if(!openingSnackBar){
                    snackBarQR(qrCode, previewView);
                    openingSnackBar  = true;
                }
//                qrCodeFoundButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void qrCodeNotFound() {

//                qrCodeFoundButton.setVisibility(View.INVISIBLE);
            }
        }));

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }

    public void snackBarQR(String content, View parentView){

        Snackbar view = Snackbar.make(parentView, content,  Snackbar.LENGTH_LONG);
        view.setAction("Go", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("QRSCANCONTENT", content);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(content));
                startActivity(browserIntent);
            }
        });

        view.show();
        view.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    // Snackbar closed on its own
                    openingSnackBar = false;
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {

            }
        });
    }
}