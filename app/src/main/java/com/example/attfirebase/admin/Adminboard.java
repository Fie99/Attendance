package com.example.attfirebase.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.attfirebase.R;
import com.example.attfirebase.auth.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Adminboard extends AppCompatActivity {
    TextView nameDisplay;
    TextView idDisplay;
    CardView studentsCard;
    CardView teachersCard;
    CardView addUnitCard;
    CardView removeStudent;
    CardView registerUnitCard;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private Spinner spinner;
    private Button submitBtn;
    private long backPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminboard);
        nameDisplay = findViewById(R.id.nameDisplay);
        idDisplay = findViewById(R.id.idDisplay);
        studentsCard = findViewById(R.id.studentsCard);
        teachersCard = findViewById(R.id.teachersCard);
        //addUnitCard = findViewById(R.id.addUnitCard);
        registerUnitCard = findViewById(R.id.registerUnitCard);
        removeStudent=findViewById(R.id.removeStudentCard);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //getNameID();

        studentsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Adminboard.this, AddStudents.class);
                startActivity(intent);
            }
        });

        teachersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Adminboard.this, AddLecturers.class);
                startActivity(intent);
            }
        });

           /* addUnitCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboard.this, AddUnit.class);
                    startActivity(intent);
                }

            });*/

        registerUnitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Adminboard.this, RegisterUnit.class);
                startActivity(intent);
            }
        });
        removeStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Adminboard.this, RemoveStudents.class);
                startActivity(intent);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(Adminboard.this);
            builder.setTitle("Log Out");
            builder.setMessage("Do you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Adminboard.this, Login.class);
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
}
