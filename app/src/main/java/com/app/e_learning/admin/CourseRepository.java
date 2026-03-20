package com.app.e_learning.admin;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Add Course
    public void addCourse(Course course, OnCompleteListener listener) {
        db.collection("courses")
                .add(course.toMap())
                .addOnSuccessListener(docRef ->
                        listener.onSuccess("Course added"))
                .addOnFailureListener(listener::onFailure);
    }

    // Update Course
    public void updateCourse(Course course, OnCompleteListener listener) {
        db.collection("courses")
                .document(course.getId())
                .update(course.toMap())
                .addOnSuccessListener(unused ->
                        listener.onSuccess("Course updated"))
                .addOnFailureListener(listener::onFailure);
    }

    // Delete Course
    public void deleteCourse(String courseId, OnCompleteListener listener) {
        db.collection("courses")
                .document(courseId)
                .delete()
                .addOnSuccessListener(unused ->
                        listener.onSuccess("Course deleted"))
                .addOnFailureListener(listener::onFailure);
    }

    // Real-time listener
    public ListenerRegistration listenForCourses(CourseDataListener listener) {
        return db.collection("courses")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        listener.onFailure(error);
                        return;
                    }

                    List<Course> list = new ArrayList<>();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Course course = doc.toObject(Course.class);
                        if (course != null) {
                            course.setId(doc.getId());
                            list.add(course);
                        }
                    }

                    listener.onCoursesLoaded(list);
                });
    }

    public interface OnCompleteListener {
        void onSuccess(String message);

        void onFailure(Exception e);
    }

    public interface CourseDataListener {
        void onCoursesLoaded(List<Course> courses);

        void onFailure(Exception e);
    }
}