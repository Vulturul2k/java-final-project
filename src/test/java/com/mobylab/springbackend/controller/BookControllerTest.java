package com.mobylab.springbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobylab.springbackend.entity.Book;
import com.mobylab.springbackend.service.BookServie;
import com.mobylab.springbackend.service.dto.BookDto;
import com.mobylab.springbackend.service.dto.BookWithOwnerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookServie bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllBooks_ShouldReturnOk() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/book/all"))
                .andExpect(status().isOk());
    }

    @Test
    void getBooksByAuthor_ShouldReturnOk_WhenFound() throws Exception {
        when(bookService.getBooksByAuthor(anyString())).thenReturn(List.of(new BookWithOwnerDto()));

        mockMvc.perform(get("/book/getByAuthor").param("author", "Author"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser // Simulate a user to pass any security checks if they were active
    void addBook_ShouldReturnCreated() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setTitle("Title");
        bookDto.setAuthor("Author");

        when(bookService.addBook(any(BookDto.class))).thenReturn(new Book());

        mockMvc.perform(post("/book/addBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteBookByTitle_ShouldReturnNoContent_WhenDeleted() throws Exception {
        when(bookService.deleteBookByTitleAndOwner(anyString(), any())).thenReturn(true);

        mockMvc.perform(delete("/book/deleteByTitle")
                .param("title", "Title")
                .principal(() -> "user"))
                .andExpect(status().isNoContent());
    }
}
