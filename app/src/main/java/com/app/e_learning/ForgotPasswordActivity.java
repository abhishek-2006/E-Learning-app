// ForgotPasswordActivity.java
package com.app.e_learning;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnResetPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.et_email);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        auth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Enter your registered email");
                etEmail.requestFocus();
                return;
            }

            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your email", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
