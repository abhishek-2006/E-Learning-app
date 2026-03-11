package com.app.e_learning.student;

public class CourseModel {

    String courseName;
    String facultyName;

    public CourseModel(String courseName, String facultyName) {
        this.courseName = courseName;
        this.facultyName = facultyName;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getFacultyName() {
        return facultyName;
    }
}