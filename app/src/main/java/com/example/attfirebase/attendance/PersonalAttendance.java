package com.example.attfirebase.attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.attfirebase.R;
import com.example.attfirebase.Records.PersonalAttended;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class PersonalAttendance extends AppCompatActivity implements PersonalAttFragInterface  {
    private String unit;
    private String studentID;
    TextView toolbarText;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_att_rv);

        // TabLayout allows the user to switch between the Attended and Absent lists using tabs
        TabLayout tabLayout = findViewById(R.id.personalAttTabs);

// Sets up toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbarText = toolbar.findViewById(R.id.personalToolbarTV);
        setSupportActionBar(toolbar);

// ViewPager2 allows the user to swipe to move from one tab to the other
        ViewPager2 viewPager2 = findViewById(R.id.personalVP);
        ViewPager2Adapter vpAdapter = new ViewPager2Adapter(this);

// Adds fragments. This splits the activity into two lists, Attended and Absent
        vpAdapter.addFragment(PersonalAttendedFragment.newInstance(unit, studentID), "Attended");
        vpAdapter.addFragment(PersonalAbsentFragment.newInstance(unit, studentID), "Absent");

        viewPager2.setAdapter(vpAdapter);

// Set up TabLayoutMediator for ViewPager2 and TabLayout
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) ->
                tab.setText(vpAdapter.getPageTitle(position))
        ).attach();

/*Retrieves the module and student ID from the StudentAttendanceScreen class
This allows the correct attendance records to be retrieved from the Firestore database
 */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        unit = bundle.getString("UNIT_ID");
        studentID = bundle.getString("STU_ID");

        toolbarText.setText(unit);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

   // public Intent getIntent() {
        //return null;
    //}

    /*These methods pass the module and student ID to the Attended and Absent fragments so the
    Firestore database can be queried and personal attendance record can be retrieved
     */
    @Override
    public String getUnitName() {
        return unit;
    }

    @Override
    public String getStudentID() {
        return studentID;
    }
}
