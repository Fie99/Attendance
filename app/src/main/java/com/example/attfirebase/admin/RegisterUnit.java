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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterUnit extends AppCompatActivity {
    EditText code;
    AutoCompleteTextView selectUnit, selectDepartment;
    TextInputLayout textInputLayout;
    Button registerUnit;
    ProgressBar progressBar;

    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_unit);

        code = findViewById(R.id.et_code);
        selectDepartment = findViewById(R.id.autoCompleteTextview);
        selectUnit = findViewById(R.id.autoCompleteTextview1);
        registerUnit = findViewById(R.id.btn_registerUnit);

        fStore = FirebaseFirestore.getInstance();

        registerUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUnit();
            }
        });
    }

    private void registerUnit() {
        String Code = code.getText().toString().trim();
        String department = selectDepartment.getText().toString().trim();
        String unit = selectUnit.getText().toString().trim();

        // Validate input fields if needed

        // Create a map with the data
        Map<String, Object> unitData = new HashMap<>();
        unitData.put("code", Code);
        unitData.put("department", department);
        unitData.put("unit", unit);

        // Add data to Firestore
        fStore.collection("units")
                .add(unitData)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    // You can add any additional logic here
                   // progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterUnit.this,"Unit Registered successfully",Toast.LENGTH_LONG).show();
                    finish(); // Close the registration activity
                })
                .addOnFailureListener(e -> {
                    // Handle failures
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterUnit.this,"Registration failed",Toast.LENGTH_LONG).show();
                    // Show an error message to the user
                    // You might want to log the error as well
                });

        // Show a progress bar while data is being added to Firestore
        //progressBar.setVisibility(View.VISIBLE);

    }

}