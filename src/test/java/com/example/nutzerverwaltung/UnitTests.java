package com.example.nutzerverwaltung;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class UnitTests {


    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserService userService;

    private Users user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new Users();
        user.setId(10L);
        user.setUsername("UserTest");
        user.setPassword("password");
        user.setEmail("User@Test.de");
        user.setrolle(Rolle.LESER);

        Users adminUser = new Users();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setPassword("adminpassword");
        adminUser.setEmail("admin@example.com");
        adminUser.setrolle(Rolle.ADMIN);
    }

    @Test
    public void testCreateUserSuccess() throws SQLException {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             PreparedStatement mockPreparedStatement = mock(PreparedStatement.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenAnswer(invocation -> {
                ResultSet mockResultSet = mock(ResultSet.class);
                when(mockResultSet.next()).thenReturn(false);
                return mockResultSet;
            });

            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            Users createdUser = userService.createUser(user);

            assertNotNull(createdUser);
            assertEquals(user.getId(), createdUser.getId());
            assertEquals(user.getEmail(), createdUser.getEmail());
            assertEquals(user.getPassword(), createdUser.getPassword());
            assertEquals(user.getUsername(), createdUser.getUsername());
            assertEquals(user.getRolle(), createdUser.getRolle());

            verify(mockPreparedStatement).setLong(1, user.getId());
            verify(mockPreparedStatement).setString(2, user.getEmail());
            verify(mockPreparedStatement).setString(3, user.getPassword());
            verify(mockPreparedStatement).setString(4, user.getUsername());
            verify(mockPreparedStatement).setString(5, user.getRolle().name());
            verify(mockPreparedStatement).executeUpdate();
        }
    }


    @Test
    public void testCreateUserWhenUsernameTaken() throws SQLException {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             PreparedStatement mockPreparedStatement = mock(PreparedStatement.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenAnswer(invocation -> {
                ResultSet mockResultSet = mock(ResultSet.class);
                when(mockResultSet.next()).thenReturn(true);
                when(mockResultSet.getInt(1)).thenReturn(1);
                return mockResultSet;
            });

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.createUser(user);
            });

            assertEquals("Username schon vergeben", exception.getMessage());
        }
    }


    @Test
    public void testCreateUserWith300CharacterUsername() throws SQLException {
        // Generate a username with 300 characters
        StringBuilder usernameBuilder = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            usernameBuilder.append("a");
        }
        String longUsername = usernameBuilder.toString();
        user.setUsername(longUsername);

        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             PreparedStatement mockPreparedStatement = mock(PreparedStatement.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenAnswer(invocation -> {
                ResultSet mockResultSet = mock(ResultSet.class);
                when(mockResultSet.next()).thenReturn(false);
                return mockResultSet;
            });

            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            Users createdUser = userService.createUser(user);

            assertNotNull(createdUser);
            assertEquals(user.getId(), createdUser.getId());
            assertEquals(user.getEmail(), createdUser.getEmail());
            assertEquals(user.getPassword(), createdUser.getPassword());
            assertEquals(user.getUsername(), createdUser.getUsername());
            assertEquals(user.getRolle(), createdUser.getRolle());

            verify(mockPreparedStatement).setLong(1, user.getId());
            verify(mockPreparedStatement).setString(2, user.getEmail());
            verify(mockPreparedStatement).setString(3, user.getPassword());
            verify(mockPreparedStatement).setString(4, user.getUsername());
            verify(mockPreparedStatement).setString(5, user.getRolle().name());
            verify(mockPreparedStatement).executeUpdate();
        }
    }













    @Test
    public void testUpdateUser() {

    }

    @Test
    public void testGetUsers() {
        try {
            Connection connectionMock = mock(Connection.class);
            Statement statementMock = mock(Statement.class);
            ResultSet resultSetMock = mock(ResultSet.class);

            when(connectionMock.createStatement()).thenReturn(statementMock);
            when(statementMock.executeQuery("SELECT * FROM users")).thenReturn(resultSetMock);


            DriverManager.setLogWriter(null);

            String usersJson = userService.getUsers();
            JSONArray jsonArray = new JSONArray(usersJson);
            assertEquals(3, jsonArray.length());

            JSONObject userObject = jsonArray.getJSONObject(0);
            assertEquals(1L, userObject.getLong("id"));
            assertEquals("test", userObject.getString("username"));
            assertEquals("test@test.de", userObject.getString("email"));
            assertEquals("123", userObject.getString("password"));
            assertEquals("ADMIN", userObject.getString("role"));

        } catch (Exception e) {
            fail("Exception occurred while testing getUsers: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(usersRepository).deleteById(anyLong());

        userService.deleteUser(1L);

        verify(usersRepository, times(1)).deleteById(anyLong());
    }
}
