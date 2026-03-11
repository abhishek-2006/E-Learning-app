package com.app.e_learning.faculty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.e_learning.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UploadMaterialActivity extends AppCompatActivity {

    // --- Critical Constants ---
    private static final String APP_ID = "1:626475752933:android:a85c5055556f40a208a30c";

    private TextView tvSelectedFileName;
    private Button btnSelectFile, btnUploadMaterial;
    private Spinner spinnerCourses;
    private TextInputEditText etMaterialTitle, etMaterialDescription;

    private Uri fileUri;
    private final FirebaseAuth auth = FirebaseAuth.getInstance(); // Field converted to final initialization
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Field converted to final initialization
    private final FirebaseStorage storage = FirebaseStorage.getInstance(); // Field converted to final initialization

    private AlertDialog loadingDialog;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_upload_material);

        // 1. Initialize Views
        tvSelectedFileName = findViewById(R.id.tv_selected_file_name);
        btnSelectFile = findViewById(R.id.btn_select_file);
        btnUploadMaterial = findViewById(R.id.btn_upload_material);
        spinnerCourses = findViewById(R.id.spinner_courses);
        etMaterialTitle = findViewById(R.id.et_material_title);
        etMaterialDescription = findViewById(R.id.et_material_description);

        // 2. Setup Components
        setupCourseSpinner();
        setupFilePickerLauncher();
        loadingDialog = createLoadingDialog(this);

        // 3. Setup Listeners
        btnSelectFile.setOnClickListener(v -> openFileChooser());
        btnUploadMaterial.setOnClickListener(v -> uploadMaterial());
    }

    // --- DEPRECATION FIX: Activity Result Launcher ---

    private void setupFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        fileUri = result.getData().getData();

                        // Safely display the file name
                        String pathSegment = fileUri != null ? fileUri.getLastPathSegment() : null;
                        if (pathSegment != null) {
                            if (pathSegment.contains("/")) {
                                pathSegment = pathSegment.substring(pathSegment.lastIndexOf("/") + 1);
                            }
                            tvSelectedFileName.setText(pathSegment);
                        } else {
                            tvSelectedFileName.setText(getString(R.string.no_file_chosen));
                        }
                    }
                }
        );
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");

        // --- Added PPT/PDF Filter ---
        String[] mimeTypes = {
                "application/pdf", // PDF
                "application/vnd.ms-powerpoint", // PPT
                "application/vnd.openxmlformats-officedocument.presentationml.presentation" // PPTX
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // --- End Filter ---

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    // --- UI/Data Handling ---

    private void setupCourseSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.course_subjects,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapter);
    }

    private AlertDialog createLoadingDialog(Context context) {
        // Custom AlertDialog setup (Fixed inflation warning)
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Simple layout for the dialog box (using new operator instead of LayoutInflater)
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(40, 40, 40, 40);

        ProgressBar spinner = new ProgressBar(context);
        spinner.setIndeterminate(true);
        layout.addView(spinner);

        TextView textView = new TextView(context);
        textView.setText("Uploading Material...");
        textView.setPadding(40, 0, 0, 0);
        layout.addView(textView);

        builder.setView(layout);
        builder.setCancelable(false);
        return builder.create();
    }

    private void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    // --- Upload Logic ---

    private void uploadMaterial() {
        // --- Variable Finality Fixes ---
        final String title = etMaterialTitle.getText().toString().trim();
        final String description = etMaterialDescription.getText().toString().trim();
        final Object selectedItem = spinnerCourses.getSelectedItem();
        final String course = (selectedItem != null) ? selectedItem.toString() : "";

        if (fileUri == null || title.isEmpty() || course.isEmpty() || "Select a Subject".equals(course)) {
            Toast.makeText(this, "Title, description, file, and valid course selection are required", Toast.LENGTH_LONG).show();
            return;
        }

        // --- Critical User Check ---
        final FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Authentication required to upload.", Toast.LENGTH_SHORT).show();
            return;
        }
        final String uid = user.getUid();

        showLoadingDialog();

        // --- Filename and Extension Logic ---
        final String fileNameBase = System.currentTimeMillis() + "_" + title.replaceAll("[^a-zA-Z0-9.-]", "_");
        String tempExtension = ".dat"; // Mutable variable for try block

        try {
            String mimeType = getContentResolver().getType(fileUri);
            if (mimeType != null && mimeType.contains("/")) {
                tempExtension = "." + mimeType.substring(mimeType.lastIndexOf("/") + 1);
            }
        } catch (Exception ignored) { /* Use default extension */ }
        final String finalExtension = tempExtension; // Final variable for use in lambda


        // --- CORRECTED STORAGE PATH ---
        StorageReference fileRef = storage.getReference()
                .child("artifacts").child(APP_ID).child("public/materials")
                .child(fileNameBase + finalExtension);

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    // --- CORRECTED FIRESTORE PATH ---
                    CollectionReference materialsCollection = db.collection("artifacts")
                            .document(APP_ID)
                            .collection("public")
                            .document("data")
                            .collection("materials");

                    // Save metadata in Firestore
                    Map<String, Object> materialData = new HashMap<>();
                    materialData.put("title", title);
                    materialData.put("description", description);
                    materialData.put("course", course);
                    materialData.put("fileUrl", uri.toString());
                    materialData.put("fileName", fileNameBase + finalExtension);
                    materialData.put("uploadedBy", uid);
                    materialData.put("timestamp", System.currentTimeMillis());

                    materialsCollection.add(materialData)
                            .addOnSuccessListener(documentReference -> {
                                hideLoadingDialog();
                                Toast.makeText(UploadMaterialActivity.this, "Material uploaded successfully", Toast.LENGTH_SHORT).show();
                                // Reset fields upon success
                                etMaterialTitle.setText("");
                                etMaterialDescription.setText("");
                                tvSelectedFileName.setText(getString(R.string.no_file_chosen));
                                fileUri = null;
                            })
                            .addOnFailureListener(e -> {
                                hideLoadingDialog();
                                Log.e("Upload", "Firestore save failed", e);
                                Toast.makeText(UploadMaterialActivity.this, "Failed to save metadata: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    hideLoadingDialog();
                    Log.e("Upload", "Storage failed", e);
                    Toast.makeText(UploadMaterialActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}