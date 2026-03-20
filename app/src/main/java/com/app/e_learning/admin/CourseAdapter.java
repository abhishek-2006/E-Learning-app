package com.app.e_learning.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.e_learning.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final List<Course> courseList;
    private final OnCourseClickListener listener;

    public CourseAdapter(List<Course> courseList, OnCourseClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.tvCourseName.setText(course.getCourseName());
        holder.tvFacultyName.setText(course.getFacultyName());
        holder.tvCourseCode.setText(course.getCourseCode());
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_admin_course, parent, false);
        return new CourseViewHolder(view);
    }

    public interface OnCourseClickListener {
        void onEdit(Course course);

        void onDelete(Course course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView tvCourseName, tvFacultyName, tvCourseCode;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvFacultyName = itemView.findViewById(R.id.tv_faculty_name);
            tvCourseCode = itemView.findViewById(R.id.tv_course_code);
        }
    }
}