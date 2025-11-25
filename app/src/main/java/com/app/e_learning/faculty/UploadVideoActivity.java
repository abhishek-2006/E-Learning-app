package com.app.e_learning.faculty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// NOTE ON FIREBASE PATHS:
// For this single Java file, we must assume a fixed APP_ID constant is available.
// In a real Canvas environment, you would retrieve the actual __app_id dynamically.
public class UploadVideoActivity extends AppCompatActivity {

    // --- Global Canvas Constants (Mocked for Demo) ---
    // This value must be replaced with the actual runtime value of __app_id
    private static final String APP_ID = "academia-lms-default";

    private static final String VIDEO_MIME_TYPE = "video/*";

    private TextInputEditText etVideoTitle, etVideoDescription;
    private Spinner spinnerCourses;
    private TextView tvSelectedFile;
    private Button btnSelectFile, btnUpload;

    private Uri videoUri;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    // Modern Progress Indicator replacement
    private AlertDialog loadingDialog;

    // Modern Activity Result API Launcher
    private ActivityResultLauncher<Intent> videoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_upload_video);

        // 1. Initialize Views
        etVideoTitle = findViewById(R.id.et_video_title);
        etVideoDescription = findViewById(R.id.et_video_description);
        spinnerCourses = findViewById(R.id.spinner_courses);
        tvSelectedFile = findViewById(R.id.tv_selected_file_name);
        btnSelectFile = findViewById(R.id.btn_select_file);
        btnUpload = findViewById(R.id.btn_upload_lecture);

        // 2. Initialize Firebase Instances
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        // 3. Initialize UI Components
        setupCourseSpinner();
        setupVideoPickerLauncher();
        loadingDialog = createLoadingDialog(this, "Uploading Video...");

        // 4. Setup Listeners
        btnSelectFile.setOnClickListener(v -> openFileChooser());

        btnUpload.setOnClickListener(v -> {
            if (videoUri != null) {
                uploadVideo();
            } else {
                Toast.makeText(this, "Please select a video first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initializes the Activity Result Launcher for picking video files.
     * Replaces the deprecated onActivityResult method.
     */
    private void setupVideoPickerLauncher() {
        videoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        videoUri = result.getData().getData();

                        // Display filename (or path segment)
                        String path = videoUri.getLastPathSegment();
                        if (path != null && path.contains("/")) {
                            // Attempt to clean up path segment for better display
                            path = path.substring(path.lastIndexOf("/") + 1);
                        } else if (path != null) {
                            // Default to full path segment
                            tvSelectedFile.setText(path);
                        }
                        tvSelectedFile.setText(path != null ? path : "Video Selected");
                    }
                }
        );
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(VIDEO_MIME_TYPE);
        videoPickerLauncher.launch(intent);
    }

    /**
     * Initializes the Spinner with course subjects defined in resources.
     */
    private void setupCourseSpinner() {
        // Assuming R.array.course_subjects contains the subject list (DBMS, DS, etc.)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.course_subjects, // Resource ID for the array
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapter);
    }

    /**
     * Creates a standard AlertDialog used as a loading spinner.
     * Replaces the deprecated ProgressDialog usage.
     */
    private AlertDialog createLoadingDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Use a simple layout containing a spinning ProgressBar and the message
        LayoutInflater inflater = LayoutInflater.from(context);
        // Using a standard Android system layout resource
        builder.setView(inflater.inflate(android.R.layout.simple_list_item_activated_1, null));

        builder.setCancelable(false);
        return builder.create();
    }

    private void showLoadingDialog(String message) {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            // NOTE: Setting a custom message on simple system dialogs is tricky.
            // For production, create a custom layout for the dialog.
            // For this fix, we simply show the dialog.
            loadingDialog.setTitle(message);
            loadingDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


    /**
     * Corrected method to upload video file to Firebase Storage
     * and save metadata to Firestore using Canvas-mandated paths.
     */
    private void uploadVideo() {
        String title = etVideoTitle.getText().toString().trim();
        String description = etVideoDescription.getText().toString().trim();
        String course = spinnerCourses.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || spinnerCourses.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Title, description, and course selection are required", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog("Uploading Video...");

        // --- Correct Firebase Storage Path ---
        // Path: artifacts/{APP_ID}/public/lectures/{timestamp}.mp4
        StorageReference fileRef = storage.getReference()
                .child("artifacts").child(APP_ID).child("public/lectures")
                .child(System.currentTimeMillis() + "_" + title.replaceAll("\\s+", "_") + ".mp4");

        UploadTask uploadTask = fileRef.putFile(videoUri);

        uploadTask.addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    // --- Correct Firestore Path ---
                    // Path: artifacts/{APP_ID}/public/data/lectures
                    CollectionReference lecturesCollection = db.collection("artifacts")
                            .document(APP_ID)
                            .collection("public")
                            .document("data")
                            .collection("lectures");

                    // Save metadata in Firestore
                    Map<String, Object> videoData = new HashMap<>();
                    videoData.put("title", title);
                    videoData.put("description", description);
                    videoData.put("course", course);
                    videoData.put("videoUrl", uri.toString());
                    videoData.put("uploadedBy", "faculty");
                    videoData.put("timestamp", System.currentTimeMillis());

                    lecturesCollection.add(videoData)
                            .addOnSuccessListener(documentReference -> {
                                hideLoadingDialog();
                                Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                hideLoadingDialog();
                                Toast.makeText(this, "Failed to save video metadata: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
        ).addOnFailureListener(e -> {
            hideLoadingDialog();
            Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }).addOnProgressListener(snapshot -> {
            // Optional: Update progress percentage in the dialog title or a dedicated view
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.setTitle(String.format(Locale.getDefault(), "Uploading: %.1f%%", progress));
            }
        });
    }
}