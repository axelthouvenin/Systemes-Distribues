package com.example.NoteApp;

public class Note {
    private Integer id;
    private String body;
    private String title;

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

    public Note(Integer id, String body, String title) {
        this.id = id;
        this.body = body;
        this.title = title;
    }
}
