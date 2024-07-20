package com.example.nutzerverwaltung;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;

@SpringBootTest
public class UserServiceTests {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserService userService;
    private AdminUserRequest.Admin admin;
    private Users user;
    private Users adminUser;

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
        adminUser.setId(1L);
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

    @Test
    public void testIsUsernameTaken() throws SQLException {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
             ResultSet mockResultSet = mock(ResultSet.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(1); // Username is taken

            boolean isTaken = userService.isUsernameTaken("UserTest");

            assertTrue(isTaken);
            verify(mockPreparedStatement).setString(1, "UserTest");
            verify(mockPreparedStatement).executeQuery();
        }
    }


    @Test
    public void testGetUsers() throws SQLException, JSONException {
        JSONArray expectedUsers = new JSONArray();
        JSONObject userObject1 = new JSONObject();
        userObject1.put("id", 1L);
        userObject1.put("username", "admin");
        userObject1.put("email", "admin@example.com");
        userObject1.put("password", "adminpassword");
        userObject1.put("role", "ADMIN");
        expectedUsers.put(userObject1);

        JSONObject userObject2 = new JSONObject();
        userObject2.put("id", 2L);
        userObject2.put("username", "user");
        userObject2.put("email", "user@example.com");
        userObject2.put("password", "userpassword");
        userObject2.put("role", "LESER");
        expectedUsers.put(userObject2);

        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             Statement mockStatement = mock(Statement.class);
             ResultSet mockResultSet = mock(ResultSet.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery("SELECT * FROM users")).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getLong("id")).thenReturn(1L, 2L);
            when(mockResultSet.getString("username")).thenReturn("admin", "user");
            when(mockResultSet.getString("email")).thenReturn("admin@example.com", "user@example.com");
            when(mockResultSet.getString("password")).thenReturn("adminpassword", "userpassword");
            when(mockResultSet.getString("role")).thenReturn("ADMIN", "LESER");

            String result = userService.getUsers();
            assertEquals(expectedUsers.toString(), result);
        }
    }



    @Test
    public void testAuthenticateUserSuccess() throws SQLException {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
             ResultSet mockResultSet = mock(ResultSet.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(adminUser.getId());
            when(mockResultSet.getString("username")).thenReturn(adminUser.getUsername());
            when(mockResultSet.getString("password")).thenReturn(adminUser.getPassword());
            when(mockResultSet.getString("email")).thenReturn(adminUser.getEmail());
            when(mockResultSet.getString("role")).thenReturn(adminUser.getRolle().name());

            Users authenticatedUser = userService.authenticateUser("admin", "adminpassword");

            assertNotNull(authenticatedUser);
            assertEquals(adminUser.getId(), authenticatedUser.getId());
            assertEquals(adminUser.getUsername(), authenticatedUser.getUsername());
            assertEquals(adminUser.getPassword(), authenticatedUser.getPassword());
            assertEquals(adminUser.getEmail(), authenticatedUser.getEmail());
            assertEquals(adminUser.getRolle(), authenticatedUser.getRolle());

            verify(mockPreparedStatement).setString(1, "admin");
            verify(mockPreparedStatement).setString(2, "adminpassword");
            verify(mockPreparedStatement).executeQuery();
        }
    }

    @Test
    public void testAuthenticateUserFailure() throws SQLException {
        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class);
             Connection mockConnection = mock(Connection.class);
             PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
             ResultSet mockResultSet = mock(ResultSet.class)) {

            driverManagerMockedStatic.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(false);

            Users authenticatedUser = userService.authenticateUser("admin", "wrongpassword");

            assertNull(authenticatedUser);

            verify(mockPreparedStatement).setString(1, "admin");
            verify(mockPreparedStatement).setString(2, "wrongpassword");
            verify(mockPreparedStatement).executeQuery();
        }
    }

}

