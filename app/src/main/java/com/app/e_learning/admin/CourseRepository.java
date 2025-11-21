package com.app.e_learning.admin;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference coursesCollection;

    public CourseRepository() {
        // Defines the Firestore path: artifacts/APP_ID/public/data/courses
        coursesCollection = db.collection("artifacts")
                .document("data")
                .collection("courses");
    }

    // --- C: Create Operation ---
    public void addCourse(Course course, final OnCompleteListener listener) {
        coursesCollection.add(course.toMap())
                .addOnSuccessListener(documentReference -> {
                    if (listener != null) {
                        listener.onSuccess("Course added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    // --- R: Real-time Read Operation ---
    // Sets up a listener for real-time updates and returns a registration object
    // to allow the calling activity/viewmodel to stop listening when destroyed.
    public ListenerRegistration listenForCourses(final CourseDataListener listener) {
        return coursesCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle the error (e.g., network issues, permissions)
                listener.onFailure(error);
                return;
            }

            if (value != null) {
                List<Course> courses = new ArrayList<>();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    // Convert Firestore Document to the Course model
                    Course course = doc.toObject(Course.class);
                    if (course != null) {
                        // Set the document ID onto the model object
                        course.setId(doc.getId());
                        courses.add(course);
                    }
                }
                listener.onCoursesLoaded(courses);
            }
        });
    }

    // --- U: Update Operation ---
    public void updateCourse(String courseId, Map<String, Object> updates, final OnCompleteListener listener) {
        coursesCollection.document(courseId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onSuccess("Course updated successfully.");
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    // --- D: Delete Operation ---
    public void deleteCourse(String courseId, final OnCompleteListener listener) {
        coursesCollection.document(courseId).delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onSuccess("Course deleted successfully.");
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    // Interface to communicate results back to the Activity/ViewModel
    public interface CourseDataListener {
        void onCoursesLoaded(List<Course> courses);

        void onFailure(Exception e);
    }

    // Simple listener interface for Create, Update, and Delete operations
    public interface OnCompleteListener {
        void onSuccess(String message);

        void onFailure(Exception e);
    }
}