package com.app.e_learning.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.e_learning.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ComplaintFeedbackActivity extends AppCompatActivity {

    private RecyclerView rvFeedback;
    private FeedbackAdapter adapter;
    private List<Feedback> feedbackList;
    private FirebaseFirestore db;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints_feedback);

        db = FirebaseFirestore.getInstance();
        rvFeedback = findViewById(R.id.rv_feedback_list);
        feedbackList = new ArrayList<>();
        tvEmpty = findViewById(R.id.tv_empty_feedback);

        setupRecyclerView();
        fetchFeedbackFromDb();
    }

    private void setupRecyclerView() {
        adapter = new FeedbackAdapter(feedbackList, feedback -> {
            // Handle "View Details" click - maybe show a dialog with the full message?
            showFeedbackDetails(feedback);
        });
        rvFeedback.setLayoutManager(new LinearLayoutManager(this));
        rvFeedback.setAdapter(adapter);
    }

    private void fetchFeedbackFromDb() {
        db.collection("feedback")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading feedback", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        feedbackList.clear();
                        for (var doc : value.getDocuments()) {
                            Feedback f = doc.toObject(Feedback.class);
                            if (f != null) {
                                f.setId(doc.getId());
                                feedbackList.add(f);
                            }
                        }

                        if (feedbackList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            rvFeedback.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            rvFeedback.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void showFeedbackDetails(Feedback feedback) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(feedback.getSubject())
                .setMessage("From: " + feedback.getFromName() + "\n\n" + feedback.getMessage())
                .setPositiveButton("Close", null)
                .show();
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Feedback feedbackToDelete = feedbackList.get(position);

                // Delete from Firestore
                db.collection("feedback").document(feedbackToDelete.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(ComplaintFeedbackActivity.this, "Feedback removed", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            adapter.notifyItemChanged(position); // Undo swipe on failure
                            Toast.makeText(ComplaintFeedbackActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                        });
            }
        }).attachToRecyclerView(rvFeedback);
    }
}