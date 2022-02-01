package com.example.NoteApp;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class NoteAppController {
    private final List<Note> notes;
    private final List<Category> categories;
    private final Category default_category;
    private Integer lastId = 1;

    private final Logger logger =
            LoggerFactory.getLogger(NoteAppController.class);

    public NoteAppController() {
        categories = new ArrayList<>();
        default_category = new Category("Default");
        categories.add(default_category);
        Category test_category = new Category("Test");
        categories.add(test_category);
        notes = new ArrayList<>();
        notes.add(new Note(lastId++, "Buy cake", "TODO", default_category));
        notes.add(new Note(lastId++, "First test", "TODO", test_category));
        notes.add(new Note(lastId++, "Buy concert tickets", "concert", default_category));
        notes.add(new Note(lastId++, "Second test", "TODO", test_category));

    }

    @GetMapping("/notes")
    public List<Note> get_notes() {
        logger.info("GET /notes");
        return notes;
    }

    @GetMapping("/notes/{id}")
    public Note get_one_note(
            @PathVariable(value = "id") Integer id) {
        logger.info(String.format("GET the note %d.", id));
        for (Note note : notes) {
            if (note.getId() == id)
                return note;
        }
        throw new NotFoundException(id, "Note");
    }

    /* This sets the correct category object according to the UUID */
    private void fixup_note_category(Note note, UUID uuid){
        if (note.getCategory() == null) {
            note.setCategory(default_category);
        }
        else {
            boolean found_category = false;
            for (Category cat : categories) {
                if (cat.getId().equals(uuid)) {
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
        logger.info("POST note");
        fixup_note_category(note, note.getCategory().getId());
        note.setId(lastId++);
        notes.add(note);
        logger.info(String.format(
                "Id counter is now at %d there are %d notes",
                lastId, notes.size()));
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
        fixup_note_category(n, updateNote.getCategory().getId());
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

    @GetMapping("/categories")
    public List<Category> list_categories() {
        return this.categories;
    }

    @GetMapping("/categories/{id}")
    public Category get_one_category(
            @PathVariable(value = "id") UUID id) {
        for (Category cat : categories) {
            if (cat.getId().equals(id))
                return cat;
        }
        throw new NotFoundException(id, "Note");
    }

    @GetMapping("/categories/{id}/notes")
    public List<Note> get_notes_for_a_category(
            @PathVariable(value = "id") UUID id) {
        List<Note> notes = new ArrayList<>();
        for (Note note : this.notes) {
            if (note.getCategory().getId().equals(id))
                notes.add(note);
        }
        return notes;
    }

    @DeleteMapping("/categories/{id}/notes")
    public void delete_notes_in_a_category(
            @PathVariable(value = "id") UUID id) {
        for (int i = 0; i < notes.size();) {
            Note n = notes.get(i);
            if (n.getCategory().getId().equals(id))
                notes.remove(i); // The list notes is one shorter: don't increase i
            else
                i++;
        }
    }

    @PostMapping("/categories")
    public Category post_category(@RequestBody @Valid Category cat) {
        UUID new_uuid = UUID.randomUUID();
        cat.setId(new_uuid);
        categories.add(cat);
        return cat;
    }

    @DeleteMapping("/categories/{id}")
    public void delete_category(
            @PathVariable(value = "id") UUID id) {
        // TODO: proper exception
        if (id.equals(default_category.getId()))
            throw new RuntimeException("Can't delete default category.");
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(id)) {
                categories.remove(i);
                int num = 0;
                for (Note note : notes) {
                    num++;
                    if (note.getCategory().getId().equals(id))
                        note.setCategory(default_category);
                }
                logger.info(
                        String.format(
                                "Moved %d notes to the default category.", num));
                return;
            }
        }
        throw new NotFoundException(id, "Category");
    }
}
