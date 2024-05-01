package com.example.attfirebase.attendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.attfirebase.R;
import com.example.attfirebase.student.AttendanceScreen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AttScreen extends AppCompatActivity implements AttFragInterface {

    public String unit;
    public String date;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_recyclerview);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();


        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarText = toolbar.findViewById(R.id.attendanceToolbarTV);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        ViewPager2Adapter vpAdapter = new ViewPager2Adapter(this);

        vpAdapter.addFragment(new AttendedFragment(), "Attended");
        vpAdapter.addFragment(new AbsentFragment(), "Absent");

        viewPager2.setAdapter(vpAdapter);
        // Set up TabLayoutMediator for ViewPager2 and TabLayout
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) ->
                tab.setText(vpAdapter.getPageTitle(position))
        ).attach();



        //Retrieves module and date from AttendanceSelect
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        unit = bundle.getString("UNIT_ID");
        date = bundle.getString("DATE_PICKED");

        // Adding log messages to check if unit and date are retrieved successfully
        Log.d("AttendanceSelect", "Unit: " + unit);
        Log.d("AttendanceSelect", "Selected Date: " + date);

        toolbarText.setText(unit);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //FloatingActionButton functions as an info box
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AttScreen.this);
                builder.setTitle("Handling Clicks");
                builder.setMessage("Click on a student to see their attendance for the selected module. Hold down on a student to set them as present/absent.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

  //  public Intent getIntent() {
       // return null;
   // }



    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String getDate() {
        return date;
    }
}