package com.polsource.notes.repository;

import com.polsource.notes.domain.Note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

}
