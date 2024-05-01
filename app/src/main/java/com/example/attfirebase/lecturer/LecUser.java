package com.example.attfirebase.lecturer;

public class LecUser {
    private String teacher_id;
    private String name;

    public LecUser(String teacher_id, String name) {
        this.teacher_id = teacher_id;
        this.name = name;
    }

    public LecUser() {
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public String getName() {
        return name;
    }
}


