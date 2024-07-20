package com.example.nutzerverwaltung;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AdminUserRequestTests {

    private AdminUserRequest adminUserRequest;
    private AdminUserRequest.Admin admin;
    private AdminUserRequest.UpdatedUser updatedUser;

    @BeforeEach
    public void setUp() {
        adminUserRequest = new AdminUserRequest();
        admin = new AdminUserRequest.Admin();
        updatedUser = new AdminUserRequest.UpdatedUser();
    }

    @Test
    public void testSetAndGetAdmin() {
        admin.setUsername("adminUsername");
        admin.setPassword("adminPassword");
        adminUserRequest.setAdmin(admin);

        assertEquals("adminUsername", adminUserRequest.getAdmin().getUsername());
        assertEquals("adminPassword", adminUserRequest.getAdmin().getPassword());
    }

    @Test
    public void testSetAndGetUpdatedUser() {
        updatedUser.setId(1L);
        updatedUser.setUsername("updatedUsername");
        updatedUser.setPassword("updatedPassword");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRolle("ADMIN");
        adminUserRequest.setUpdatedUser(updatedUser);

        assertEquals(1L, adminUserRequest.getUpdatedUser().getId());
        assertEquals("updatedUsername", adminUserRequest.getUpdatedUser().getUsername());
        assertEquals("updatedPassword", adminUserRequest.getUpdatedUser().getPassword());
        assertEquals("updated@example.com", adminUserRequest.getUpdatedUser().getEmail());
        assertEquals("ADMIN", adminUserRequest.getUpdatedUser().getRolle());
    }

    @Test
    public void testAdminSetAndGetUsername() {
        admin.setUsername("adminUsername");
        assertEquals("adminUsername", admin.getUsername());
    }

    @Test
    public void testAdminSetAndGetPassword() {
        admin.setPassword("adminPassword");
        assertEquals("adminPassword", admin.getPassword());
    }

    @Test
    public void testUpdatedUserSetAndGetId() {
        updatedUser.setId(1L);
        assertEquals(1L, updatedUser.getId());
    }

    @Test
    public void testUpdatedUserSetAndGetUsername() {
        updatedUser.setUsername("updatedUsername");
        assertEquals("updatedUsername", updatedUser.getUsername());
    }

    @Test
    public void testUpdatedUserSetAndGetPassword() {
        updatedUser.setPassword("updatedPassword");
        assertEquals("updatedPassword", updatedUser.getPassword());
    }

    @Test
    public void testUpdatedUserSetAndGetEmail() {
        updatedUser.setEmail("updated@example.com");
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    public void testUpdatedUserSetAndGetRolle() {
        updatedUser.setRolle("ADMIN");
        assertEquals("ADMIN", updatedUser.getRolle());
    }
}

