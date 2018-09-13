package com.polsource.notes.controller;

import java.util.List;
import java.util.Map;

import com.polsource.notes.domain.Note;
import com.polsource.notes.service.NoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note newNote = null;
        try {
            newNote = this.noteService.createNote(note);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(newNote, HttpStatus.OK);
    }

    @PostMapping("/{title}")
    public ResponseEntity<Note> updateNote(@PathVariable String title, @RequestBody Map<String, String> noteContent) {

        Note newNote = null;
        try {
            newNote = this.noteService.updateNotes(title, noteContent.get("content"));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(newNote, HttpStatus.OK);
    }

    @DeleteMapping("/{title}")
    public ResponseEntity deleteNotes(@PathVariable String title) {
        try {
            this.noteService.deleteNotes(title);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping
    public List<Note> getAllCurrentNotes() {
         return noteService.getAllCurrentNotes();
    }

    @GetMapping("/history/{title}")
    public List<Note> getNotesHistory(@PathVariable String title) {
        return noteService.getNotesHistoryByTitle(title);
    }
}
