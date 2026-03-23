package com.app.e_learning.admin;

public class Feedback {
    private String id;
    private String subject;
    private String fromName;
    private String message;
    private long timestamp;

    public Feedback() {
    } // Required for Firestore

    public Feedback(String subject, String fromName, String message, long timestamp) {
        this.subject = subject;
        this.fromName = fromName;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public String getFromName() {
        return fromName;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}