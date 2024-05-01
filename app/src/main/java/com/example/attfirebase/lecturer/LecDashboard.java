package com.example.attfirebase.lecturer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.example.attfirebase.attendance.AttendanceSelect;
import com.example.attfirebase.auth.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LecDashboard extends AppCompatActivity {
    TextView nameDisplay;
    TextView idDisplay;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private long backPressed;
    CardView genCard,moduleCard,attendanceCard,settingsCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lec_dashboard);
        nameDisplay = findViewById(R.id.nameDisplay);
        idDisplay = findViewById(R.id.idDisplay);
        genCard = findViewById(R.id.genCard);
        moduleCard = findViewById(R.id.moduleCard);
        attendanceCard = findViewById(R.id.attendanceCard);
        //settingsCard = findViewById(R.id.settingsCard);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getNameID();

        genCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String teacherName = nameDisplay.getText().toString();
                String teacherID = idDisplay.getText().toString();

                Intent intent = new Intent(LecDashboard.this, GenerateCode.class);
                intent.putExtra("TEACHER_NAME", teacherName);
                intent.putExtra("TEACHER_ID", teacherID);
                startActivity(intent);

            }
        });

        moduleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LecDashboard.this, LecUnits.class);
                startActivity(intent);
            }
        });

        attendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LecDashboard.this, AttendanceSelect.class);
                startActivity(intent);
            }
        });

       /* settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LecDashboard.this, Settings.class);
                startActivity(intent);
            }
        });*/
    }

    //Asks the user if they want to log out when they press the back button
    @Override
    public void onBackPressed() {

        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LecDashboard.this);
            builder.setTitle("Log Out");
            builder.setMessage("Do you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(LecDashboard.this, Login.class);
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

    //Retrieves current user's name and ID from database and displays it
    private void getNameID() {

        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();

                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {

                        String teacherName = documentSnapshot.getString("name");
                        String teacherID = documentSnapshot.getString("teacher_id");

                        nameDisplay.setText(teacherName);
                        idDisplay.setText(teacherID);
                    } else {
                        Toast.makeText(LecDashboard.this, "Document doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LecDashboard.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
