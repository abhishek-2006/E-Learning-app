package com.app.e_learning.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class CourseOversightActivity extends AppCompatActivity
        implements CourseRepository.CourseDataListener {

    private CourseRepository repository;
    private ListenerRegistration courseListenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_admin_course_oversight); // Your XML layout

        repository = new CourseRepository();

        // 1. Start listening for real-time course updates
        courseListenerRegistration = repository.listenForCourses(this);

        // Example: Add a new course on button click
        // findViewById(R.id.btn_add_new_course).setOnClickListener(v -> handleAddNewCourse());
    }

    private void handleAddNewCourse() {
        String name = "New Maths Course"; // Get from EditText
        String desc = "Advanced Calculus"; // Get from EditText
        Course newCourse = new Course(name, desc);

        repository.addCourse(newCourse, new CourseRepository.OnCompleteListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(CourseOversightActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CourseOversightActivity.this, "Error adding course: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Admin", "Failed to add course", e);
            }
        });
    }

    // 2. Implementation of the CourseDataListener Interface
    @Override
    public void onCoursesLoaded(List<Course> courses) {
        // This method is called every time the data in Firestore changes.
        Log.d("Admin", "Courses loaded: " + courses.size());
        // TODO: Update your RecyclerView Adapter with the new list of 'courses'.
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("Admin", "Error loading courses", e);
        Toast.makeText(this, "Failed to load course list.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 3. IMPORTANT: Stop listening when the Activity is destroyed to prevent memory leaks
        if (courseListenerRegistration != null) {
            courseListenerRegistration.remove();
        }
    }
}