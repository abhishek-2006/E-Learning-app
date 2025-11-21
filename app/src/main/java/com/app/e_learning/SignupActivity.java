package com.app.e_learning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etMobile, etEnrollment;
    private RadioGroup rgGender;
    private Button btnSignup;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Firebase
        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("en");
        db = FirebaseFirestore.getInstance();

        // Views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etMobile = findViewById(R.id.etMobile);
        etEnrollment = findViewById(R.id.etEnrollment);
        rgGender = findViewById(R.id.rgGender);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> registerStudent());

        TextView tvLogin = findViewById(R.id.login);
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // optional: close signup so user can't go back
        });

    }

    private void registerStudent() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String enrollment = etEnrollment.getText().toString().trim();

        // Gender selection
        int genderId = rgGender.getCheckedRadioButtonId();
        if (genderId == -1) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        String gender = ((RadioButton) findViewById(genderId)).getText().toString();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || mobile.isEmpty() || enrollment.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!enrollment.matches("\\d+")) {
            Toast.makeText(this, "Enrollment must contain numbers only", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Firebase Auth user
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    Log.d("SignupDebug", "Auth Success, UID: " + userId);

                    // Prepare student data
                    Map<String, Object> studentMap = new HashMap<>();
                    studentMap.put("name", name);
                    studentMap.put("email", email);
                    studentMap.put("gender", gender);
                    studentMap.put("mobile", mobile);
                    studentMap.put("enrollment", enrollment);


                    // Add to Firestore collection "student"
                    db.collection("student").document(userId)
                            .set(studentMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(SignupActivity.this, "Signup Successful! Please login.", Toast.LENGTH_SHORT).show();
                                Log.d("SignupDebug", "Firestore Inserted Successfully");

                                // Redirect to login page
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(SignupActivity.this, "DB Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("SignupDebug", "Firestore Error", e);
                            });
                }
            } else {
                Toast.makeText(SignupActivity.this, "Signup Failed: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                Log.e("SignupDebug", "Auth Error", authTask.getException());
            }
        });
    }
}
