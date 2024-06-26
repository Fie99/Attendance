package com.example.attfirebase.lecturer;

import static android.app.ProgressDialog.show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.example.attfirebase.student.CodeScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GenerateCode extends AppCompatActivity {
    private final static String TAG = "GEN_CODE";

    private FirebaseFirestore db;

    private String uid;
    private Spinner teacherSpinner;
    private String currentDate;


    Button genCodeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        genCodeBtn = findViewById(R.id.genCodeBtn);
        teacherSpinner = findViewById(R.id.spinner);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        populateSpinner();
        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnectedtoInternet(GenerateCode.this)) {
                    generateCode();
                } else {
                    Toast.makeText(GenerateCode.this, "Please connect to internet to generate code", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    private void populateSpinner() {

        //Searches for the teacher's modules in their database record
        // moduleRef checks if document exists and retrieves data
        CollectionReference moduleRef = db.collection("School")
                .document("Units")
                .collection("User")
                .document(uid)
                .collection("Units");

        //Prepares spinner and dropdown list
        final List<String> unitsList = new ArrayList<>();
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.units_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        teacherSpinner.setAdapter(adapter);

        //Retrieves the IDs of the modules and adds them to the list for the spinner
        moduleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                        String unitName = queryDocumentSnapshot.getId();
                        unitsList.add(unitName);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GenerateCode.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateCode() {
        String spinnerValue = null;
        if (teacherSpinner != null && teacherSpinner.getCount() > 0) {
            // Retrieve value from spinner
            spinnerValue = teacherSpinner.getSelectedItem().toString();

        } else {
            Toast.makeText(GenerateCode.this, "No item selected", Toast.LENGTH_SHORT).show();
            return; // Exit the method if no item is selected
        }
        android.util.Log.d(TAG, "Selected spinner value: " + spinnerValue);



// Handle the case where spinnerValue is null
        if (spinnerValue != null && !spinnerValue.isEmpty()) {
            // Determines database for the selected module's database record
            final DocumentReference moduleRef = db.collection("School")
                    .document("0DKXnQhueh18DH7TSjsb")
                    .collection("Units")
                    .document(spinnerValue);

            moduleRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot documentSnapshot = task.getResult();
                        assert documentSnapshot != null;

                        if (documentSnapshot.exists()) {

                            //Retrieves the ID of the corresponding module document
                            final String unitID = documentSnapshot.getId();

                            final DocumentReference documentReference = db.collection("School")
                                    .document("0DKXnQhueh18DH7TSjsb")
                                    .collection("Units")
                                    .document(unitID);

                            //Updates the qr_code field in the right module document with a randomised char string
                            documentReference.update("qr_code", genRandomString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    android.util.Log.d(TAG, "Code generated successfully");

                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot snapshot = task.getResult();

                                                assert snapshot != null;
                                                if (snapshot.exists()) {
                                                    final String qrCode = snapshot.getString("qr_code");

                                                    //Gets current date
                                                    currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date());

                                                    //Adds module field to the generated module document in the Attendance collection
                                                    final Map<String, Object> unit = new HashMap<>();
                                                    unit.put("unit", unitID);

                                                    //Adds date and attendance field to the generated date document in the Date sub-collection
                                                    final Map<String, Object> date = new HashMap<>();
                                                    date.put("date", currentDate);
                                                    date.put("attendance", 0);



                                                    //Adds module to Attendance collection
                                                    db.collection("School")
                                                            .document("0DKXnQhueh18DH7TSjsb")
                                                            .collection("Attendance")
                                                            .document(unitID)
                                                            .set(unit);

                                                    //Determines database path for the right date document in the Attendance collection
                                                    DocumentReference dateCheck = db.collection("School")
                                                            .document("0DKXnQhueh18DH7TSjsb")
                                                            .collection("Attendance")
                                                            .document(unitID)
                                                            .collection("Date")
                                                            .document(currentDate);

                                                /*Checks if code was already generated for the selected module on the current date
                                                and asks the user if they want to overwrite the previous attendance
                                                 */
                                                    dateCheck.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {

                                                                DocumentSnapshot docSnap = task.getResult();
                                                                assert docSnap != null;
                                                                if (docSnap.exists()) {

                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(GenerateCode.this);
                                                                    builder.setTitle("Overwrite Attendance?");
                                                                    builder.setMessage("Generating a QR code will overwrite previous attendance. Continue?");
                                                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            //Adds current date record to database
                                                                            db.collection("School")
                                                                                    .document("0DKXnQhueh18DH7TSjsb")
                                                                                    .collection("Attendance")
                                                                                    .document(unitID)
                                                                                    .collection("Date")
                                                                                    .document(currentDate)
                                                                                    .set(date);

                                                                            //Determines database path for all the students enrolled in the selected module
                                                                            CollectionReference studentRef = db.collection("School")
                                                                                    .document("0DKXnQhueh18DH7TSjsb")
                                                                                    .collection("Units")
                                                                                    .document(unitID)
                                                                                    .collection("Students");

                                                                            //Gets all students enrolled in the module and adds them to the Attendance collection
                                                                            Query query = studentRef.orderBy("student_id", Query.Direction.DESCENDING);
                                                                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                                    if (task.isSuccessful()) {
                                                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                                                                                            String studentID = queryDocumentSnapshot.getId();
                                                                                            android.util.Log.d("STU_ID", studentID);

                                                                                            //Attendance sent to false by default. Updated to true when they scan the code
                                                                                            Map<String, Object> attended = new HashMap<>();
                                                                                            attended.put("attended", false);
                                                                                            attended.put("student_id", studentID);

                                                                                            db.collection("School")
                                                                                                    .document("0DKXnQhueh18DH7TSjsb")
                                                                                                    .collection("Attendance")
                                                                                                    .document(unitID)
                                                                                                    .collection("Date")
                                                                                                    .document(currentDate)
                                                                                                    .collection("Students")
                                                                                                    .document(studentID)
                                                                                                    .set(attended);

                                                                                            //Adds student ID document to AttendanceRecord collection
                                                                                            Map<String, Object> stuID = new HashMap<>();
                                                                                            stuID.put("student_id", studentID);

                                                                                            db.collection("School")
                                                                                                    .document("0DKXnQhueh18DH7TSjsb")
                                                                                                    .collection("AttendanceRecord")
                                                                                                    .document(studentID)
                                                                                                    .set(stuID);

                                                                                            //Adds date, module and attended checker to student's personal attendance record
                                                                                            Map<String, Object> attend = new HashMap<>();
                                                                                            attend.put("date", currentDate);
                                                                                            attend.put("unit", unitID);
                                                                                            attend.put("attended", false);
                                                                                            attend.put("time", FieldValue.serverTimestamp());

                                                                                            //Removes spaces for the ID of the generated attendance document
                                                                                            String docName = unitID.replaceAll("\\s+", "") + currentDate.replaceAll("\\s+", "");

                                                                                            db.collection("School")
                                                                                                    .document("0DKXnQhueh18DH7TSjsb")
                                                                                                    .collection("AttendanceRecord")
                                                                                                    .document(studentID)
                                                                                                    .collection("Records")
                                                                                                    .document(docName)
                                                                                                    .set(attend, SetOptions.merge());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });

                                                                            //Passes QR Code and module to CodeScreen class
                                                                            Intent intent = new Intent(GenerateCode.this, CodeScreen.class);
                                                                            intent.putExtra("QR_CODE", qrCode);
                                                                            intent.putExtra("UNIT_ID", unitID);
                                                                            startActivity(intent);

                                                                        }
                                                                    });
                                                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    });
                                                                    AlertDialog alert = builder.create();
                                                                    alert.show();
                                                                } else {

                                                                    //Adds current date record to database
                                                                    db.collection("School")
                                                                            .document("0DKXnQhueh18DH7TSjsb")
                                                                            .collection("Attendance")
                                                                            .document(unitID)
                                                                            .collection("Date")
                                                                            .document(currentDate)
                                                                            .set(date);

                                                                    //Determines database path for all the students enrolled in the selected module
                                                                    CollectionReference studentRef = db.collection("School")
                                                                            .document("0DKXnQhueh18DH7TSjsb")
                                                                            .collection("Units")
                                                                            .document(unitID)
                                                                            .collection("Students");

                                                                    //Gets all students enrolled in the module and adds them to the Attendance collection
                                                                    Query query = studentRef.orderBy("student_id", Query.Direction.DESCENDING);
                                                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            if (task.isSuccessful()) {
                                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                                                                                    String studentID = queryDocumentSnapshot.getId();
                                                                                    Log.d("STU_ID", studentID);

                                                                                    //Attendance sent to false by default. Updated to true when they scan the code
                                                                                    Map<String, Object> attended = new HashMap<>();
                                                                                    attended.put("attended", false);
                                                                                    attended.put("student_id", studentID);

                                                                                    db.collection("School")
                                                                                            .document("0DKXnQhueh18DH7TSjsb")
                                                                                            .collection("Attendance")
                                                                                            .document(unitID)
                                                                                            .collection("Date")
                                                                                            .document(currentDate)
                                                                                            .collection("Students")
                                                                                            .document(studentID)
                                                                                            .set(attended);

                                                                                    //Adds student ID document to AttendanceRecord collection
                                                                                    Map<String, Object> stuID = new HashMap<>();
                                                                                    stuID.put("student_id", studentID);

                                                                                    db.collection("School")
                                                                                            .document("0DKXnQhueh18DH7TSjsb")
                                                                                            .collection("AttendanceRecord")
                                                                                            .document(studentID)
                                                                                            .set(stuID);

                                                                                    //Adds date, module and attended checker to student's personal attendance record
                                                                                    Map<String, Object> attend = new HashMap<>();
                                                                                    attend.put("date", currentDate);
                                                                                    attend.put("unit", unitID);
                                                                                    attend.put("attended", false);
                                                                                    attend.put("time", FieldValue.serverTimestamp());

                                                                                    //Removes spaces for the ID of the generated attendance document
                                                                                    String docName = unitID.replaceAll("\\s+", "") + currentDate.replaceAll("\\s+", "");

                                                                                    db.collection("School")
                                                                                            .document("0DKXnQhueh18DH7TSjsb")
                                                                                            .collection("AttendanceRecord")
                                                                                            .document(studentID)
                                                                                            .collection("Records")
                                                                                            .document(docName)
                                                                                            .set(attend, SetOptions.merge());
                                                                                }
                                                                            }
                                                                        }
                                                                    });

                                                                    //Passes QR Code and module to CodeScreen class
                                                                    Intent intent = new Intent(GenerateCode.this, CodeScreen.class);
                                                                    intent.putExtra("QR_CODE", qrCode);
                                                                    intent.putExtra("UNIT_ID", unitID);
                                                                    startActivity(intent);
                                                                }
                                                            } else {
                                                                Toast.makeText(GenerateCode.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(GenerateCode.this, "Nothing found.", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(GenerateCode.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(GenerateCode.this, "Document doesn't exist.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(GenerateCode.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        // Continue with your existing code that uses moduleRef
    }




    //Generates random string for the QR code
    private String genRandomString() {
        char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            char c = characters[random.nextInt(characters.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();

    }

    //Checks if user is connected to the internet
    public static boolean isConnectedtoInternet(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    //Methods for the spinner. Unused but required
    // @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    //@Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
