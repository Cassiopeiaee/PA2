package com.example.nutzerverwaltung;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeleteUserRequestTests {

    private DeleteUserRequest deleteUserRequest;

    @BeforeEach
    public void setUp() {
        deleteUserRequest = new DeleteUserRequest();
    }

    @Test
    public void testSetAndGetUsername() {
        deleteUserRequest.setUsername("username");
        assertEquals("username", deleteUserRequest.getUsername());
    }

    @Test
    public void testSetAndGetPassword() {
        deleteUserRequest.setPassword("password");
        assertEquals("password", deleteUserRequest.getPassword());
    }

    @Test
    public void testSetAndGetTargetUserId() {
        deleteUserRequest.setTargetUserId(1L);
        assertEquals(1L, deleteUserRequest.getTargetUserId());
    }
    @Test
    public void testSetAndGetAdmin() {
        AdminUserRequest.Admin admin = new AdminUserRequest.Admin();
        admin.setUsername("adminUsername");
        admin.setPassword("adminPassword");

        deleteUserRequest.setAdmin(admin);
        assertEquals("adminUsername", deleteUserRequest.getAdmin().getUsername());
        assertEquals("adminPassword", deleteUserRequest.getAdmin().getPassword());
    }
}

