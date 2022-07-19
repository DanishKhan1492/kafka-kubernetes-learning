package com.learning.library.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.library.controllers.BookController;
import com.learning.library.dto.Book;
import com.learning.library.dto.LibraryEvent;
import com.learning.library.producers.BookEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {BookController.class})
@AutoConfigureMockMvc
public class LibraryControllerUnitTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookEventProducer bookEventProducer;

    @Test
    void unitTestAddBook() throws Exception {
        Book book = new Book(234, "A Case Of Exploding Mangos", "Muhammad Hanif");
        LibraryEvent libraryEvent = new LibraryEvent(new Random(200000).nextInt(), book);
        doNothing().when(bookEventProducer).sendBookEvent(libraryEvent);

        mockMvc.perform(post("/add")
                .content(new ObjectMapper().writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("Book Created"));
    }

    @Test
    void unitTestUpdateBook() throws Exception {
        Book book = new Book(234, "A Case Of Exploding Mangoes", "Muhammad Hanif");
        LibraryEvent libraryEvent = new LibraryEvent(new Random(200000).nextInt(), book);
        doNothing().when(bookEventProducer).sendUpdateBookEvent(libraryEvent);

        mockMvc.perform(put("/update")
                        .content(new ObjectMapper().writeValueAsString(libraryEvent))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Book Updated"));
    }

    @Test
    void unitTestUpdateLibraryEventWithNullID() throws Exception {
        Book book = new Book(234, "A Case Of Exploding Mangoes", "Muhammad Hanif");
        LibraryEvent libraryEvent = new LibraryEvent(null, book);
        doNothing().when(bookEventProducer).sendUpdateBookEvent(libraryEvent);

        mockMvc.perform(put("/update")
                        .content(new ObjectMapper().writeValueAsString(libraryEvent))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

}
