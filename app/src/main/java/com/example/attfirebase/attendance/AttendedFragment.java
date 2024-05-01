package com.example.attfirebase.attendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AttendedFragment extends Fragment {
    private FirebaseFirestore db;
    AttendanceAdapter attendAdapter;
    private RecyclerView recyclerView;
    private String unit;
    private String date;



    public AttendedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_attended, container, false);
        recyclerView = v.findViewById(R.id.attendedRV);

        db = FirebaseFirestore.getInstance();

        //Module and date retrieved from AttendanceScreen class using an interface
        AttFragInterface activity = (AttFragInterface) getActivity();
        assert activity != null;
        unit = activity.getUnit();
        date = activity.getDate();
        // Adding log messages to check if unit and date are retrieved successfully
        Log.d("AttendanceSelect", "Unit: " + unit);
        Log.d("AttendanceSelect", "Selected Date: " + date);

        Log.d("AttendanceSelect", "Calling getStudents() method");

        getStudents();

        return v;
    }

    private void getStudents() {
        if (unit != null) {

            //Determines document path for student attendance on the selected module and date
            CollectionReference studentsRef = db.collection("School")
                    .document("0DKXnQhueh18DH7TSjsb")
                    .collection("Attendance")
                    .document(unit)
                    .collection("Date")
                    .document(date)
                    .collection("Students");



            //Looks for the students who HAVE attended the module
            final Query query = studentsRef.whereEqualTo("attended", true);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   // List<String> students = new ArrayList<>(); // List to store student IDs
                    if (task.isSuccessful()) {
                        // Create a list to store student IDs
                        List<String> studentIds = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String studentId = document.getString("student_id");
                            studentIds.add(studentId); // Add student ID to the list
                            Log.d("Firestore_Query", "Student ID: " + studentId);
                            Log.d("Firestore_Query", "Number of documents: " + task.getResult().size());
                        }
                            // Create FirestoreRecyclerOptions inside onComplete
                            FirestoreRecyclerOptions<Student> students = new FirestoreRecyclerOptions.Builder<Student>()
                                    .setQuery(query, Student.class)
                                    .build();

                            // Initialize and set up RecyclerView inside onComplete
                            attendAdapter = new AttendanceAdapter(students);
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            recyclerView.setAdapter(attendAdapter);
                            attendAdapter.startListening();

                            // ... (rest of your code)


                        //Goes to student's attendance record for that module
                        if (attendAdapter != null) {

                            attendAdapter.setOnItemClickListener(new AttendanceAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                                    if(isConnectedtoInternet(requireActivity())){
                                        String studentID = documentSnapshot.getId();

                                        Intent intent = new Intent(getActivity(), PersonalAttendance.class);
                                        intent.putExtra("STU_ID", studentID);
                                        intent.putExtra("UNIT_ID", unit);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Please connect to internet to see student attendance", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }


            //Allows the teacher to set the selected student as absent and updates the student's attendance record
            if (attendAdapter != null) {
            attendAdapter.setOnItemLongClickListener(new AttendanceAdapter.OnItemLongClickListener() {

                @Override
                public void onItemLongClick(DocumentSnapshot documentSnapshot, int position) {

                    if (isConnectedtoInternet(requireActivity())) {


                       final String studentID = documentSnapshot.getId();


                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                        builder.setTitle("Set Attendance");
                        builder.setMessage("Do you want to set the student as absent?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DocumentReference documentReference = db.collection("School")
                                        .document("0DKXnQhueh18DH7TSjsb")
                                        .collection("Attendance")
                                        .document(unit)
                                        .collection("Date")
                                        .document(date)
                                        .collection("Students")
                                        .document(studentID);

                                documentReference.update("attended", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Log.d("ATT", "Attendance updated");

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("ATT", "Error updating field");

                                            }
                                        });

                                //Removes spaces when adding document to the database so it can be queried properly
                                String docName = unit.replaceAll("\\s+", "") + date.replaceAll("\\s+", "");

                                DocumentReference docRef = db.collection("School")
                                        .document("0DKXnQhueh18DH7TSjsb")
                                        .collection("AttendanceRecord")
                                        .document(studentID)
                                        .collection("Records")
                                        .document(docName);

                                docRef.update("attended", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("ATT", "Personal attendance updated");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("ATT", "Failed to update");
                                            }
                                        });
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
                    } else {
                        Toast.makeText(getActivity(), "Please connect to internet to set attendance", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        }


    public static boolean isConnectedtoInternet(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

   @Override
   public void onStart() {
       super.onStart();
       if (attendAdapter != null) {
           attendAdapter.startListening();
       }
   }

    @Override
    public void onStop() {
        super.onStop();
        if (attendAdapter != null) {
            attendAdapter.stopListening();
        }
    }


    public void updateRecyclerView(List<Student> attendedStudentIds) {
        // Update the dataset of your RecyclerView's adapter with the provided list of attended student IDs
        if (attendAdapter != null) {
            attendAdapter.updateData(attendedStudentIds);
            attendAdapter.notifyDataSetChanged(); // Notify the adapter that the dataset has changed
        }
    }
}

    //Starts listening for changes to the RecyclerView when Activity starts
  /*  @Override
    public void onStart() {
        super.onStart();

        if (isConnectedtoInternet(requireActivity())) {
            attendAdapter.startListening();
        }

    }

    //Stops listening for changes to the RecyclerView when Activity stops
    @Override
    public void onStop() {
        super.onStop();
        attendAdapter.stopListening();
    }*/



