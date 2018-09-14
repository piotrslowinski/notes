package com.polsource.service;

import com.polsource.notes.domain.Note;
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
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NoteServiceTest {

    private NoteService noteService;

    @Mock
    private NoteRepository noteRepository;

    private Note sampleNote;

    @Before
    public void setUp() {
        sampleNote = new Note("Note 1", "Note 1 content");
        noteService = new NoteService(noteRepository);
    }

    @Test
    public void shouldAddNewNote() {
        //given
        when(noteRepository.findNotesByTitle(sampleNote.getTitle())).thenReturn(Collections.emptyList());

        //when
        Note note = this.noteService.createNote(sampleNote);

        //then
        assertEquals("Note 1", note.getTitle());
        assertEquals("Note 1 content", note.getContent());
    }

    @Test
    public void shouldAssignProperDateWhenAddNewNote() {
        //given
        when(noteRepository.findNotesByTitle(sampleNote.getTitle())).thenReturn(Collections.emptyList());

        //when
        Note note = this.noteService.createNote(sampleNote);

        //then
        assertEquals(LocalDate.now(), note.getCreated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhenAddSameTitleNote() {
        //given
        when(noteRepository.findNotesByTitle(sampleNote.getTitle())).thenReturn(Arrays.asList(sampleNote));

        //when
        Note newNote = new Note("Note 1", "Alternative content");

        this.noteService.createNote(newNote);
    }

    @Test
    public void shouldUpdateNote() {
        //given
        when(noteRepository.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));

        //when
        Note updatedNote = noteService.updateNotes(sampleNote.getTitle(), "New content");

        //then
        assertEquals("New content", updatedNote.getContent());
        assertEquals(sampleNote.getTitle(), updatedNote.getTitle());
    }

    @Test
    public void shouldIncrementVersionWhenUpdateNote() {
        //given
        when(noteRepository.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));

        //when
        Note updatedOnce = noteService.updateNotes(sampleNote.getTitle(), "Once updated");

        when(noteRepository.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(updatedOnce));

        Note updatedTwice = noteService.updateNotes(sampleNote.getTitle(), "Twice updated");

        //then
        assertEquals(0, sampleNote.getVersion());
        assertEquals(1, updatedOnce.getVersion());
        assertEquals(2, updatedTwice.getVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhileUpdateNoteWithDifferentTitle() {
        //given
        when(noteRepository.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));

        //when
        noteService.updateNotes("New title", "Once updated");
    }

    @Test
    public void shouldChangeActiveFieldWhenDeleteNote() {
        //given
        when(noteRepository.findNotesByTitle(sampleNote.getTitle())).thenReturn(Arrays.asList(sampleNote));

        //when
        noteService.deleteNotes(sampleNote.getTitle());

        //then
        assertEquals(false, sampleNote.isActive());
    }

    @Test
    public void shouldDeactivateAllVersionsOfNotesWhenDeleteNote() {
        //given
        when(noteRepository.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));
        Note updatedOnce = noteService.updateNotes(sampleNote.getTitle(), "Once updated");

        when(noteRepository.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(updatedOnce));
        Note updatedTwice = noteService.updateNotes(sampleNote.getTitle(), "Twice updated");

        when(noteRepository.findNotesByTitle(sampleNote.getTitle()))
                .thenReturn(Arrays.asList(sampleNote, updatedOnce, updatedTwice));

        //when
        noteService.deleteNotes(sampleNote.getTitle());

        //then
        assertEquals(false, sampleNote.isActive());
        assertEquals(false, updatedOnce.isActive());
        assertEquals(false, updatedTwice.isActive());
    }

    @Test
    public void shouldReturnOnlyActiveNotesWhenSearchCurrentNotes() {
        //given
        when(noteRepository.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));
        Note updatedOnce = noteService.updateNotes(sampleNote.getTitle(), "Once updated");

        Note newNote = this.noteService.createNote(new Note("New note", "New Content"));

        when(noteRepository.findCurrentNotes()).thenReturn(Arrays.asList(updatedOnce, newNote));

        //when
        List<Note> currentNotes = noteService.getAllCurrentNotes();

        //then
        assertEquals(Arrays.asList(updatedOnce, newNote), currentNotes);
        assertEquals(true, currentNotes.stream().map(Note::isActive).findFirst().get());
    }

    @Test
    public void shouldReturnDeletedNotesHistory() {
        //given
        when(noteRepository.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));
        Note updatedOnce = noteService.updateNotes(sampleNote.getTitle(), "Once updated");

        when(noteRepository.findCurrentNoteByTitle(sampleNote.getTitle())).thenReturn(Optional.of(updatedOnce));
        Note updatedTwice = noteService.updateNotes(sampleNote.getTitle(), "Twice updated");

        when(noteRepository.findNotesByTitle(sampleNote.getTitle()))
                .thenReturn(Arrays.asList(sampleNote, updatedOnce, updatedTwice));

        noteService.deleteNotes(sampleNote.getTitle());

        //when

        List<Note> notesHistory = noteService.getNotesHistoryByTitle(sampleNote.getTitle());
        assertEquals(Arrays.asList(sampleNote, updatedOnce, updatedTwice), notesHistory);
        assertEquals(3, notesHistory.size());
    }

    @Test
    public void shouldReturnCurrentNoteById() {
        //given
        when(noteRepository.getActiveNoteById(sampleNote.getId())).thenReturn(Optional.of(sampleNote));

        //when
        Note note = noteService.getActiveNoteById(sampleNote.getId());

        //then
        assertEquals(note, sampleNote);
        assertEquals(true, note.isActive());
    }

}
