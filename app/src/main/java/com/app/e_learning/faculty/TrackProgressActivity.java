package com.app.e_learning.faculty;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.e_learning.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrackProgressActivity extends AppCompatActivity {

    private Spinner spinnerCourses;
    private Button btnViewStudents;
    private RecyclerView rvStudentList;
    private TextView tvStudentListLabel;

    private FirebaseFirestore db;

    private List<Student> studentList;
    private StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_track_progress);

        spinnerCourses = findViewById(R.id.spinner_courses);
        btnViewStudents = findViewById(R.id.btn_view_students_in_course);
        rvStudentList = findViewById(R.id.rv_student_list);
        tvStudentListLabel = findViewById(R.id.tv_student_list_label);

        db = FirebaseFirestore.getInstance();

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList);
        rvStudentList.setLayoutManager(new LinearLayoutManager(this));
        rvStudentList.setAdapter(adapter);

        btnViewStudents.setOnClickListener(v -> {
            String selectedCourse = spinnerCourses.getSelectedItem().toString();
            if (!selectedCourse.isEmpty()) {
                loadStudents(selectedCourse);
            } else {
                Toast.makeText(this, "Please select a course", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudents(String course) {
        studentList.clear();
        tvStudentListLabel.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();

        db.collection("students")
                .whereEqualTo("course", course)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Map<String, Object> data = doc.getData();
                            String name = (String) data.get("name");
                            String email = (String) data.get("email");
                            studentList.add(new Student(name, email));
                        }
                        tvStudentListLabel.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No students found for " + course, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch students", Toast.LENGTH_SHORT).show());
    }

    // Student model
    public static class Student {
        String name;
        String email;

        public Student(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
