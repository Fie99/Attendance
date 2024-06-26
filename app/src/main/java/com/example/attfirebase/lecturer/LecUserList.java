package com.example.attfirebase.lecturer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attfirebase.databinding.ActivityLecUserListBinding;

import com.example.attfirebase.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LecUserList extends AppCompatActivity {
    private LecUserAdapter adapter;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lec_user_rv);

        Toolbar toolbar = findViewById(R.id.toolbarTeachers);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        db = FirebaseFirestore.getInstance();

        getTeachers();

        if(!isConnectedtoInternet(LecUserList.this)){
            Toast.makeText(LecUserList.this, "Please connect to internet to see list", Toast.LENGTH_SHORT).show();

        }
    }

    private void getTeachers() {

        CollectionReference userRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User");

        Query query = userRef.orderBy("teacher_id", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    Objects.requireNonNull(task.getResult());
                }
                else {
                    Toast.makeText(LecUserList.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        FirestoreRecyclerOptions<LecUser> teachers = new FirestoreRecyclerOptions.Builder<LecUser>()
                .setQuery(query, LecUser.class)
                .build();


        adapter = new LecUserAdapter(teachers);
        RecyclerView recyclerView = findViewById(R.id.teacherUserRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(LecUserList.this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static boolean isConnectedtoInternet(@NonNull LecUserList context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
        {
            Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public Object getSystemService(String connectivityService) {
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isConnectedtoInternet(LecUserList.this))
        {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
