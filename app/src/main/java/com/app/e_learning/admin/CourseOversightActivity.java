package com.app.e_learning.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.e_learning.R;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class CourseOversightActivity extends AppCompatActivity
        implements CourseRepository.CourseDataListener {

    TextView tvEmpty;

    private RecyclerView rvCourses;
    private Button btnAddCourse;

    private CourseAdapter adapter;
    private List<Course> courseList;

    private CourseRepository repository;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_course_oversight);

        rvCourses = findViewById(R.id.rv_courses_list);
        btnAddCourse = findViewById(R.id.btn_add_new_course);
        tvEmpty = findViewById(R.id.tv_empty);

        courseList = new ArrayList<>();
        adapter = new CourseAdapter(courseList, new CourseAdapter.OnCourseClickListener() {
            @Override
            public void onEdit(Course course) {
                showEditDialog(course);
            }

            @Override
            public void onDelete(Course course) {
                showDeleteDialog(course);
            }
        });

        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);

        repository = new CourseRepository();
        listenerRegistration = repository.listenForCourses(this);
        btnAddCourse.setOnClickListener(v -> showAddCourseDialog());
    }

    private void showAddCourseDialog() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_course, null);

        EditText etFaculty = view.findViewById(R.id.et_faculty_name);
        EditText etCourse = view.findViewById(R.id.et_course_name);
        EditText etCode = view.findViewById(R.id.et_course_code);

        new AlertDialog.Builder(this)
                .setTitle("Add Course")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {

                    String faculty = etFaculty.getText().toString().trim();
                    String course = etCourse.getText().toString().trim();
                    String code = etCode.getText().toString().trim();

                    if (faculty.isEmpty() || course.isEmpty() || code.isEmpty()) {
                        Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Course newCourse = new Course(faculty, course, code);

                    repository.addCourse(newCourse, new CourseRepository.OnCompleteListener() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(CourseOversightActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(CourseOversightActivity.this,
                                    "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(Course course) {

        View view = getLayoutInflater().inflate(R.layout.dialog_add_course, null);

        EditText etFaculty = view.findViewById(R.id.et_faculty_name);
        EditText etCourse = view.findViewById(R.id.et_course_name);
        EditText etCode = view.findViewById(R.id.et_course_code);

        // Pre-fill data
        etFaculty.setText(course.getFacultyName());
        etCourse.setText(course.getCourseName());
        etCode.setText(course.getCourseCode());

        new AlertDialog.Builder(this)
                .setTitle("Edit Course")
                .setView(view)
                .setPositiveButton("Update", (dialog, which) -> {

                    String id = course.getId();

                    Course updatedCourse = new Course(
                            etFaculty.getText().toString(),
                            etCourse.getText().toString(),
                            etCode.getText().toString()
                    );
                    updatedCourse.setId(id);

                    repository.updateCourse(course, new CourseRepository.OnCompleteListener() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(CourseOversightActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(CourseOversightActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onCoursesLoaded(List<Course> courses) {
        if (courses.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        courseList.clear();
        courseList.addAll(courses);
        adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(Course course) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (d, w) -> {

                    repository.deleteCourse(course.getId(),
                            new CourseRepository.OnCompleteListener() {
                                @Override
                                public void onSuccess(String message) {
                                    Toast.makeText(CourseOversightActivity.this, message, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(CourseOversightActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onFailure(Exception e) {
        Toast.makeText(this, "Failed to load courses", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}