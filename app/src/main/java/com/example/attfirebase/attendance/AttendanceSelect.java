package com.example.attfirebase.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.attfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AttendanceSelect extends AppCompatActivity {

    private Spinner attendanceSpinner;
    private FirebaseFirestore db;
    private Button submitBtn;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_select);

        attendanceSpinner = findViewById(R.id.attendanceSpinner);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        CalendarView calendarView = findViewById(R.id.calendarView);
        submitBtn = findViewById(R.id.submitBtn);

        populateSpinner();


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                //month has to be incremented because it is always 1 behind
                month = month + 1;

                final String datePicked;

                //Adds padding of one zero to the day if its less than 10
                if (dayOfMonth < 10) {
                    datePicked = "0" + dayOfMonth + " " + "0" + month + " " + year;
                    Log.d("ATT_SLCT", datePicked);
                } else {
                    datePicked = dayOfMonth + " " + "0" + month + " " + year;
                }


                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(isConnectedtoInternet(AttendanceSelect.this)) {

                            //  final String spinnerValue = attendanceSpinner.getSelectedItem().toString();
                            // Get the selected item from the Spinner
                         //   Object selectedItem = attendanceSpinner.getSelectedItem();

// Check if the selected item is not null before calling toString()
                           // String spinnerValue = null;
                            //if (selectedItem != null) {
                               // spinnerValue = selectedItem.toString();
                                // Rest of your code using spinnerValue
                           // }

                            //Determines database path for the record of the chosen date
                            //assert spinnerValue != null;
                            final String spinnerValue = attendanceSpinner.getSelectedItem().toString();

                            DocumentReference dateRef = db.collection("School")
                                    .document("0DKXnQhueh18DH7TSjsb")
                                    .collection("Attendance")
                                    .document(spinnerValue)
                                    .collection("Date")
                                    .document(datePicked);

                           // String spinnerValue = SpinnerValue;
                            dateRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();

                                        assert documentSnapshot != null;
                                        if (documentSnapshot.exists()) {

                                            Intent intent = new Intent(AttendanceSelect.this, AttScreen.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("UNIT_ID", spinnerValue);
                                            bundle.putString("DATE_PICKED", datePicked);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(AttendanceSelect.this, "No attendance record for this module on " + datePicked, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(AttendanceSelect.this, "Please connect to internet to see attendance", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

       // return false;
    }

    //public void startActivity(Intent intent) {
    //}

    private void populateSpinner() {

        //Determines database path for the teacher's modules
        CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Units");

        //Prepares spinner
        final List<String> unitsList = new ArrayList<>();
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.units_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        attendanceSpinner.setAdapter(adapter);

        //Searches for modules which has the corresponding teacher ID and adds them to spinner
        moduleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        String unitName = queryDocumentSnapshot.getId();
                        unitsList.add(unitName);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    //public Context getApplicationContext() {
       // return null;
    //}

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

}
