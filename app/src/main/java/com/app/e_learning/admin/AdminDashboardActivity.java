package com.app.e_learning.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private LinearLayout llTeacherOnboarding, llAddFaculty, llCourseOversight, llStudentMonitoring, llComplaintFeedback, llReports;
    private Button btn_logout;
    private TextView tvAdminTitle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvAdminTitle = findViewById(R.id.tv_admin_title);
        llAddFaculty = findViewById(R.id.ll_add_faculty);
        llCourseOversight = findViewById(R.id.ll_course_oversight);
        llStudentMonitoring = findViewById(R.id.ll_student_monitoring);
        llComplaintFeedback = findViewById(R.id.ll_complaint_feedback);
        llReports = findViewById(R.id.ll_reports);
        btn_logout = findViewById(R.id.btn_logout);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String adminName = prefs.getString("adminName", "Admin");
        tvAdminTitle.setText("Welcome, " + adminName);

        llAddFaculty.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AddFacultyActivity.class));
        });

        llCourseOversight.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, CourseOversightActivity.class));
        });

        llStudentMonitoring.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, StudentMonitoringActivity.class));
        });

        llComplaintFeedback.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ComplaintFeedbackActivity.class));
        });

        llReports.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ReportsActivity.class));
        });

        btn_logout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(AdminDashboardActivity.this, com.app.e_learning.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
