package com.example.medrec.model;

import java.io.Serializable;
import java.util.List;

public class Media implements Serializable {
    private String id;
    private String title;
    private String type;
    private String description;
    private String imageUrl;
    private String language;
    private List<String> genres;

    public Media() {
        // Required empty constructor for Firebase
    }

    public Media(String id, String title, String type, String description,
                 String imageUrl, String language, List<String> genres) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.description = description;
        this.imageUrl = imageUrl;
        this.language = language;
        this.genres = genres;
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getGenres() {
        return genres;
    }
}

