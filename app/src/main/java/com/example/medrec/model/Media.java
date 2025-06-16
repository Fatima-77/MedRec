package com.example.medrec.model;

import java.io.Serializable;
import java.util.List;

public class Media implements Serializable {
    private String id;
    private String title;
    private String type;
    private String description;
    private String cover;
    private Double rating;       // Optional: could be null
    private Integer year;        // Optional: could be null
    private String authors;      // Optional
    private String language;     // Optional
    private List<String> category;  // category is your genre list

    // User-specific fields for Library (set in code, not in main Media DB)
    private String status;        // e.g. "Completed", "Watching", etc.
    private Double userRating;    // user's own rating for this media
    private Long timestamp;       // date/time user added to their library

    public Media() {
        // Default constructor required for calls to DataSnapshot.getValue(Media.class)
    }

    // --- Core fields (Firebase uses these) ---
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getType() {
        return type;
    }
    public String getDescription() {
        return description;
    }
    public String getCover() {
        return cover;
    }
    public Double getRating() {
        return rating;
    }
    public Integer getYear() {
        return year;
    }
    public String getAuthors() {
        return authors;
    }
    public String getLanguage() {
        return language;
    }
    public List<String> getCategory() {
        return category;
    }

    // --- User-library fields (for app use, not core DB) ---
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getUserRating() { return userRating; }
    public void setUserRating(Double userRating) { this.userRating = userRating; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}



