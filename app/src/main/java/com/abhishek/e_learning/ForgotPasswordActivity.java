package com.abhishek.e_learning;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.et_email);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progress_bar); // Make sure to add this to XML

        auth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        // 1. Basic Validation
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        // 2. Format Validation (Check if it's a real email address)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please provide valid email");
            etEmail.requestFocus();
            return;
        }

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        btnResetPassword.setEnabled(false);

        // 3. Firebase Implementation
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Check your inbox for the reset link!", Toast.LENGTH_LONG).show();
                        finish(); // Go back to Login screen
                    } else {
                        // Handle specific errors (e.g., user not found)
                        String error = task.getException() != null ? task.getException().getMessage() : "Failed to send link";
                        Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
