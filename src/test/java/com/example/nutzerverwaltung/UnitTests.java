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
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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
    private Users adminUser;
    private AdminUserRequest.Admin admin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new Users();
        user.setId(10L);
        user.setUsername("UserTest");
        user.setPassword("password");
        user.setEmail("User@Test.de");
        user.setrolle(Rolle.LESER);

        adminUser = new Users();
        adminUser.setId(9L);
        adminUser.setUsername("admin");
        adminUser.setPassword("adminpassword");
        adminUser.setEmail("admin@example.com");
        adminUser.setrolle(Rolle.ADMIN);

        Users targetUser = new Users();
        targetUser.setId(11L);
        targetUser.setUsername("targetUser");
        targetUser.setPassword("targetpassword");
        targetUser.setEmail("target@Test.de");
        targetUser.setrolle(Rolle.LESER);

        admin = new AdminUserRequest.Admin();
        admin.setUsername("admin");
        admin.setPassword("adminpassword");
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
    public void testGetUserById_UserFound() throws SQLException {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
             ResultSet mockResultSet = mock(ResultSet.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT * FROM users WHERE id = ?")).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(user.getId());
            when(mockResultSet.getString("username")).thenReturn(user.getUsername());
            when(mockResultSet.getString("password")).thenReturn(user.getPassword());
            when(mockResultSet.getString("email")).thenReturn(user.getEmail());
            when(mockResultSet.getString("role")).thenReturn(user.getRolle().name());

            Users foundUser = userService.getUserById(user.getId());

            assertNotNull(foundUser);
            assertEquals(user.getId(), foundUser.getId());
            assertEquals(user.getUsername(), foundUser.getUsername());
            assertEquals(user.getPassword(), foundUser.getPassword());
            assertEquals(user.getEmail(), foundUser.getEmail());
            assertEquals(user.getRolle(), foundUser.getRolle());

            verify(mockPreparedStatement).setLong(1, user.getId());
            verify(mockPreparedStatement).executeQuery();
        }
    }


    @Test
    public void testGetUserById_UserNotFound() throws SQLException {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             PreparedStatement mockPreparedStatement = mock(PreparedStatement.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT * FROM users WHERE id = ?")).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenAnswer(invocation -> {
                ResultSet mockResultSet = mock(ResultSet.class);
                when(mockResultSet.next()).thenReturn(false);
                return mockResultSet;
            });

            Users foundUser = userService.getUserById(3L);

            assertNull(foundUser);

            verify(mockPreparedStatement).setLong(1, 3L);
            verify(mockPreparedStatement).executeQuery();
        }
    }


    @Test
    public void testUpdateUserWithExistingUsernameOrEmail() throws SQLException {
        AdminUserRequest request = new AdminUserRequest();
        request.setAdmin(admin);
        AdminUserRequest.UpdatedUser updatedUserDetails = new AdminUserRequest.UpdatedUser();
        updatedUserDetails.setId(10L);
        updatedUserDetails.setUsername("ExistingUser");
        updatedUserDetails.setPassword("newpassword");
        updatedUserDetails.setEmail("existinguser@example.com");
        updatedUserDetails.setRolle("ADMIN");
        request.setUpdatedUser(updatedUserDetails);

        MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockCheckStatement = mock(PreparedStatement.class);
        PreparedStatement mockAuthenticateStatement = mock(PreparedStatement.class);
        PreparedStatement mockUpdateStatement = mock(PreparedStatement.class);
        PreparedStatement mockUpdateIdStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSet mockCheckResultSet = mock(ResultSet.class);

        try {
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT COUNT(*) FROM users WHERE (username = ? OR email = ?) AND id != ?"))
                    .thenReturn(mockCheckStatement);

            when(mockCheckStatement.executeQuery()).thenReturn(mockCheckResultSet);
            when(mockCheckResultSet.next()).thenReturn(true);
            when(mockCheckResultSet.getInt(1)).thenReturn(1);

            when(mockConnection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?"))
                    .thenReturn(mockAuthenticateStatement);

            when(mockAuthenticateStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(adminUser.getId());
            when(mockResultSet.getString("username")).thenReturn(adminUser.getUsername());
            when(mockResultSet.getString("password")).thenReturn(adminUser.getPassword());
            when(mockResultSet.getString("email")).thenReturn(adminUser.getEmail());
            when(mockResultSet.getString("role")).thenReturn(adminUser.getRolle().name());

            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                userService.updateUser(user.getId(), request);
            });

            assertNotNull(exception);
        } finally {
            driverManagerMockedStatic.close();
        }
    }












    @Test
    public void testDeleteUserSuccess() throws SQLException {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setUsername("admin");
        deleteUserRequest.setPassword("adminpassword");
        deleteUserRequest.setTargetUserId(11L);

        // Mock the authenticateUser method
        UserService spyUserService = spy(userService);
        doReturn(adminUser).when(spyUserService).authenticateUser("admin", "adminpassword");

        // Mock the database connection and statement
        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             PreparedStatement mockPreparedStatement = mock(PreparedStatement.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            boolean result = spyUserService.deleteUser(deleteUserRequest);

            assertTrue(result);
            verify(mockPreparedStatement).setLong(1, 11L);
            verify(mockPreparedStatement).executeUpdate();
        }
    }


    @Test
    public void testDeleteUserFailureNotAdmin() throws SQLException {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setUsername("UserTest");
        deleteUserRequest.setPassword("password");
        deleteUserRequest.setTargetUserId(11L);

        UserService spyUserService = spy(userService);
        doReturn(user).when(spyUserService).authenticateUser("UserTest", "password");

        boolean result = spyUserService.deleteUser(deleteUserRequest);

        assertFalse(result);
    }
}
