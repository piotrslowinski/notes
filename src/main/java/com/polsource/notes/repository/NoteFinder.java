package com.polsource.notes.repository;

import com.polsource.notes.domain.Note;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoteFinder {

    List<Note> findNotesByTitle(String title);

    Optional<Note> findCurrentNoteByTitle(String title);

    List<Note> findCurrentNotes();
}
