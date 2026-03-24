package com.app.e_learning.student;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.e_learning.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyCoursesActivity extends AppCompatActivity {

    RecyclerView rvCourses;
    TextView tvEmpty;

    FirebaseFirestore db;
    FirebaseAuth auth;

    List<CourseModel> courseList;
    StudentCoursesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_my_courses);

        rvCourses = findViewById(R.id.rv_courses_list);
        tvEmpty = findViewById(R.id.tv_empty_courses);

        rvCourses.setLayoutManager(new LinearLayoutManager(this));

        courseList = new ArrayList<>();
        adapter = new StudentCoursesAdapter(courseList);
        rvCourses.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadCourses();
    }

    private void loadCourses() {

        String uid = auth.getUid();
        if (uid == null) return;

        db.collection("student").document(uid).get().addOnSuccessListener(studentDoc -> {

            if (!studentDoc.exists()) {
                showEmpty();
                return;
            }

            String enrollmentId = studentDoc.getString("enrollment");

            if (enrollmentId == null || enrollmentId.isEmpty()) {
                showEmpty();
                return;
            }

            db.collection("enrollments").document(enrollmentId).get().addOnSuccessListener(enrollmentDoc -> {

                if (!enrollmentDoc.exists()) {
                    showEmpty();
                    return;
                }

                courseList.clear();

                String courseName = enrollmentDoc.getString("courseName");
                String facultyName = enrollmentDoc.getString("facultyName");
                String courseCode = enrollmentDoc.getString("courseCode");

                courseList.add(new CourseModel(courseName, facultyName, courseCode));

                adapter.notifyDataSetChanged();

                rvCourses.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            });
        });
    }

    private void showEmpty() {
        rvCourses.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }
}