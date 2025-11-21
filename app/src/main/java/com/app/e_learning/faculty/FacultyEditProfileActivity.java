package com.app.e_learning.faculty;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.android.material.textfield.TextInputEditText;

public class FacultyEditProfileActivity extends AppCompatActivity {

    private static final String PREF_NAME = "E_Learning_Prefs";
    private TextInputEditText etName, etEmail, etMobile, etDepartment, etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Button btnSaveChanges;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_edit_profile);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etMobile = findViewById(R.id.et_mobile);
        etDepartment = findViewById(R.id.et_department);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        btnSaveChanges = findViewById(R.id.btn_save_changes);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Load existing data
        etName.setText(sharedPreferences.getString("faculty_name", ""));
        etEmail.setText(sharedPreferences.getString("faculty_email", ""));
        etMobile.setText(sharedPreferences.getString("faculty_mobile", ""));
        etDepartment.setText(sharedPreferences.getString("faculty_department", ""));

        btnSaveChanges.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String department = etDepartment.getText().toString().trim();
            String currentPassword = etCurrentPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();
            String confirmNewPassword = etConfirmNewPassword.getText().toString();

            if (name.isEmpty() || mobile.isEmpty() || department.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update password if fields are filled
            if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmNewPassword.isEmpty()) {
                String savedPassword = sharedPreferences.getString("faculty_password", "");
                if (!currentPassword.equals(savedPassword)) {
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPassword.equals(confirmNewPassword)) {
                    Toast.makeText(this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Save new password
                sharedPreferences.edit().putString("faculty_password", newPassword).apply();
            }

            // Save updated info
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("faculty_name", name);
            editor.putString("faculty_mobile", mobile);
            editor.putString("faculty_department", department);
            editor.apply();

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
