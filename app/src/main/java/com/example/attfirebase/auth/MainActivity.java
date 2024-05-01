package com.example.attfirebase.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.attfirebase.R;

public class MainActivity extends AppCompatActivity {
    public TextView welcome, attendance;

    public static int splash_timeout = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcome = findViewById(R.id.tv_welcome);
        attendance = findViewById(R.id.tv_attendance);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splash = new Intent(MainActivity.this, Login.class);
                startActivity(splash);
                finish();
            }
        }, splash_timeout);

        Animation myAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation2);
        welcome.startAnimation(myAnimation);
        Animation myAnimation2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation1);
        attendance.startAnimation(myAnimation2);
        //return false;
    }



}
