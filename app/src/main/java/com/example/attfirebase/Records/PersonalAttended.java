package com.example.attfirebase.Records;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.attfirebase.R;
import com.example.attfirebase.attendance.PersonalAttFragInterface;
import com.example.attfirebase.attendance.PersonalAttendanceAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PersonalAttended extends AppCompatActivity implements PersonalAttFragInterface {
    private String unit;
    private String studentID;

    private FirebaseFirestore db;
    PersonalAttendanceAdapter adapter;
    private RecyclerView recyclerView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.personal_attended_recyclerview);



            db = FirebaseFirestore.getInstance();


            //Retrieves module and student ID from Intent extras
            Intent intent= getIntent();
            if (intent != null) {
                unit = intent.getStringExtra("UNIT_ID");
                studentID = intent.getStringExtra("STU_ID");
            }

            // Adding log messages to check if unit and studentID are retrieved successfully
            Log.d("PersonalAttendance", "Unit Name: " + unit);
            Log.d("PersonalAttendance", "Student ID: " + studentID);

            // Make sure both unit and date are not null before proceeding
           if (unit != null && studentID != null) {
                getDates();
            }



           //getDates();
        }

        private void getDates() {
            if (studentID != null) {
                Log.d("PERS", "Student ID retrieved successfully: " + studentID);
                //Looks for the student's personal attendance record based on their student ID
                CollectionReference recordRef = db.collection("School")
                        .document("0DKXnQhueh18DH7TSjsb")
                        .collection("AttendanceRecord")
                        .document(studentID)
                        .collection("Records");

                //Retrieves the dates they were present for the selected module
                final Query query = recordRef.whereEqualTo("unit", unit).whereEqualTo("attended", true);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<String> datesList = new ArrayList<>();
                                for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                                    String dateString = queryDocumentSnapshot.getString("date");

                                    datesList.add(dateString);
                                Log.d("PERS", "Docs: " + dateString);
                                    // Log datesList to ensure it contains the expected dates
                                    Log.d("PERS", "Dates List: " + datesList);
                            }
                            //Builds the RecyclerView
                            FirestoreRecyclerOptions<Date> options = new FirestoreRecyclerOptions.Builder<Date>()
                                    .setQuery(query, Date.class)
                                    .build();


                            adapter = new PersonalAttendanceAdapter(options);
                            recyclerView = findViewById(R.id.personalAttendedRV);
                            recyclerView.setLayoutManager(new LinearLayoutManager(PersonalAttended.this));
                            recyclerView.setAdapter(adapter);
                            adapter.startListening();
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }

        @Override
        public String getUnitName() {
            return unit;
        }

        @Override
        public String getStudentID() {
            return studentID;
        }
    }

