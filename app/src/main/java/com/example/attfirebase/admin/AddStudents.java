package com.example.attfirebase.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddStudents extends AppCompatActivity {

    EditText fullName,email,password,regNo;
    Button regStudents;

    ProgressBar progressBar;
    String uid;

    AutoCompleteTextView autoCompleteTextView,autoCompleteTextView1;
    TextInputLayout  textInputLayout;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.et_fullName);
        regNo = findViewById(R.id.et_registerNumber);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextview);
        textInputLayout=findViewById(R.id.TextInputCourse);
        email = findViewById(R.id.et_registerEmail);
        password = findViewById(R.id.et_registerPassword);
        autoCompleteTextView1=findViewById(R.id.autoCompleteTextview1);
        progressBar = findViewById(R.id.progressbar);
        regStudents = findViewById(R.id.btn_registerStudent);

        // Set onClickListener for the register button
        regStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerStudent();
            }
        });
    }

    private void registerStudent() {
        // Retrieve user input
        String studentName = fullName.getText().toString().trim();
        String studentID = regNo.getText().toString().trim();
        String selectCourse = autoCompleteTextView.getText().toString().trim();
        String selectDepartment=autoCompleteTextView1.getText().toString().trim();
        String registerEmail = email.getText().toString().trim();
        String registerPassword = password.getText().toString().trim();

        // Validate input
        if (validateInput(studentName,selectDepartment, studentID,selectCourse, registerEmail, registerPassword)) {
            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Create a data map to be stored in Firestore
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("fullName", studentName);
            studentData.put("registerNumber", studentID);
           studentData.put("selectCourse", selectCourse);
            studentData.put("registerEmail", registerEmail);
            studentData.put("selectDepartment",selectDepartment);
            studentData.put("registerPassword", registerPassword);

            // Add userType to the student data
            studentData.put("userType", "student");

            // Add data to Firestore
            fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    FirebaseUser user = fAuth.getCurrentUser();
                    fStore.collection("students")
                            //.document(studentID)  // Assuming uid is the user's regNo
                            .add(studentData)
                            .addOnSuccessListener(documentReference -> {

                                // Enroll the student in selected units
                                enrollStudentInUnits(uid, studentID);
                                // Hide progress bar
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AddStudents.this, "Student added successfully", Toast.LENGTH_LONG).show();
                                // Clear input fields
                                clearInputFields();
                            });
                }
            }) .addOnFailureListener(e -> {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddStudents.this, "student already exists", Toast.LENGTH_LONG).show();
            });



        }
}

    private void enrollStudentInUnits(String uid, String studentID) {
        // Read units from XML string array
        String[] unitsArray = getResources().getStringArray(R.array.units_array);

        // Enroll the student in each unit
        for (String unitName : unitsArray) {
            // Add logic to enroll student in the unit
            Map<String, Object> enrollmentData = new HashMap<>();
            enrollmentData.put("unitName", unitName);

            if (uid != null) {
                fStore.collection("enrollments")
                        .document(uid)
                        .collection("units")
                        .add(enrollmentData)
                        .addOnSuccessListener(documentReference -> {
                            // Log success or handle accordingly
                            Log.d("ENROLLMENT", "Enrolled student in unit: " + unitName);
                        })
                        .addOnFailureListener(e -> {
                            // Log failure or handle accordingly
                            Log.e("ENROLLMENT", "Failed to enroll student in unit: " + unitName, e);
                        });
            } else {
                Log.e("ENROLLMENT", "Student UID is null");
            }
        }
    }

    private void clearInputFields() {
        fullName.setText("");
        regNo.setText("");
        autoCompleteTextView.setText("");
        autoCompleteTextView1.setText("");
        email.setText("");
        password.setText("");
    }

    private boolean validateInput(String studentName,String selectDepartment, String studentID,String selectCourse, String registerEmail, String registerPassword) {
        if (studentName.isEmpty() || selectDepartment.isEmpty()|| studentID.isEmpty()|| selectCourse.isEmpty()|| registerEmail.isEmpty() || registerPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }
}