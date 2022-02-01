package com.example.NoteApp;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(
            long id, String type) {
        super(String.format(
                "The %s with the id %d was not found.",
                type, id));
    }
    public NotFoundException(
            UUID id, String type) {
        super(String.format(
                "The %s with the id %s was not found.",
                type, id));
    }

}
