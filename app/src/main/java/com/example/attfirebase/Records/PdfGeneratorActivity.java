package com.example.attfirebase.Records;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PdfGeneratorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String studentID;
    private String unit;
    private List<Map<String, Object>> attendanceRecords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_generator);
        // Initialize FirebaseFirestore
        db = FirebaseFirestore.getInstance();


        String unitId = getIntent().getStringExtra("UNIT_ID");
        String studentId = getIntent().getStringExtra("STU_ID");




        generateReport(unitId, studentId);
    }

    private void generateReport(String unitId, String studentId) {
        // Fetch attendance records for the unit
        if (studentId != null) {
            Query attendanceRef = db.collection("School")
                    .document("0DKXnQhueh18DH7TSjsb")
                    .collection("AttendanceRecord")
                    .document(studentID)
                    .collection("Records")
                    .whereEqualTo("unit", unitId);

            attendanceRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<AttendanceRecord> attendanceRecords = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String unit = document.getString("unit");
                            Timestamp timestamp = document.getTimestamp("timestamp");

                            // Create an AttendanceRecord object and add it to the list
                            AttendanceRecord record = new AttendanceRecord(unit, timestamp.toDate());
                            attendanceRecords.add(record);
                        }

                        // Generate the report and display it
                        generateAndDisplayReport(attendanceRecords);
                    } else {
                        // Handle the error
                    }
                }
            });
        }
    }

        private void generateAndDisplayReport(List<AttendanceRecord> attendanceRecords) {
            // Generate the report
            StringBuilder report = new StringBuilder();
            report.append("Attendance Report\n\n");

            for (AttendanceRecord record : attendanceRecords) {
                report.append("Unit: ").append(record.getUnit())
                        .append(", Date: ").append(record.getDate())
                        .append(", Time: ").append(record.getTime())
                        .append("\n");
            }

            // Display the report
            TextView reportTextView = findViewById(R.id.reportTextView);
            reportTextView.setText(report.toString());
        }

    public void shareReport(View view) {
        // Add code here to handle sharing the report
        String reportText = ((TextView) findViewById(R.id.reportTextView)).getText().toString();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, reportText);
        shareIntent.setType("text/plain");

        Intent shareIntentChooser = Intent.createChooser(shareIntent, "Share Report");
        if (shareIntentChooser.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntentChooser);
        } else {
            // Handle if no app can handle the share intent
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show();
        }
    }
    }

