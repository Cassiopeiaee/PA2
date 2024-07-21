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

        System.out.println("Admin Username: " + adminUserRequest.getAdmin().getUsername());
        System.out.println("Admin Password: " + adminUserRequest.getAdmin().getPassword());

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

        System.out.println("Updated User ID: " + adminUserRequest.getUpdatedUser().getId());
        System.out.println("Updated User Username: " + adminUserRequest.getUpdatedUser().getUsername());
        System.out.println("Updated User Password: " + adminUserRequest.getUpdatedUser().getPassword());
        System.out.println("Updated User Email: " + adminUserRequest.getUpdatedUser().getEmail());
        System.out.println("Updated User Rolle: " + adminUserRequest.getUpdatedUser().getRolle());

        assertEquals(1L, adminUserRequest.getUpdatedUser().getId());
        assertEquals("updatedUsername", adminUserRequest.getUpdatedUser().getUsername());
        assertEquals("updatedPassword", adminUserRequest.getUpdatedUser().getPassword());
        assertEquals("updated@example.com", adminUserRequest.getUpdatedUser().getEmail());
        assertEquals("ADMIN", adminUserRequest.getUpdatedUser().getRolle());
    }

    @Test
    public void testAdminSetAndGetUsername() {
        admin.setUsername("adminUsername");
        System.out.println("Admin Username: " + admin.getUsername());
        assertEquals("adminUsername", admin.getUsername());
    }

    @Test
    public void testAdminSetAndGetPassword() {
        admin.setPassword("adminPassword");
        System.out.println("Admin Password: " + admin.getPassword());
        assertEquals("adminPassword", admin.getPassword());
    }

    @Test
    public void testUpdatedUserSetAndGetId() {
        updatedUser.setId(1L);
        System.out.println("Updated User ID: " + updatedUser.getId());
        assertEquals(1L, updatedUser.getId());
    }

    @Test
    public void testUpdatedUserSetAndGetUsername() {
        updatedUser.setUsername("updatedUsername");
        System.out.println("Updated User Username: " + updatedUser.getUsername());
        assertEquals("updatedUsername", updatedUser.getUsername());
    }

    @Test
    public void testUpdatedUserSetAndGetPassword() {
        updatedUser.setPassword("updatedPassword");
        System.out.println("Updated User Password: " + updatedUser.getPassword());
        assertEquals("updatedPassword", updatedUser.getPassword());
    }

    @Test
    public void testUpdatedUserSetAndGetEmail() {
        updatedUser.setEmail("updated@example.com");
        System.out.println("Updated User Email: " + updatedUser.getEmail());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    public void testUpdatedUserSetAndGetRolle() {
        updatedUser.setRolle("ADMIN");
        System.out.println("Updated User Rolle: " + updatedUser.getRolle());
        assertEquals("ADMIN", updatedUser.getRolle());
    }
}
