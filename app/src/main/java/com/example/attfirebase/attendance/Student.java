//Model class to show a list of the students that have attended a module

package com.example.attfirebase.attendance;

import androidx.annotation.NonNull;

import java.util.Map;

public class Student {

    private  String student_id;
    private String name;


    public Student() {
    }

    public Student(String student_id) {
        this.student_id = student_id;
    }

    public String getStudent_id() {
        return student_id;
    }
    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String studentName) {
        this.name = name;
    }


}
