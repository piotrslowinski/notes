package com.polsource.notes.repository;

import java.util.Optional;

import com.polsource.notes.domain.Note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByTitle(String title);

    @Query("SELECT n from Note n WHERE n.version = (SELECT MAX(nn.version) FROM Note nn WHERE nn.title = :title)")
    Optional<Note> findCurrentNoteByTitle(@Param("title")String title);
}
