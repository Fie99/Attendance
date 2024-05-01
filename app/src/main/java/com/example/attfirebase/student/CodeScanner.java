package com.example.attfirebase.student;

import static android.Manifest.permission.CAMERA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class CodeScanner extends AppCompatActivity  {
    private static final int REQUEST_CAMERA = 1;

    private static final String TAG = "CODE_SCAN";

    private String unitID;
    private String qrCode;
    private String studentID;
    private String currentDate;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //setContentView(R.layout.activity_code_scanner);
       


        db = FirebaseFirestore.getInstance();
        scanCode();



        //Gets current date
        currentDate = new SimpleDateFormat("dd MM yyyy", Locale.ENGLISH).format(new Date());
        Log.d(TAG, "date: " + currentDate);


        //Retrieves QR code, module and student ID from ModulePicker.class
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        assert data != null;
        qrCode = data.getString("QR_CODE");
        unitID = data.getString("UNIT_ID");
        studentID = data.getString("STU_ID");
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission for camera required.")
                        .setMessage("This permission is required to scan QR codes.")
                        .setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanCode();
                            }
                        })
                        .setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            } else {
               // Toast.makeText(this, "No Result", Toast.LENGTH_SHORT).show();
                handleScanResult(result.getContents());
            }
        } else {
                super.onActivityResult(requestCode, resultCode, data);
            }

            }


    private void handleScanResult(String scanResult) {
        //If the QR code they scan matches the one the teacher generated
        if (qrCode.equals(scanResult)) {

            addAttendance();
            addAttendanceRecord();

            //Notifies the user their attendance is recorded
            AlertDialog.Builder builder = new AlertDialog.Builder(CodeScanner.this);
            builder.setTitle("Attendance Authenticated!");
            builder.setMessage("Your attendance for " + unitID + " on " + currentDate + " has been recorded.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(CodeScanner.this, StudentDashBoard.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {

            //Notifies the user the QR code is invalid
            AlertDialog.Builder builder = new AlertDialog.Builder(CodeScanner.this);
            builder.setTitle("Invalid QR code");
            builder.setMessage("This code is invalid.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(CodeScanner.this, StudentDashBoard.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    //Checks if permission was granted already
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                // Start the camera if permission is granted
                scanCode();
            } else {
                    Toast.makeText(this, "Attendance needs the camera to scan QR codes. Please enable camera permission in settings.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    //Adds attendance to the database collection where the teacher can see his students' attendance
    private void addAttendance() {

        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Attendance")
                .document(unitID)
                .collection("Date")
                .document(currentDate);

        documentReference.collection("Students").document(studentID).update("attended", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Success");
                        DocumentReference documentReference = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("Attendance")
                                .document(unitID)
                                .collection("Date")
                                .document(currentDate);

                        // Increment attendance number
                        documentReference.update("attendance", FieldValue.increment(1));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failure");
                    }
                });

    }

    //Adds attendance record to students' personal attendance
    private void addAttendanceRecord() {

        CollectionReference attendRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("AttendanceRecord")
                .document(studentID)
                .collection("Records");

        Query query = attendRef.whereEqualTo("unit", unitID).whereEqualTo("date", currentDate);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                        String attRecord = queryDocumentSnapshot.getId();
                        Log.d(TAG, "attRecord: " + attRecord);

                        DocumentReference reference = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("AttendanceRecord")
                                .document(studentID)
                                .collection("Records")
                                .document(attRecord);

                        reference.update("attended", true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Attendance updated");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Attendance failed to update");
                                    }
                                });
                    }
                } else {
                    Toast.makeText(CodeScanner.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
