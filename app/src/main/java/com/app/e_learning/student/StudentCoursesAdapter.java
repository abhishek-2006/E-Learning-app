package com.app.e_learning.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.e_learning.R;

import java.util.List;

public class StudentCoursesAdapter extends RecyclerView.Adapter<StudentCoursesAdapter.ViewHolder> {

    List<CourseModel> list;

    public StudentCoursesAdapter(List<CourseModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_student_course, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CourseModel course = list.get(position);

        holder.courseName.setText(course.getCourseName());
        holder.facultyName.setText("Faculty: " + course.getFacultyName());
        holder.courseCode.setText("Course Code: " + course.getCourseCode());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView courseName, facultyName, courseCode;

        public ViewHolder(View itemView) {
            super(itemView);

            courseName = itemView.findViewById(R.id.tv_course_name);
            facultyName = itemView.findViewById(R.id.tv_faculty_name);
            courseCode = itemView.findViewById(R.id.tv_course_code);
        }
    }
}