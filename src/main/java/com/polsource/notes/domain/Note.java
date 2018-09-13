package com.polsource.notes.domain;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private LocalDate created;

    private LocalDate modified;

    private int version;

    public Note() {
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.created = LocalDate.now();
        this.version = 0;
    }

    public Note(String title, String content, LocalDate created, int version) {
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = LocalDate.now();
        this.version = version;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getCreated() {
        return this.created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getModified() {
        return this.modified;
    }

    public void setModified(LocalDate modified) {
        this.modified = modified;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
