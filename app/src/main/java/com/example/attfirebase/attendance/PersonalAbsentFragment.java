package com.example.attfirebase.attendance;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Objects;



public class PersonalAbsentFragment extends Fragment {

    private String unit;
    private String studentID;
    private PersonalAttendanceAdapter adapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;



    public PersonalAbsentFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String unit, String studentID) { PersonalAbsentFragment fragment = new PersonalAbsentFragment();
        Bundle args = new Bundle();
        args.putString("UNIT_ID", unit);
        args.putString("STU_ID", studentID);
        fragment.setArguments(args);
        return fragment;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_personal_absent, container, false);
        recyclerView = v.findViewById(R.id.personalAbsentRV);

        db = FirebaseFirestore.getInstance();

        PersonalAttFragInterface activity = (PersonalAttFragInterface) getActivity();
        assert activity != null;
        unit = activity.getUnitName();
        studentID = activity.getStudentID();

        getDates();
        if (getArguments() != null) {
            unit = getArguments().getString("UNIT_ID");
            studentID = getArguments().getString("STU_ID");
        }

        return v;
    }

    private void getDates() {


        if (studentID != null) {
            //Determines database path for student's personal attendance records
            CollectionReference recordRef = db.collection("School")
                    .document("0DKXnQhueh18DH7TSjsb")
                    .collection("AttendanceRecord")
                    .document(studentID)
                    .collection("Records");
            // Proceed with using recordRef

            final Query query = recordRef.whereEqualTo("unit", unit).whereEqualTo("attended", false);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                            String date = queryDocumentSnapshot.getString("date");

                        }
                    } else {
                        Toast.makeText(getActivity(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });


            //Builds RecyclerView
            FirestoreRecyclerOptions<Date> dates = new FirestoreRecyclerOptions.Builder<Date>()
                    .setQuery(query, Date.class)
                    .build();

            adapter = new PersonalAttendanceAdapter(dates);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

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

   /* @Override
    public void onStart() {
        super.onStart();

        if(isConnectedtoInternet(requireActivity())){
            adapter.startListening();
        }
    }*/

   /* @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/






}