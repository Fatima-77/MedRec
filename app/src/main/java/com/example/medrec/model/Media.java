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

    public Media() {
        // Default constructor required for calls to DataSnapshot.getValue(Media.class)
    }

    // Add getters — Firebase uses these when populating the class

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
}


