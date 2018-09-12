package com.polsource.notes.service;

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
        if (isNotePresent(note)){
            throw new IllegalArgumentException("the note with such a title already exists");
        }else if (!isNotesFieldsValid(note)) {
            log.debug("field validation error");
        }
        Note newNote = new Note(
                note.getTitle(),
                note.getContent()
        );
        this.noteRepository.save(newNote);
        return newNote;
    }

    private boolean isNotePresent(Note note) {
        return noteRepository.findByTitle(note.getTitle()).isPresent();
    }

    private boolean isNotesFieldsValid(Note note) {
        if (note.getTitle() == null || note.getTitle().trim().length() == 0) {
            throw new IllegalArgumentException("the title field has not been filled out");
        } else if (note.getContent() == null || note.getContent().trim().length() == 0) {
            throw new IllegalArgumentException("the content field has not been filled out");
        }
        return true;
    }
}
