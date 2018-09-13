package com.polsource.notes.repository;

import java.util.List;
import java.util.Optional;

import com.polsource.notes.domain.Note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT n FROM Note n WHERE n.title = :title")
    List<Note> findNotesByTitle(@Param("title") String title);

    @Query("SELECT n from Note n WHERE n.version = (SELECT MAX(nn.version) FROM Note nn WHERE nn.title = :title AND nn.active IS true) ")
    Optional<Note> findCurrentNoteByTitle(@Param("title") String title);

    @Query("SELECT n FROM Note n WHERE n.version = (SELECT MAX(nn.version) FROM Note nn WHERE nn.active IS true)")
    List<Note> findCurrentNotes();
}
