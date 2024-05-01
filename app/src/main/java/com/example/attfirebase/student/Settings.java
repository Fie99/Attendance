package com.example.attfirebase.student;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.attfirebase.R;
import com.example.attfirebase.auth.Login;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button resetPassBtn = findViewById(R.id.resetPassBtn);
        Button logOutBtn = findViewById(R.id.logOutBtn);

        resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, PasswordReset.class);
                startActivity(intent);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("Log Out");
                builder.setMessage("Are you sure you want to log out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(Settings.this, Login.class);
                        startActivity(intent);
                        finish();
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
        });
    }
    }
