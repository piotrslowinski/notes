package com.polsource.notes.repository;

import com.polsource.notes.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class JPQLNoteFinder implements NoteFinder {

    private EntityManager entityManager;

    @Autowired
    public JPQLNoteFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Note> findNotesByTitle(String title) {
        Query query = entityManager.createQuery("SELECT n FROM Note n WHERE n.title = :title");
        query.setParameter("title", title);
        List<Note> notes = query.getResultList();
        return notes;
    }

    @Override
    public Optional<Note> findCurrentNoteByTitle(String title) {
        Query query = entityManager.createQuery("SELECT n from Note n WHERE n.title = :title AND n.version = (SELECT MAX(nn.version) FROM Note nn WHERE nn.title = n.title AND nn.active IS true)");
        query.setParameter("title", title);
        Note note = (Note) query.setMaxResults(1).getSingleResult();
        return Optional.of(note);

    }

    @Override
    public List<Note> findCurrentNotes() {
        Query query = entityManager.createQuery("SELECT n FROM Note n WHERE n.version = (SELECT MAX(nn.version) FROM Note nn WHERE nn.title = n.title) GROUP BY n.title ");
        List<Note> notes = query.getResultList();
        return notes;
    }

    @Override
    public Optional<Note> getActiveNoteById(Long id) {
        try {
            Note note = (Note) entityManager.createQuery("SELECT n FROM Note n WHERE n.id = :id AND n.active IS true")
                    .setParameter("id", id).getSingleResult();
            return Optional.of(note);
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }


}
