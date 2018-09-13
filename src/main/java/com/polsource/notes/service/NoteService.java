package com.polsource.notes.service;

import java.util.Optional;

import com.polsource.notes.domain.Note;
import com.polsource.notes.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(Note note) {
        Optional<Note> noteOptional = findCurrentNoteByTitle(note.getTitle());
        if (noteOptional.isPresent()) {
            throw new IllegalArgumentException("the note with such a title already exists");
        } else if (!isFieldValid("title", note.getTitle()) || !isFieldValid("content", note.getContent())) {
            log.debug("field validation error");
        }
        Note newNote = new Note(
                note.getTitle(),
                note.getContent()
        );
        saveNote(newNote);
        return newNote;
    }
    public Note updateNotes(String title, String content) {
        Optional<Note> noteOptional = findCurrentNoteByTitle(title);
        if (!noteOptional.isPresent()) {
            throw new IllegalArgumentException("note with such a title doesn't exist");
        } else if (!isFieldValid("content", content)) {
            log.debug("field validation error");
        }
        Note noteFromRepo = noteOptional.get();
        Note newNote = new Note(
                noteFromRepo.getTitle(),
                content.toString(),
                noteFromRepo.getCreated(),
                noteFromRepo.getVersion() + 1
        );
        saveNote(newNote);
        return newNote;
    }

    private void saveNote(Note newNote) {
        this.noteRepository.save(newNote);
    }

    private Optional<Note> findNoteByTitle(String title) {
        return this.noteRepository.findByTitle(title);
    }

    private Optional<Note> findCurrentNoteByTitle(String title) {
        return this.noteRepository.findCurrentNoteByTitle(title);
    }

    private boolean isFieldValid(String field, String value) {
        if (value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException(String.format("the %s field has not been filled out", field));
        }
        return true;
    }


}
