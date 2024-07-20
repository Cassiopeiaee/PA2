package com.example.nutzerverwaltung;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserControllerTests {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private Users user;
    private AdminUserRequest adminUserRequest;
    private DeleteUserRequest deleteUserRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        user = new Users();
        user.setId(10L);
        user.setUsername("UserTest");
        user.setPassword("password");
        user.setEmail("User@Test.de");
        user.setrolle(Rolle.LESER);

        adminUserRequest = new AdminUserRequest();
        AdminUserRequest.Admin admin = new AdminUserRequest.Admin();
        admin.setUsername("admin");
        admin.setPassword("adminpassword");
        AdminUserRequest.UpdatedUser updatedUser = new AdminUserRequest.UpdatedUser();
        updatedUser.setId(10L);
        updatedUser.setUsername("UpdatedUser");
        updatedUser.setPassword("updatedPassword");
        updatedUser.setEmail("updated@Test.de");
        updatedUser.setRolle("ADMIN");
        adminUserRequest.setAdmin(admin);
        adminUserRequest.setUpdatedUser(updatedUser);

        deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setUsername("admin");
        deleteUserRequest.setPassword("adminpassword");
        deleteUserRequest.setTargetUserId(11L);
    }

    @Test
    public void testCreateUserController() {
        when(userService.createUser(any(Users.class))).thenReturn(user);

        Users createdUser = userController.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getPassword(), createdUser.getPassword());
        assertEquals(user.getUsername(), createdUser.getUsername());
        assertEquals(user.getRolle(), createdUser.getRolle());

        verify(userService).createUser(any(Users.class));
    }

    @Test
    public void testUpdateUserSuccessController() {
        when(userService.updateUser(anyLong(), any(AdminUserRequest.class))).thenReturn(user);

        ResponseEntity<String> response = userController.updateUser(adminUserRequest, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nutzer wurde erfolgreich Verändert", response.getBody());

        verify(userService).updateUser(anyLong(), any(AdminUserRequest.class));
    }

    @Test
    public void testUpdateUserUnauthorizedController() {
        when(userService.updateUser(anyLong(), any(AdminUserRequest.class))).thenThrow(new IllegalArgumentException("Nur ein Admin kann einen Nutzer verändern"));

        ResponseEntity<String> response = userController.updateUser(adminUserRequest, 10L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Nur ein Admin kann einen Nutzer verändern", response.getBody());

        verify(userService).updateUser(anyLong(), any(AdminUserRequest.class));
    }

    @Test
    public void testUpdateUserRuntimeExceptionController() {
        when(userService.updateUser(anyLong(), any(AdminUserRequest.class))).thenThrow(new RuntimeException("Internal Server Error"));

        ResponseEntity<String> response = userController.updateUser(adminUserRequest, 10L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody());

        verify(userService).updateUser(anyLong(), any(AdminUserRequest.class));
    }

    @Test
    public void testGetUsersController() {
        String usersJson = "[{\"id\":1,\"username\":\"user1\",\"email\":\"user1@example.com\",\"password\":\"password1\",\"role\":\"LESER\"}]";
        when(userService.getUsers()).thenReturn(usersJson);

        String response = userController.getUsers();

        assertEquals(usersJson, response);
        verify(userService).getUsers();
    }

    @Test
    public void testDeleteUserSuccessController() {
        when(userService.deleteUser(any(DeleteUserRequest.class))).thenReturn(true);

        ResponseEntity<String> response = userController.deleteUser(deleteUserRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nutzer wurde gelöscht", response.getBody());

        verify(userService).deleteUser(any(DeleteUserRequest.class));
    }

    @Test
    public void testDeleteUserFailureController() {
        when(userService.deleteUser(any(DeleteUserRequest.class))).thenReturn(false);

        ResponseEntity<String> response = userController.deleteUser(deleteUserRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Fehler: Nutzer konnte nicht gelöscht werden", response.getBody());

        verify(userService).deleteUser(any(DeleteUserRequest.class));
    }

    @Test
    public void testGetUserById_UserFoundController() {
        when(userService.getUserById(anyLong())).thenReturn(user);

        ResponseEntity<Users> response = userController.getUserById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getId());
        assertEquals(user.getEmail(), response.getBody().getEmail());
        assertEquals(user.getPassword(), response.getBody().getPassword());
        assertEquals(user.getUsername(), response.getBody().getUsername());
        assertEquals(user.getRolle(), response.getBody().getRolle());

        verify(userService).getUserById(anyLong());
    }

    @Test
    public void testGetUserById_UserNotFoundController() {
        when(userService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<Users> response = userController.getUserById(10L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(userService).getUserById(anyLong());
    }
}

