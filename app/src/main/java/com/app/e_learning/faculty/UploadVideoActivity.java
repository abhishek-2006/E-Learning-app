package com.app.e_learning.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UploadVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;

    private TextInputEditText etVideoTitle, etVideoDescription;
    private Spinner spinnerCourses;
    private TextView tvSelectedFile;
    private Button btnSelectFile, btnUpload;

    private Uri videoUri;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        etVideoTitle = findViewById(R.id.et_video_title);
        etVideoDescription = findViewById(R.id.et_video_description);
        spinnerCourses = findViewById(R.id.spinner_courses);
        tvSelectedFile = findViewById(R.id.tv_selected_file_name);
        btnSelectFile = findViewById(R.id.btn_select_file);
        btnUpload = findViewById(R.id.btn_upload_lecture);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // File Picker
        btnSelectFile.setOnClickListener(v -> openFileChooser());

        // Upload Video
        btnUpload.setOnClickListener(v -> {
            if (videoUri != null) {
                uploadVideo();
            } else {
                Toast.makeText(this, "Please select a video first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            tvSelectedFile.setText(videoUri.getLastPathSegment());
        }
    }

    private void uploadVideo() {
        String title = etVideoTitle.getText().toString().trim();
        String description = etVideoDescription.getText().toString().trim();
        String course = spinnerCourses.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and description required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Uploading Video...");
        progressDialog.show();

        // Save in Firebase Storage
        StorageReference fileRef = storage.getReference("videos/" + System.currentTimeMillis() + ".mp4");
        UploadTask uploadTask = fileRef.putFile(videoUri);

        uploadTask.addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Save metadata in Firestore
                    Map<String, Object> videoData = new HashMap<>();
                    videoData.put("title", title);
                    videoData.put("description", description);
                    videoData.put("course", course);
                    videoData.put("videoUrl", uri.toString());
                    videoData.put("uploadedBy", "faculty"); // You can pass faculty ID/email here
                    videoData.put("timestamp", System.currentTimeMillis());

                    db.collection("videos")
                            .add(videoData)
                            .addOnSuccessListener(documentReference -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Failed to save video metadata", Toast.LENGTH_SHORT).show();
                            });
                })
        ).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
