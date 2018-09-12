package com.polsource.notes.repository;

import com.polsource.notes.domain.Note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByTitle(String title);
}
