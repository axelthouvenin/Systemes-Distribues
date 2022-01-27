package com.example.NoteApp;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Note {
    private Integer id;
    @NotNull(message= "Please provide a body.")
    private String body;
    @NotNull
    @Size(max = 255)
    private String title;

    private Category category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Note() {
    }

    public Note(Integer id, String body, String title, Category category) {
        this.id = id;
        this.body = body;
        this.title = title;
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
