package com.app.e_learning.faculty;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AnnouncementsActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etMessage;
    private Spinner spinnerCourses;
    private Button btnPostAnnouncement;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_announcements);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Views
        etTitle = findViewById(R.id.et_announcement_title);
        etMessage = findViewById(R.id.et_announcement_message);
        spinnerCourses = findViewById(R.id.spinner_courses);
        btnPostAnnouncement = findViewById(R.id.btn_post_announcement);

        btnPostAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAnnouncement();
            }
        });
    }

    private void postAnnouncement() {
        String title = etTitle.getText().toString().trim();
        String message = etMessage.getText().toString().trim();
        String course = spinnerCourses.getSelectedItem().toString();

        if (title.isEmpty()) {
            etTitle.setError("Title required");
            etTitle.requestFocus();
            return;
        }

        if (message.isEmpty()) {
            etMessage.setError("Message required");
            etMessage.requestFocus();
            return;
        }

        Map<String, Object> announcement = new HashMap<>();
        announcement.put("title", title);
        announcement.put("message", message);
        announcement.put("course", course);
        announcement.put("facultyId", auth.getCurrentUser().getUid());
        announcement.put("timestamp", System.currentTimeMillis());

        db.collection("announcements").add(announcement)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AnnouncementsActivity.this, "Announcement posted!", Toast.LENGTH_SHORT).show();
                    etTitle.setText("");
                    etMessage.setText("");
                    spinnerCourses.setSelection(0);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(AnnouncementsActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
