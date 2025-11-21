package com.app.e_learning.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddFacultyActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etDepartment, etPassword, etConfirmPassword;
    private Button btnRegister;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_faculty);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Views
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email_address);
        etDepartment = findViewById(R.id.et_department);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> registerFaculty());
    }

    private void registerFaculty() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(department) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Create user in Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Save faculty data in Firestore
                    Map<String, Object> faculty = new HashMap<>();
                    faculty.put("uid", uid);
                    faculty.put("name", name);
                    faculty.put("email", email);
                    faculty.put("department", department);
                    faculty.put("role", "faculty");

                    db.collection("faculty").document(uid).set(faculty)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Faculty registered successfully!", Toast.LENGTH_SHORT).show();
                                clearFields();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Auth error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void clearFields() {
        etFullName.setText("");
        etEmail.setText("");
        etDepartment.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }
}
