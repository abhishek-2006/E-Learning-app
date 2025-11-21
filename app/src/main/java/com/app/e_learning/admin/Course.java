package com.app.e_learning.admin;

import java.util.HashMap;
import java.util.Map;

public class Course {
    private String id;
    private String name;
    private String description;
    private long createdAt;

    // Required no-argument constructor for Firebase
    public Course() {
    }

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters (omitted for brevity)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // Helper method to convert to a Firestore-friendly Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("createdAt", createdAt);
        // Do NOT include the 'id' when creating or updating, as it's the document key
        return map;
    }
}