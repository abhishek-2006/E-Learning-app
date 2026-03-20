package com.app.e_learning.admin;

import java.util.HashMap;
import java.util.Map;

public class Course {
    private String id;
    private String facultyName;
    private String courseName;
    private String courseCode;

    // Required no-argument constructor for Firebase
    public Course() {
    }

    public Course(String facultyName, String courseName, String courseCode) {
        this.facultyName = facultyName;
        this.courseName = courseName;
        this.courseCode = courseCode;
    }

    // Getters and Setters (omitted for brevity)
    public String getId() {
        return id;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Helper method to convert to a Firestore-friendly Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("facultyName", facultyName);
        map.put("courseName", courseName);
        map.put("courseCode", courseCode);

        return map;
    }
}