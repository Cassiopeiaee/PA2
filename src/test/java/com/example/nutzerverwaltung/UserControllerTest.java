package com.example.nutzerverwaltung;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.sql.Delete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UsersRepository usersRepository;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        usersRepository.deleteAll();
    }

    @Test
    public void testCreateUser() throws Exception {
        Users user = new Users();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setId(10L);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    public void testGetUser() throws Exception {
        Users user = new Users();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setId(1L);
        usersRepository.save(user);

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("testuser@example.com"))
                .andExpect(jsonPath("$[0].password").value("password"))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testDeleteUser() throws Exception{
        Long userId = 1L;

        when(usersRepository.existsById(userId)).thenReturn(true);
        doNothing().when(usersRepository).deleteById(userId);

        mockMvc.perform(delete("/users/{id}/delete", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Nutzer erfolgreich gelöscht."));
    }


    @Test
    public void testUpdateUser() throws Exception {
        Long userId = 1L;
        Users updatedUser = new Users();
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("testuser@example.com");
        updatedUser.setPassword("password");
        updatedUser.setId(1L);

        Mockito.when(usersRepository.existsById(userId)).thenReturn(true);

        mockMvc.perform(put("/users/{id}/update", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("Nutzer Update erfolgreich."));
    }

}
