# Notes-App
---
Used technologies:

1. Java 8
2. Maven
3. Spring Boot
4. MySQL DB
5. HSQLDB for tests

---

## 1. About

This app lets you to store notes in db with possibility to update note creating new instance with the same title and incremented version. Original note's version is 0. Every update changes "modified" field for current date. Deleting note finds every note with the same title and sets "active" field to false deactivating the note. Deactivated notes are available to see only via history endpoint by title.

---

Requirements to run the project:
1. JRE8
2. Git
3. Maven
4. IDE (Intellij IDEA)
5. MySQL DB

Scripts to setup database:

...

    - CREATE DATABASE notes;
    - USE notes;
    - CREATE TABLE notes (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           title VARCHAR(255) NOT NULL,
                           content VARCHAR NOT NULL, 
                           created DATE NOT NULL,
                           modified DATE, 
                           version INT,
                           active BOOL
                           );
    
...

How to run:
1. Install and setup MySQL DB
2. Clone repo with git
3. Build project with Maven (clean, install)
4. Run the "Application" class (listen on localhost:8080 by default)

---

## 2. Sample endpoints
1. Creating new note

    *POST/notes*
    
...

       {
            "title":"New title",
            "content": "Sample content"
       }
       
...

2. Update note (only if exists)

    *POST/notes/:title*
    
...

        {
            "content": "New content"
        }
        
...

3. Delete notes (only if exists)

    *DELETE/notes/:title*
    
4. Get all current notes (returns all active notes with latest version)

    *GET/notes*
    
5. Get note history (returns all versions of note active and non-active specified by title)

    *GET/notes/history/:title*
    
6. Get current note specified by id (returns only active current notes by id)

    *GET/notes/:id*