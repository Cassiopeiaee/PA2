package com.example.nutzerverwaltung;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTests {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private UserService userService;

    public UserServiceTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() throws SQLException {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setUsername("testuser");
        user.setrolle(rolle.Admin);

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        Users createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("test@example.com", createdUser.getEmail());
        verify(statement, times(1)).executeUpdate();
    }

    @Test
    public void testCreateUserWithExistingId() throws SQLException {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setUsername("testuser");
        user.setrolle(rolle.Admin);

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(new SQLException("Duplicate entry"));

        assertThrows(RuntimeException.class, () -> userService.createUser(user));
    }

    @Test
    public void testAssignInvalidRole() {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setUsername("testuser");

        assertThrows(IllegalArgumentException.class, () -> user.setrolle(rolle.Admin.valueOf("INVALID_ROLE")));
    }

    @Test
    public void testCreateUserWithExceedingLength() throws SQLException {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("a".repeat(256) + "@example.com");  // Assuming email length should not exceed 255 characters
        user.setPassword("password");
        user.setUsername("testuser");
        user.setrolle(rolle.Admin);

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(new SQLException("Data too long for column"));

        assertThrows(RuntimeException.class, () -> userService.createUser(user));
    }
}
