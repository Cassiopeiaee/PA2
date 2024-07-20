package com.example.nutzerverwaltung;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UsersTests {

    private Users user;

    @BeforeEach
    public void setUp() {
        user = new Users();
        user.setId(10L);
        user.setUsername("UserTest");
        user.setPassword("password");
        user.setEmail("User@Test.de");
        user.setrolle(Rolle.LESER);
    }

    @Test
    public void testSetAndGetId() {
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    public void testSetAndGetUsername() {
        user.setUsername("NewUsername");
        assertEquals("NewUsername", user.getUsername());
    }

    @Test
    public void testSetAndGetPassword() {
        user.setPassword("NewPassword");
        assertEquals("NewPassword", user.getPassword());
    }

    @Test
    public void testSetAndGetEmail() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    public void testSetAndGetRolle() {
        user.setrolle(Rolle.ADMIN);
        assertEquals(Rolle.ADMIN, user.getRolle());
    }

    @Test
    public void testUsersConstructor() {
        String username = "testUser";
        String password = "testPassword";
        String email = "test@example.com";
        Rolle role = Rolle.ADMIN;

        Users user = new Users(username, password, email, role);

        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(role, user.getRolle());
    }
}
