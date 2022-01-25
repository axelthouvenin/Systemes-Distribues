package com.example.NoteApp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class NoteApp {
    private List<Note> notes;

    public NoteApp() {
        notes = new ArrayList<>();
        notes.add(new Note(1, "Buy cake", "TODO"));
        notes.add(new Note(2, "Buy concert tickets", "concert"));
    }

    @GetMapping("/notes")
    public List<Note> get_notes() {
        return notes;
    }

    @GetMapping("/notes/{id}")
    public Note get_one_note(
            @PathVariable(value = "id") Integer id) {
        for (Note note : notes) {
            if (note.getId() == id)
                return note;
        }
        throw new RuntimeException("Note does not exist.");
    }
}
