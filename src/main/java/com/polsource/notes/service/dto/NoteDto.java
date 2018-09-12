package com.polsource.notes.service.dto;

import java.time.LocalDate;

public class NoteDto {

    private String title;

    private String content;

    private LocalDate created;

    private LocalDate modified;

    public NoteDto(String title, String content, LocalDate created, LocalDate modified) {
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }
}
