package com.app.e_learning.faculty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssignmentsQuizActivity extends AppCompatActivity {

    private Spinner spinnerCourses;
    private ListView listViewSubmissions;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private List<String> studentNames; // Display names
    private List<String> studentIds;   // Document IDs
    private List<String> assignmentIds; // Assignment doc IDs
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_assignments_quiz);

        spinnerCourses = findViewById(R.id.spinner_courses);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        studentNames = new ArrayList<>();
        studentIds = new ArrayList<>();
        assignmentIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentNames);
        listViewSubmissions.setAdapter(adapter);

        // On course selection
        spinnerCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String course = parent.getItemAtPosition(position).toString();
                loadSubmissions(course);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // On clicking a student submission
        listViewSubmissions.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(AssignmentsQuizActivity.this, GradeSubmissionsActivity.class);
            intent.putExtra("studentId", studentIds.get(position));
            intent.putExtra("assignmentId", assignmentIds.get(position));
            startActivity(intent);
        });
    }

    private void loadSubmissions(String course) {
        studentNames.clear();
        studentIds.clear();
        assignmentIds.clear();
        adapter.notifyDataSetChanged();

        db.collection("submissions")
                .whereEqualTo("course", course)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Map<String, Object> submission = doc.getData();
                            studentNames.add((String) submission.get("studentName"));
                            studentIds.add(doc.getString("studentId")); // store student doc ID
                            assignmentIds.add(doc.getId());             // store assignment doc ID
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No submissions for this course", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load submissions", Toast.LENGTH_SHORT).show());
    }
}
