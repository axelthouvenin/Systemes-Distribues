package com.example.NoteApp;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class NoteAppController {
    private final List<Note> notes;
    private final List<Category> categories;
    private final Category default_category;
    private Integer lastId = 1;

    public NoteAppController() {
        categories = new ArrayList<>();
        default_category = new Category("Default");
        categories.add(default_category);
        notes = new ArrayList<>();
        notes.add(new Note(lastId++, "Buy cake", "TODO", default_category));
        notes.add(new Note(lastId++, "Buy concert tickets", "concert", default_category));
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
        throw new NotFoundException(id, "Note");
    }

    private void fixup_note_category(Note note){
        if (note.getCategory() == null) {
            note.setCategory(default_category);
        }
        else {
            boolean found_category = false;
            for (Category cat : categories) {
                if (cat.getId().equals(note.getCategory().getId())) {
                    note.setCategory(cat);
                    found_category = true;
                    break;
                }
            }
            if (!found_category)
                throw new NotFoundException(0, "Category");
        }
    }

    @PostMapping("/notes")
    public Note post_note(@RequestBody @Valid Note note) {
        fixup_note_category(note);
        note.setId(lastId++);
        notes.add(note);
        return note;
    }

    @PutMapping("/notes/{id}")
    public Note update_note(@PathVariable(value = "id") Integer id,
                          @RequestBody @Valid Note updateNote) {
        Note n = null;
        for (Note note : notes) { // Search the already existing note.
            if (note.getId() == id) {
                n = note;
                break;
            }
        }
            if (n == null) // Not found.
                throw new NotFoundException(id, "Note");
        n.setBody(updateNote.getBody()); // Update body
        n.setTitle(updateNote.getTitle()); // Update title
        return n; // return updated note
    }


    @DeleteMapping("/notes/{id}")
    public void delete_note(@PathVariable(value = "id") Integer id) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == id) {
                notes.remove(i);
                return;
            }
        }
    }
}
