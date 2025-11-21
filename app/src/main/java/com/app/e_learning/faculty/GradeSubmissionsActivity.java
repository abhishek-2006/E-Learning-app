// GradeSubmissionsActivity.java
package com.app.e_learning.faculty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GradeSubmissionsActivity extends AppCompatActivity {

    private TextView tvStudentAssignmentInfo, tvSubmittedFileName;
    private TextInputEditText etScoreGrade, etFeedbackComments;
    private Button btnViewDownloadFile, btnSaveGrade;

    private String studentId, assignmentId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_grade_submissions);

        // Init views
        tvStudentAssignmentInfo = findViewById(R.id.tv_student_assignment_info);
        tvSubmittedFileName = findViewById(R.id.tv_submitted_file_name);
        etScoreGrade = findViewById(R.id.et_score_grade);
        etFeedbackComments = findViewById(R.id.et_feedback_comments);
        btnViewDownloadFile = findViewById(R.id.btn_view_download_file);
        btnSaveGrade = findViewById(R.id.btn_save_grade);

        db = FirebaseFirestore.getInstance();

        // Get intent extras safely
        studentId = getIntent().getStringExtra("studentId");
        assignmentId = getIntent().getStringExtra("assignmentId");

        if (studentId == null || assignmentId == null) {
            Toast.makeText(this, "Missing student or assignment info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSubmissionData();

        btnViewDownloadFile.setOnClickListener(v -> {
            String fileUrl = tvSubmittedFileName.getTag() != null ? tvSubmittedFileName.getTag().toString() : null;
            if (fileUrl != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No file available", Toast.LENGTH_SHORT).show();
            }
        });

        btnSaveGrade.setOnClickListener(v -> saveGrade());
    }

    private void loadSubmissionData() {
        // Firestore path: submissions/{studentId}/assignments/{assignmentId}
        DocumentReference docRef = db.collection("submissions")
                .document(studentId)
                .collection("assignments")
                .document(assignmentId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String studentName = documentSnapshot.getString("studentName");
                String assignmentTitle = documentSnapshot.getString("assignmentTitle");
                String fileName = documentSnapshot.getString("fileName");
                String fileUrl = documentSnapshot.getString("fileUrl");

                tvStudentAssignmentInfo.setText("For Student: " + studentName + " (Assignment: " + assignmentTitle + ")");
                tvSubmittedFileName.setText(fileName != null ? fileName : "No file submitted");
                tvSubmittedFileName.setTag(fileUrl); // store URL for download
            } else {
                Toast.makeText(this, "No submission found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error loading submission", Toast.LENGTH_SHORT).show());
    }

    private void saveGrade() {
        String score = etScoreGrade.getText().toString().trim();
        String feedback = etFeedbackComments.getText().toString().trim();

        if (score.isEmpty()) {
            etScoreGrade.setError("Enter score/grade");
            etScoreGrade.requestFocus();
            return;
        }

        DocumentReference docRef = db.collection("submissions")
                .document(studentId)
                .collection("assignments")
                .document(assignmentId);

        docRef.update("score", score, "feedback", feedback)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Grade saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving grade", Toast.LENGTH_SHORT).show());
    }
}
