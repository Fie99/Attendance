package com.example.attfirebase.student;

import static com.example.attfirebase.student.AttendanceScreen.isConnectedtoInternet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.example.attfirebase.auth.Login;
import com.example.attfirebase.units.SelectUnit;
import com.example.attfirebase.units.StudentUnits;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class StudentDashBoard extends AppCompatActivity {
    TextView SDashboard;
    CardView ScanCardView, AttendanceCardView, SelectUnitCardView, SettingsCardView;

    private long backPressed;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    TextView nameDisplay;
    TextView idDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dash_board);

        ScanCardView = findViewById(R.id.card1);
        AttendanceCardView = findViewById(R.id.card2);
        SelectUnitCardView = findViewById(R.id.card3);
        SettingsCardView = findViewById(R.id.card4);
        nameDisplay = findViewById(R.id.nameDisplay);
        idDisplay = findViewById(R.id.idDisplay);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        getNameID();

        SettingsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent card4 = new Intent(StudentDashBoard.this, Settings.class);
                startActivity(card4);
            }
        });
        ScanCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentDashBoard.this, SelectUnit.class);
                String studentName = nameDisplay.getText().toString();
                String studentID = idDisplay.getText().toString();

                intent.putExtra("STUDENT_NAME", studentName);
                intent.putExtra("STUDENT_ID", studentID);
                startActivity(intent);
            }
        });
        SelectUnitCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentName = nameDisplay.getText().toString();
                String studentID = idDisplay.getText().toString();

                Intent intent = new Intent(StudentDashBoard.this, StudentUnits.class);
                intent.putExtra("STUDENT_ID", studentID);
                intent.putExtra("STUDENT_NAME", studentName);
                startActivity(intent);
            }
        });
        AttendanceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnectedtoInternet(StudentDashBoard.this)){
                    Intent intent = new Intent(StudentDashBoard.this, AttendanceScreen.class);
                    intent.putExtra("STU_ID", idDisplay.getText().toString());
                    startActivity(intent);
                }
                else {
                    Toast.makeText(StudentDashBoard.this, "Please connect to internet to see attendance", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    public void onBackPressed() {

        if(backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(StudentDashBoard.this);
            builder.setTitle("Log Out");
            builder.setMessage("Do you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(StudentDashBoard.this, Login.class);
                    startActivity(intent);
                    fAuth.signOut();
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
        }
        backPressed = System.currentTimeMillis();
    }


    private void getNameID() {
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();

                    assert documentSnapshot != null;
                    if(documentSnapshot.exists()) {

                        //Retrieves name and student ID
                        String studentName = documentSnapshot.getString("name");
                        String studentID = documentSnapshot.getString("student_id");

                        //Sets display TextViews to the student's name and ID
                        nameDisplay.setText(studentName);
                        idDisplay.setText(studentID);
                    }
                    //else {
                       // Toast.makeText(StudentDashBoard.this, "Document doesn't exist", Toast.LENGTH_SHORT).show();
                    //}
                }
                else {
                    Toast.makeText(StudentDashBoard.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    }
