package com.example.medrec.model;

public class Media {
    private String id;
    private String title;
    private String type; // anime, manga, book, lightnovel
    private String description;
    private String language;
    private String[] genres;
    private String imageUrl;

    public Media() {
        // Default constructor required for Firebase
    }

    public class Media {
        private String id, title, cover, description, type;
        private double rating;
        private List<String> category;
        private String authors;  // Optional (for books)
        private String language; // Optional (for books)

        public Media() {
            // Default constructor
        }

        // Getters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getCover() { return cover; }
        public String getDescription() { return description; }
        public String getType() { return type; }
        public double getRating() { return rating; }
        public List<String> getCategory() { return category; }
        public String getAuthors() { return authors; }
        public String getLanguage() { return language; }


