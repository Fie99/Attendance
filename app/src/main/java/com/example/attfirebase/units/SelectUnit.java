package com.example.attfirebase.units;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.example.attfirebase.student.CodeScanner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectUnit extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private final static String TAG = "UNIT_PICK";

    private Boolean codeCheck;
    private String uid;
    private FirebaseFirestore fstore;

    private String studentID;
    private Spinner spinner;
    Button scanCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_unit);

        scanCodeBtn = findViewById(R.id.scanCodeBtn);
        spinner = findViewById(R.id.spinner);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



      /* String spinnerValue = "";
        if (spinner.getSelectedItem() != null) {
            spinnerValue = spinner.getSelectedItem().toString();
        } else {
            // Handle the case where nothing is selected in the spinner
            Toast.makeText(SelectUnit.this, "Please select a unit", Toast.LENGTH_SHORT).show();
            return;
        }*/


        uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        codeCheck = false;

        if (user != null) {
            Log.d(TAG, "User found");
        } else {
            Log.d(TAG, "User not found");
        }

        //Retrieves student ID from StudentActivity.class
        Intent intent = getIntent();
        studentID = intent.getStringExtra("STUDENT_ID");

        populateSpinner();

        scanCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnectedtoInternet(SelectUnit.this)){
                    unitCheck();

                }
                else {
                    Toast.makeText(SelectUnit.this, "Please connect to internet to scan code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //spinner.setOnItemSelectedListener(this);
        unitCheck();

    }

    private void populateSpinner() {

        //Determines database path for the user's modules
        final CollectionReference unitsRef = fstore.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Units");

        //Prepares spinner
        final List<String> unitsList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectUnit.this, android.R.layout.simple_spinner_item, unitsList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);


        //Searches for modules the current student user is enrolled in and adds them to the spinner
        unitsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        String unitName = queryDocumentSnapshot.getId();
                        // Adds the unit name to the unitsList
                        unitsList.add(unitName);

                        Log.d(TAG, "Units: " + unitsList);
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });
    }

    //To check if a QR code was generated for the selected module in the spinner
    private void unitCheck() {
        if (spinner.getSelectedItem() == null) {
            Toast.makeText(SelectUnit.this, "Please select a unit", Toast.LENGTH_SHORT).show();
            return;
        }

        //Spinner value determines which module they want to record their attendance for
        final String spinnerValue = spinner.getSelectedItem().toString();

        //Determines document path for the chosen module
        DocumentReference moduleRef = fstore.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Units")
                .document(spinnerValue);

        //Finds correct document to retrieve the QR code and module ID
        moduleRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {

                        //Retrieves QR code and document ID for the module
                        String qrCode = documentSnapshot.getString("qr_code");
                        String unitID = documentSnapshot.getId();

                        //If qr_code is not null. Meaning the QR code has not been generated
                        if (qrCode != null) {
                            codeCheck = true;
                        }

                        //If a code was generated for the module, pass in the module name and document ID to the CodeScanner class
                        if (codeCheck.equals(true)) {
                            Bundle data = new Bundle();
                            data.putString("UNIT_ID", unitID);
                            data.putString("QR_CODE", qrCode);
                            data.putString("STU_ID", studentID);
                            Intent intent = new Intent(SelectUnit.this, CodeScanner.class);
                            intent.putExtras(data);
                            startActivity(intent);
                        } else if (codeCheck.equals(false)) {
                            Toast.makeText(SelectUnit.this, "Code not generated for this module", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SelectUnit.this, "No document exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SelectUnit.this, "Error. " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Checks if user is connected to the internet
    public static boolean isConnectedtoInternet(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
        {
            Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Call unitCheck when an item is selected


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}