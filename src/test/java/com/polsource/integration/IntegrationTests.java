package com.polsource.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polsource.notes.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
public class IntegrationTests {

    private static final String TRUNCATE_SCHEMA_QUERY = "TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void cleanDb() {
        clean();
    }

    @Test
    public void successfulNoteSave() throws Exception {
        save("New note", "New Content").andExpect(status().isOk());
    }

    @Test
    public void missingTitleWhenSaveNote() throws Exception {
        save(null, "New Content").andExpect(status().isBadRequest());
    }

    @Test
    public void missingContentWhenSaveNote() throws Exception {
        save("Title", null).andExpect(status().isBadRequest());
    }

    @Test
    public void savingNoteWithTheSameTitleTwice() throws Exception {
        save("New note", "Content").andExpect(status().isOk());
        save("New note", "New content").andExpect(status().isBadRequest());
    }

    @Test
    public void successfulUpdate() throws Exception {
        save("New note", "Content").andExpect(status().isOk());
        update("New note", "New Content").andExpect(status().isOk());
    }

    @Test
    public void missingContentWhenUpdate() throws Exception {
        save("New note", "Content").andExpect(status().isOk());
        update("New note", null).andExpect(status().isBadRequest());
    }

    @Test
    public void notExistingNoteToUpdate() throws Exception {
        update("New note", "New Content").andExpect(status().isBadRequest());
    }

    @Test
    public void successfulDeletingNote() throws Exception {
        save("New note", "Content").andExpect(status().isOk());
        delete("New note").andExpect(status().isOk());
    }

    @Test
    public void notExistingNoteToDelete() throws Exception {
        delete("New note").andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnCurrentNoteByTitle() throws Exception {
        save("New note", "Content").andExpect(status().isOk());
        getCurrentNoteById(1L).andExpect(status().isOk());
    }

    @Test
    public void tryingToGetDeletedNoteAsCurrentById() throws Exception {
        save("New note", "Content").andExpect(status().isOk());
        delete("New note").andExpect(status().isOk());
        getCurrentNoteById(1L).andExpect(status().isNotFound());
    }

    private ResultActions getCurrentNoteById(Long id) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .get("/notes/"+id)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions delete(String title) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .delete("/notes/" + title)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions update(String title, String content) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("content", content);
        return mvc.perform(post("/notes/" + title).content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions save(String title, String content) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("content", content);
        return mvc.perform(post("/notes").content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON));
    }

    public void clean() {
        entityManager.createNativeQuery(TRUNCATE_SCHEMA_QUERY).executeUpdate();
    }
}
