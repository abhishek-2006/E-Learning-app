package com.app.e_learning;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.admin.AdminDashboardActivity;
import com.app.e_learning.faculty.FacultyDashboardActivity;
import com.app.e_learning.student.StudentDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // blank layout

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            checkUserRole(user.getUid());
        } else {
            // Not logged in, go to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void checkUserRole(String uid) {
        // Check student collection first
        db.collection("student").document(uid).get().addOnSuccessListener(studentDoc -> {
            if (studentDoc.exists()) {
                startActivity(new Intent(this, StudentDashboardActivity.class));
                finish();
            } else {
                // Check faculty collection
                db.collection("faculty").document(uid).get().addOnSuccessListener(facDoc -> {
                    if (facDoc.exists()) {
                        startActivity(new Intent(this, FacultyDashboardActivity.class));
                        finish();
                    } else {
                        // Check admin collection
                        db.collection("admin").document(uid).get().addOnSuccessListener(adminDoc -> {
                            if (adminDoc.exists()) {
                                startActivity(new Intent(this, AdminDashboardActivity.class));
                                finish();
                            } else {
                                // User not found in any collection, logout
                                auth.signOut();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(e -> redirectToLogin());
                    }
                }).addOnFailureListener(e -> redirectToLogin());
            }
        }).addOnFailureListener(e -> redirectToLogin());
    }

    private void redirectToLogin() {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}