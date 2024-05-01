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
import androidx.fragment.app.FragmentActivity;
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
import java.util.Objects;


public class AbsentFragment extends Fragment {
    private FirebaseFirestore db;
    public AttendanceAdapter attendAdapter;
    private RecyclerView recyclerView;

    private String unit;
    private String date;


    public AbsentFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_absent, container, false);
        recyclerView = v.findViewById(R.id.absentRV);

        //Retrieves module and date from the AttendanceScreen class through an interface
        AttFragInterface activity = (AttFragInterface) getActivity();
        assert activity != null;
        unit = activity.getUnit();
        date = activity.getDate();

        getStudents();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
    }

    private void getStudents() {
        if (unit != null) {

            CollectionReference unitsRef = db.collection("School")
                    .document("0DKXnQhueh18DH7TSjsb")
                    .collection("Attendance")
                    .document(unit)
                    .collection("Date")
                    .document(date)
                    .collection("Students");


            Query query = unitsRef.whereEqualTo("attended", false);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                            // Create a list to store student IDs
                            List<String> studentIds = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String studentId = document.getString("student_id");
                                studentIds.add(studentId); // Add student ID to the list
                                Log.d("Firestore_Query", "Student ID: " + studentId);
                                Log.d("Firestore_Query", "Number of documents: " + task.getResult().size());
                            }
                            FirestoreRecyclerOptions<Student> students = new FirestoreRecyclerOptions.Builder<Student>()
                                    .setQuery(query, Student.class)
                                    .build();

                            attendAdapter = new AttendanceAdapter(students);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(attendAdapter);
                            attendAdapter.startListening();
                           // attendAdapter.notifyDataSetChanged();
                            //Shows overall student attendance for that module
                            if (attendAdapter != null) {
                                attendAdapter.setOnItemClickListener(new AttendanceAdapter.OnItemClickListener() {


                                    @Override
                                    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                                        if (isConnectedtoInternet(requireActivity())) {
                                            String studentID = documentSnapshot.getId();

                                            Intent intent = new Intent(getActivity(), PersonalAttendance.class);
                                            intent.putExtra("STU_ID", studentID);
                                            intent.putExtra("UNIT_ID", unit);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getActivity(), "Please connect to internet to see student attendance", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }


                        }

                    } else {
                        Log.d("ATT_SCREEN", "Something went wrong");
                    }
                }
            });



            if (attendAdapter != null) {


                //Allows the teacher to manually set the user as present and updates their attendance record
                attendAdapter.setOnItemLongClickListener(new AttendanceAdapter.OnItemLongClickListener() {


                    @Override
                    public void onItemLongClick(DocumentSnapshot documentSnapshot, int position) {

                        if (isConnectedtoInternet(requireActivity())) {


                            final String studentID = documentSnapshot.getId();


                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                            builder.setTitle("Set Attendance");
                            builder.setMessage("Do you want to set the student as present?");
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

                                    documentReference.update("attended", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Log.d("ATT", "Success");

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Error updating database", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    String docName = unit.replaceAll("\\s+", "") + date.replaceAll("\\s+", "");

                                    DocumentReference docRef = db.collection("School")
                                            .document("0DKXnQhueh18DH7TSjsb")
                                            .collection("AttendanceRecord")
                                            .document(studentID)
                                            .collection("Records")
                                            .document(docName);

                                    docRef.update("attended", true).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }

    public static boolean isConnectedtoInternet(@NonNull FragmentActivity context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void updateRecyclerView(List<Student> absentStudentIds) {
        // Update the dataset of your RecyclerView's adapter with the provided list of attended student IDs
        if (attendAdapter != null) {
            attendAdapter.updateData(absentStudentIds);
            attendAdapter.notifyDataSetChanged(); // Notify the adapter that the dataset has changed
        }
    }

    

   /* public void updateRecyclerView(List<String> absentStudentIds) {
        if (attendAdapter != null) { // Check if adapter is not null
            // Update adapter with new data
            attendAdapter.setStudentIds(absentStudentIds);
            // Notify adapter that data set has changed
            attendAdapter.notifyDataSetChanged();
        } else {
            Log.e("AttendedFragment", "Adapter is null");
        }
    }*/
}


   /* @Override
    public void onStart() {
        super.onStart();

        if(isConnectedtoInternet(requireActivity())){
            attendAdapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        attendAdapter.stopListening();
    }*/

