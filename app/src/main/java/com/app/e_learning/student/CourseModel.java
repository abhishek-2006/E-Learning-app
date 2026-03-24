package com.app.e_learning.student;

public class CourseModel {

    private String courseName, facultyName, courseCode;

    public CourseModel() {
    }

    public CourseModel(String courseName, String facultyName, String courseCode) {
        this.courseName = courseName;
        this.facultyName = facultyName;
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getCourseCode() {
        return courseCode;
    }
}