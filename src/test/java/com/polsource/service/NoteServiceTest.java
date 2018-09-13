package com.polsource.service;

import com.polsource.notes.domain.Note;
import com.polsource.notes.repository.NoteFinder;
import com.polsource.notes.repository.NoteRepository;
import com.polsource.notes.service.NoteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NoteServiceTest {

    private NoteService noteService;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private NoteFinder noteFinder;

    private Note sampleNote;

    @Before
    public void setUp() {
        sampleNote = new Note("Note 1", "Note 1 content");
        noteService = new NoteService(noteRepository, noteFinder);
    }

    @Test
    public void shouldAddNewNote() {
        //given
        when(noteFinder.findNotesByTitle(sampleNote.getTitle())).thenReturn(Collections.emptyList());

        //when
        Note note = this.noteService.createNote(sampleNote);

        //then
        assertEquals("Note 1", note.getTitle());
        assertEquals("Note 1 content", note.getContent());
    }

    @Test
    public void shouldAssignProperDateWhenAddNewNote() {
        //given
        when(noteFinder.findNotesByTitle(sampleNote.getTitle())).thenReturn(Collections.emptyList());

        //when
        Note note = this.noteService.createNote(sampleNote);

        //then
        assertEquals(LocalDate.now(), note.getCreated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhenAddSameTitleNote() {
        //given
        when(noteFinder.findNotesByTitle(sampleNote.getTitle())).thenReturn(Arrays.asList(sampleNote));

        //when
        Note newNote = new Note("Note 1", "Alternative content");

        this.noteService.createNote(newNote);
    }

    @Test
    public void shouldUpdateNote() {
        //given
        when(noteFinder.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));

        //when
        Note updatedNote = noteService.updateNotes(sampleNote.getTitle(), "New content");

        //then
        assertEquals("New content", updatedNote.getContent());
        assertEquals(sampleNote.getTitle(), updatedNote.getTitle());
    }

    @Test
    public void shouldIncrementVersionWhenUpdateNote() {
        //given
        when(noteFinder.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));

        //when
        Note updatedOnce = noteService.updateNotes(sampleNote.getTitle(), "Once updated");

        when(noteFinder.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(updatedOnce));

        Note updatedTwice = noteService.updateNotes(sampleNote.getTitle(), "Twice updated");

        //then
        assertEquals(0, sampleNote.getVersion());
        assertEquals(1, updatedOnce.getVersion());
        assertEquals(2, updatedTwice.getVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhileUpdateNoteWithDifferentTitle() {
        //given
        when(noteFinder.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));

        //when
        noteService.updateNotes("New title", "Once updated");
    }

}
