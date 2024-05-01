package com.example.attfirebase.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RemoveStudents extends AppCompatActivity {
    private EditText etStudentId;
    private Button btnDelete;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_students);

        firestore = FirebaseFirestore.getInstance();

        etStudentId = findViewById(R.id.et_student);
        btnDelete = findViewById(R.id.btn_remove);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStudent();
            }
        });
    }

    private void deleteStudent() {
        String studentID = etStudentId.getText().toString().trim();

        // Check if studentId is not empty
        if (!studentID.isEmpty()) {
            // Query the Firestore collection based on studentId
            firestore.collection("students")
                    .whereEqualTo("registerNumber", studentID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            // Delete the document
                            documentSnapshot.getReference().delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Document successfully deleted
                                            Toast.makeText(RemoveStudents.this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                                            etStudentId.getText().clear();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error handling
                                            Toast.makeText(RemoveStudents.this, "Error deleting student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error handling
                            Toast.makeText(RemoveStudents.this, "Error querying student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Show a message if studentId is empty
            Toast.makeText(RemoveStudents.this, "Please enter a student ID", Toast.LENGTH_SHORT).show();
        }

    }
}
