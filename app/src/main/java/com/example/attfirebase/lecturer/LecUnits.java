package com.example.attfirebase.lecturer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.attfirebase.R;
import com.example.attfirebase.Records.PdfGeneratorActivity;
import com.example.attfirebase.units.StudentUnitsItem;
import com.example.attfirebase.units.UnitsAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LecUnits extends AppCompatActivity {

    private UnitsAdapter unitsAdapter;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    private String studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.units_recyclerview);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();



        Intent intent = getIntent();
        studentID = intent.getStringExtra("STU_ID");
        Log.d("Student_ID", "Student ID: " + studentID);

        getUnits();

    }


    public void getUnits() {
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        final CollectionReference unitsRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Units");
        // Define the data to add or update in the documents
        Map<String, Object> unitData = new HashMap<>();
        unitData.put("unit_lecturer", "Tom Doe");
        unitData.put("unit_date", "2024-02-28");

        Query query = unitsRef.orderBy("unit", Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<StudentUnitsItem> unitsList = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                        unitsRef.document(queryDocumentSnapshot.getId()).set(unitData, SetOptions.merge());
                        String Id = queryDocumentSnapshot.getId();
                        String unitLecturer = queryDocumentSnapshot.getString("unit_lecturer");
                        String unitDate = queryDocumentSnapshot.getString("unit_date");

                        // Create StudentUnitsItem object
                        StudentUnitsItem unitItem = new StudentUnitsItem(Id, unitLecturer, unitDate);
                        // Log the retrieved data
                        Log.d("Data", "ID: " + Id);
                        Log.d("Data", "Unit Lecturer: " + unitLecturer);
                        Log.d("Data", "Unit Date: " + unitDate);

                        // Add StudentUnitsItem object to the list
                        unitsList.add(unitItem);
                    }
                    FirestoreRecyclerOptions<StudentUnitsItem> options = new FirestoreRecyclerOptions.Builder<StudentUnitsItem>()
                            .setQuery(query, StudentUnitsItem.class)
                            .build();

                    // Set adapter to RecyclerView
                    unitsAdapter = new UnitsAdapter(options);
                    RecyclerView recyclerView = findViewById(R.id.classRecyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(LecUnits.this));
                    recyclerView.setAdapter(unitsAdapter);
                    unitsAdapter.startListening(); // Start listening for Firestore data


                    if (unitsAdapter != null) {

                        unitsAdapter.setOnItemClickListener(new UnitsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                                String unit = documentSnapshot.getId();

                                Intent intent = new Intent(LecUnits.this, PdfGeneratorActivity.class);
                                intent.putExtra("STU_ID", studentID);
                                intent.putExtra("UNIT_ID", unit);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        });



        }
    }
