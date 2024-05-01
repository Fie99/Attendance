package com.example.attfirebase.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.example.attfirebase.admin.Adminboard;
import com.example.attfirebase.lecturer.LecDashboard;
import com.example.attfirebase.student.StudentDashBoard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button loginBtn, gotoRegister;

    private final static String TAG = "login";


    private FirebaseAuth.AuthStateListener fAuthListener;



    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        //gotoRegister = findViewById(R.id.gotoRegister);

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAdmin();
            }
        });
    }

    private void loginAdmin() {
        String username = email.getText().toString().trim();
        String Password = password.getText().toString().trim();

        fAuth.signInWithEmailAndPassword(username,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Login successful",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = fAuth.getCurrentUser();
                            if (user!=null){
                                // Check if the user is an admin
                                // In this example, we assume that the admin's email is hardcoded
                                if (Objects.equals(user.getEmail(), "admin@gmail.com")) {
                                    // Redirect to the main admin page
                                    startActivity(new Intent(Login.this, Adminboard.class));
                                    finish();
                                }else {
                                    // Check user's type
                                    checkUserRoleAndRedirect(user.getUid());
                                }
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserRoleAndRedirect(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //User's UID is retrieved to find their record in the database
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        // Query the roles collection to check the user's role
        db.collection("School").document("0DKXnQhueh18DH7TSjsb").collection("User").document(uid)
                .get()

                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String userType = document.getString("user_type");
                                String userName = document.getString("name");


                                if (userType != null) {
                                    if (userType.equals("admin")) {
                                        // Redirect to the main admin page
                                        startActivity(new Intent(Login.this, Adminboard.class));
                                        Toast.makeText(Login.this, "Welcome, " + userName, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else if (userType.equals("lecturer")) {
                                        // Redirect to the lecturer dashboard
                                        startActivity(new Intent(Login.this, LecDashboard.class));
                                        Toast.makeText(Login.this, "Welcome, " + userName, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else if (userType.equals("student")) {
                                        // Redirect to the student dashboard
                                        startActivity(new Intent(Login.this, StudentDashBoard.class));
                                        Toast.makeText(Login.this, "Welcome, " + userName, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            } else {
                                // User document doesn't exist
                                // Handle the case as needed (assume student in this example)
                                startActivity(new Intent(Login.this, StudentDashBoard.class));
                                finish();
                            }
                        } else {
                            // Error getting user's type, assume student
                            startActivity(new Intent(Login.this, StudentDashBoard.class));
                            finish();
                        }
                    }
                });
    }

    private boolean isUserLecturer(FirebaseUser user) {
        return false;
    }
}





