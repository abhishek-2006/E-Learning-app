// FacultyDashboardActivity.java
package com.app.e_learning.faculty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FacultyDashboardActivity extends AppCompatActivity {

    private TextView tvFacultyName, tvFacultyEmail;
    private Button btnEditProfile, btnUploadVideo, btnUploadMaterial, btnAssignmentsQuiz,
            btnGradeSubmissions, btnTrackProgress, btnAnnouncements, btnLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Views
        tvFacultyName = findViewById(R.id.tv_faculty_name);
        tvFacultyEmail = findViewById(R.id.tv_faculty_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnUploadVideo = findViewById(R.id.btn_upload_video);
        btnUploadMaterial = findViewById(R.id.btn_upload_material);
        btnAssignmentsQuiz = findViewById(R.id.btn_assignments_quiz);
        btnGradeSubmissions = findViewById(R.id.btn_grade_submissions);
        btnTrackProgress = findViewById(R.id.btn_track_progress);
        btnAnnouncements = findViewById(R.id.btn_announcements);
        btnLogout = findViewById(R.id.btn_logout);

        // Load Faculty info
        loadFacultyInfo();

        // Redirect logic
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FacultyDashboardActivity.this, FacultyEditProfileActivity.class);
            startActivity(intent);
        });


        btnUploadVideo.setOnClickListener(v -> startActivity(new Intent(this, UploadVideoActivity.class)));
        btnUploadMaterial.setOnClickListener(v -> startActivity(new Intent(this, UploadMaterialActivity.class)));
        btnAssignmentsQuiz.setOnClickListener(v -> startActivity(new Intent(this, AssignmentsQuizActivity.class)));
        btnGradeSubmissions.setOnClickListener(v -> startActivity(new Intent(this, GradeSubmissionsActivity.class)));

        btnTrackProgress.setOnClickListener(v -> startActivity(new Intent(this, TrackProgressActivity.class)));
        btnAnnouncements.setOnClickListener(v -> startActivity(new Intent(this, AnnouncementsActivity.class)));

        // Logout
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(FacultyDashboardActivity.this, com.app.e_learning.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadFacultyInfo() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("faculty").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                tvFacultyName.setText(documentSnapshot.getString("name"));
                tvFacultyEmail.setText(documentSnapshot.getString("email"));
            }
        });
    }
}
