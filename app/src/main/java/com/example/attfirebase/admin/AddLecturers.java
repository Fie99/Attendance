package com.example.attfirebase.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class AddLecturers extends AppCompatActivity {
    EditText fullName,email,password,phoneNumber, lecNumber;
    Button regLec;

    ProgressBar progressBar;
    AutoCompleteTextView autoCompleteTextView;
    TextInputLayout textInputLayout;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecturers);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        // Initialize views
        fullName = findViewById(R.id.et_fullNameName);
        phoneNumber = findViewById(R.id.et_registerPhone);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextview);
        textInputLayout= findViewById(R.id.TextInputDepartment);
        email = findViewById(R.id.et_registerEmail);
        password = findViewById(R.id.et_registerPassword);
        lecNumber= findViewById(R.id.et_Pf);
        progressBar = findViewById(R.id.progressbar);
        regLec = findViewById(R.id.btn_registerLec);

        // Set onClickListener for the register button
        regLec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerLecturer();
            }
        });
    }

    private void registerLecturer() {

        // Retrieve user input
        String FullName = fullName.getText().toString().trim();
        String registerPhone = phoneNumber.getText().toString().trim();
        String selectDepartment = autoCompleteTextView.getText().toString().trim();
        String registerEmail = email.getText().toString().trim();
        String registerPassword = password.getText().toString().trim();
        String LecNumber = lecNumber.getText().toString().trim();

        // Validate input
        if (validateInput(FullName, registerPhone, registerEmail, registerPassword,LecNumber)) {
            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Create a data map to be stored in Firestore
            Map<String, Object> lecturerData = new HashMap<>();
            lecturerData.put("fullName", FullName);
            lecturerData.put("registerPhone", registerPhone);
            lecturerData.put("selectDepartment", selectDepartment);
            lecturerData.put("registerEmail", registerEmail);
            lecturerData.put("registerPassword", registerPassword);
            lecturerData.put("LecNumber",LecNumber);


            // Add userType to the lecturer data
           /// lecturerData.put("userType", "lecturer");

            // Add data to Firestore
            fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    FirebaseUser user = fAuth.getCurrentUser();
                    fStore.collection("lecturers")
                            .add(lecturerData)
                            .addOnSuccessListener(documentReference -> {
                                // Hide progress bar
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AddLecturers.this, "Lecturer registered successfully", Toast.LENGTH_LONG).show();
                                // Clear input fields
                                clearInputFields();
                            });
                }
            }) .addOnFailureListener(e -> {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddLecturers.this, "lecturer already exists", Toast.LENGTH_LONG).show();
            });


        }
    }

    private void clearInputFields() {
        fullName.setText("");
        phoneNumber.setText("");
        //etSelectDepartment.setText("");
        email.setText("");
        password.setText("");
    }

    private boolean validateInput(String FullName, String registerPhone, String registerEmail, String registerPassword, String lecNumber) {

        if (FullName.isEmpty() || registerPhone.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}