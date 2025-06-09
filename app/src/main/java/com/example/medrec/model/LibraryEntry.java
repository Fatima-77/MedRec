package com.example.medrec.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LibraryEntry implements Serializable {
    public String status;
    public long timestamp;
    public Map<String, Object> media;

    public LibraryEntry() { }

    public LibraryEntry(Media mediaObj, String status) {
        this.status = status;
        this.timestamp = System.currentTimeMillis();

        media = new HashMap<>();
        media.put("id", mediaObj.getId());
        media.put("title", mediaObj.getTitle());
        media.put("type", mediaObj.getType());
        media.put("cover", mediaObj.getCover());
    }
}

