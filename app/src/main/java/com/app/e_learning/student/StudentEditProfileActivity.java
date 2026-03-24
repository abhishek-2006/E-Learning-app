package com.app.e_learning.student;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudentEditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etEnrollment, etEmail, etMobile;
    private Button btnSave;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_profile);

        // Initialization
        etName = findViewById(R.id.et_name);
        etEnrollment = findViewById(R.id.et_enrollment);
        etEmail = findViewById(R.id.et_email);
        etMobile = findViewById(R.id.et_mobile);
        btnSave = findViewById(R.id.btn_save_profile);
        progressBar = findViewById(R.id.progress_bar);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            loadStudentData();
        }

        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void loadStudentData() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("student").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        etName.setText(documentSnapshot.getString("name"));
                        etEmail.setText(documentSnapshot.getString("email"));
                        etMobile.setText(documentSnapshot.getString("mobile"));
                        etEnrollment.setText(documentSnapshot.getString("enrollment"));
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String enrollment = etEnrollment.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();


        if (name.isEmpty() || enrollment.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("enrollment", enrollment);
        updates.put("mobile", mobile);
        updates.put("email", email);

        db.collection("student").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}