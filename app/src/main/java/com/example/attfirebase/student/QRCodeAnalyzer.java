package com.example.attfirebase.student;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.firebase.firestore.FirebaseFirestore;

public class QRCodeAnalyzer implements ImageAnalysis.Analyzer {
    public QRCodeAnalyzer(String qrCode, String spinnerValue, String studentID, String currentDate, FirebaseFirestore db) {
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {

    }
}
