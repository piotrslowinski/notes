package com.polsource.notes.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.polsource.notes.domain.Note;
import com.polsource.notes.repository.NoteFinder;
import com.polsource.notes.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;
    private final NoteFinder noteFinder;

    @Autowired
    public NoteService(NoteRepository noteRepository, NoteFinder noteFinder) {
        this.noteRepository = noteRepository;
        this.noteFinder = noteFinder;
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


    public void deleteNotes(String title) {
        List<Note> notes = findNotesByTitle(title);
        if (notes.isEmpty()) {
            throw new NoSuchElementException("no such notes in repo");
        }
        notes.stream().filter(Note::isActive).forEach(note -> deactivateNote(note));
        notes.stream().forEach((note -> saveNote(note)));
    }

    private void deactivateNote(Note note) {
        note.setActive(false);
    }

    @Transactional
    private void saveNote(Note newNote) {
        this.noteRepository.save(newNote);
    }

    private List<Note> findNotesByTitle(String title) {
        return this.noteFinder.findNotesByTitle(title);
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


    public List<Note> getAllCurrentNotes() {
        List<Note> notes = noteFinder.findCurrentNotes();
        return notes.stream().filter(Note::isActive).collect(Collectors.toList());
    }

    public List<Note> getNotesHistoryByTitle(String title) {
        List<Note> notes = noteFinder.findNotesByTitle(title);
        return notes;
    }
}
