package com.abhishek.e_learning.admin;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference coursesRef = db.collection("courses");

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
        if (course.getId() == null) return;

        coursesRef.document(course.getId())
                .update(course.toMap()) // Uses the toMap() you created!
                .addOnSuccessListener(aVoid -> listener.onSuccess("Course Updated"))
                .addOnFailureListener(listener::onFailure);
    }

    // Delete Course
    public void deleteCourse(String courseId, OnCompleteListener listener) {
        coursesRef.document(courseId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess("Course Deleted"))
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
