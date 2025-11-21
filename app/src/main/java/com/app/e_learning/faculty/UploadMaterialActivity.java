// UploadMaterialActivity.java
package com.app.e_learning.faculty;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UploadMaterialActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 101;

    private TextView tvSelectedFileName;
    private Button btnSelectFile, btnUploadMaterial;
    private Spinner spinnerCourses;

    private Uri fileUri;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_upload_material);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Views
        tvSelectedFileName = findViewById(R.id.tv_selected_file_name);
        btnSelectFile = findViewById(R.id.btn_select_file);
        btnUploadMaterial = findViewById(R.id.btn_upload_material);
        spinnerCourses = findViewById(R.id.spinner_courses);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.course_subjects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapter);

        // Select file
        btnSelectFile.setOnClickListener(v -> openFileChooser());

        // Upload material
        btnUploadMaterial.setOnClickListener(v -> uploadMaterial());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            String fileName = fileUri.getLastPathSegment();
            tvSelectedFileName.setText(fileName);
        }
    }

    private void uploadMaterial() {
        if (fileUri == null) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
            return;
        }

        String course = spinnerCourses.getSelectedItem().toString();
        String uid = auth.getCurrentUser().getUid();

        // Upload file to Firebase Storage
        StorageReference fileRef = storageRef.child("study_materials/" + course + "/" + fileUri.getLastPathSegment());
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Save metadata in Firestore
                    Map<String, Object> material = new HashMap<>();
                    material.put("facultyId", uid);
                    material.put("course", course);
                    material.put("fileUrl", uri.toString());
                    material.put("fileName", fileUri.getLastPathSegment());

                    db.collection("study_materials").document(course)
                            .collection("materials").add(material)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(UploadMaterialActivity.this, "Material uploaded successfully", Toast.LENGTH_SHORT).show();
                                tvSelectedFileName.setText(getString(R.string.no_file_chosen));
                                fileUri = null;
                            })
                            .addOnFailureListener(e -> Toast.makeText(UploadMaterialActivity.this, "Failed to upload material", Toast.LENGTH_SHORT).show());
                }))
                .addOnFailureListener(e -> Toast.makeText(UploadMaterialActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
