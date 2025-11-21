package com.app.e_learning.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class StudentDashboardActivity extends AppCompatActivity {

    private LinearLayout llMyCourses, llStudyMaterials, llAssignments, llMyProgress;
    private Button btnEditProfile, btnLogout;
    private TextView tvWelcomeStudent;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvWelcomeStudent = findViewById(R.id.tv_welcome_student);
        llMyCourses = findViewById(R.id.ll_my_courses);
        llStudyMaterials = findViewById(R.id.ll_study_materials);
        llAssignments = findViewById(R.id.ll_assignments);
        llMyProgress = findViewById(R.id.ll_my_progress);

        // Main buttons
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnLogout = findViewById(R.id.btn_logout);

        loadStudentInfo();

        // My Courses
        llMyCourses.setOnClickListener(v -> {
            startActivity(new Intent(StudentDashboardActivity.this, MyCoursesActivity.class));
        });

        // Study Materials
        llStudyMaterials.setOnClickListener(v -> {
            startActivity(new Intent(StudentDashboardActivity.this, StudyMaterialsActivity.class));
        });

        // Assignments
        llAssignments.setOnClickListener(v -> {
            startActivity(new Intent(StudentDashboardActivity.this, AssignmentsActivity.class));
        });

        // My Progress
        llMyProgress.setOnClickListener(v -> {
            startActivity(new Intent(StudentDashboardActivity.this, ProgressActivity.class));
        });

        // Edit Profile
        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(StudentDashboardActivity.this, StudentEditProfileActivity.class));
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            auth.signOut();

            Intent intent = new Intent(StudentDashboardActivity.this, com.app.e_learning.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadStudentInfo() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("student").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                tvWelcomeStudent.setText("Welcome, " + name);
            }
        });
    }
}
