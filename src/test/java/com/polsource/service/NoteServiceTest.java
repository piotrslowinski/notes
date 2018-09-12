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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
        when(noteRepository.findByTitle(sampleNote.getTitle())).thenReturn(Optional.empty());

        //when
        Note note = this.noteService.createNote(sampleNote);

        //then
        assertEquals("Note 1", note.getTitle());
        assertEquals("Note 1 content", note.getContent());
    }

    @Test
    public void shouldAssignProperDateWhenAddNewNote() {
        //given
        when(noteRepository.findByTitle(sampleNote.getTitle())).thenReturn(Optional.empty());

        //when
        Note note = this.noteService.createNote(sampleNote);

        //then
        assertEquals(LocalDate.now(), note.getCreated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowErrorWhenAddSameTitleNote() {
        //given
        when(noteRepository.findByTitle(sampleNote.getTitle())).thenReturn(Optional.of(sampleNote));

        //when
        Note newNote = new Note("Note 1", "Alternative content");

        this.noteService.createNote(newNote);
    }
}
