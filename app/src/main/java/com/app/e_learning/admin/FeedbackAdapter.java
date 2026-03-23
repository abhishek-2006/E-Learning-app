package com.app.e_learning.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.e_learning.R;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private final List<Feedback> list;
    private final OnFeedbackClickListener listener;

    public FeedbackAdapter(List<Feedback> list, OnFeedbackClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feedback feedback = list.get(position);
        holder.tvSubject.setText(feedback.getSubject());
        holder.tvFrom.setText("From: " + feedback.getFromName());
        holder.btnView.setOnClickListener(v -> listener.onClick(feedback));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnFeedbackClickListener {
        void onClick(Feedback feedback);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvFrom;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tv_feedback_subject);
            tvFrom = itemView.findViewById(R.id.tv_complaint_from);
            btnView = itemView.findViewById(R.id.btn_view_details);
        }
    }
}