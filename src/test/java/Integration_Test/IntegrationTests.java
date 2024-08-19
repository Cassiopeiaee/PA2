package Integration_Test;

import com.example.nutzerverwaltung.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest(classes = NutzerverwaltungApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class IntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsersRepository usersRepository;

    private Users user;
    private Users adminUser;
    private AdminUserRequest.Admin admin;

    private String getRootUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        usersRepository.deleteAll(); // Ensure the database is clean before each test

        user = new Users("User1234", "password", "User1234@Test.de", Rolle.LESER);
        user.setId(101L);

        adminUser = new Users("Admin5678", "adminpass", "admin5678@example.com", Rolle.ADMIN);
        adminUser.setId(102L);

        Users targetUser = new Users("Target12", "targetpw", "target12@Test.de", Rolle.LESER);
        targetUser.setId(103L);

        admin = new AdminUserRequest.Admin();
        admin.setUsername(adminUser.getUsername());
        admin.setPassword(adminUser.getPassword());
    }

    @Test
    public void testUserOperations() {
        System.out.println("Erstelle Admin Benutzer...");
        Users createdAdmin = restTemplate.postForEntity(getRootUrl() + "/users/create", adminUser, Users.class).getBody();
        assertThat(createdAdmin).isNotNull();
        System.out.println("Admin Benutzer erstellt: " + createdAdmin);

        Users newUser = new Users("NewUser1", "newpass1", "newuser1@example.com", Rolle.LESER);
        newUser.setId(104L);

        System.out.println("Erstelle neuen Benutzer...");
        ResponseEntity<Users> postResponse = restTemplate.postForEntity(getRootUrl() + "/users/create", newUser, Users.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Users createdUser = postResponse.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(newUser.getId());
        System.out.println("Neuer Benutzer erstellt: " + createdUser);

        createdUser.setEmail("updateduser@example.com");
        AdminUserRequest adminUserRequest = new AdminUserRequest();
        adminUserRequest.setAdmin(admin);

        AdminUserRequest.UpdatedUser updatedUser = new AdminUserRequest.UpdatedUser();
        updatedUser.setId(createdUser.getId());
        updatedUser.setUsername(createdUser.getUsername());
        updatedUser.setPassword(createdUser.getPassword());
        updatedUser.setEmail(createdUser.getEmail());
        updatedUser.setRolle(createdUser.getRolle().name());

        adminUserRequest.setUpdatedUser(updatedUser);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<AdminUserRequest> entity = new HttpEntity<>(adminUserRequest, headers);

        System.out.println("Aktualisiere Benutzer...");
        ResponseEntity<String> updateResponse = restTemplate.exchange(getRootUrl() + "/users/" + createdUser.getId() + "/update", HttpMethod.PUT, entity, String.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isEqualTo("Nutzer wurde erfolgreich Verändert");
        System.out.println("Benutzer aktualisiert: " + updateResponse.getBody());

        System.out.println("Abrufen des aktualisierten Benutzers...");
        ResponseEntity<Users> getResponse = restTemplate.getForEntity(getRootUrl() + "/users/get/" + createdUser.getId(), Users.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Users fetchedUser = getResponse.getBody();
        assertThat(fetchedUser).isNotNull();
        assertThat(fetchedUser.getId()).isEqualTo(createdUser.getId());
        System.out.println("Aktualisierter Benutzer abgerufen: " + fetchedUser);

        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setUsername(admin.getUsername());
        deleteUserRequest.setPassword(admin.getPassword());
        deleteUserRequest.setTargetUserId(createdUser.getId());
        deleteUserRequest.setAdmin(admin);
        HttpEntity<DeleteUserRequest> deleteEntity = new HttpEntity<>(deleteUserRequest, headers);

        System.out.println("Lösche Benutzer...");
        ResponseEntity<String> deleteResponse = restTemplate.exchange(getRootUrl() + "/users/delete", HttpMethod.DELETE, deleteEntity, String.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isEqualTo("Nutzer wurde gelöscht");
        System.out.println("Benutzer gelöscht: " + deleteResponse.getBody());

        System.out.println("Überprüfe ob der Benutzer gelöscht wurde...");
        ResponseEntity<Users> deletedUserResponse = restTemplate.getForEntity(getRootUrl() + "/users/get/" + createdUser.getId(), Users.class);
        assertThat(deletedUserResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        System.out.println("Überprüfung abgeschlossen: Benutzer existiert nicht mehr.");
    }

    @Test
    public void testCreateAndRetrieveUser() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        user.setId(104L);  // Setze eine neue ID für diesen Test
        HttpEntity<Users> entity = new HttpEntity<>(user, headers);

        ResponseEntity<Users> createResponse = restTemplate.postForEntity(getRootUrl() + "/users/create", entity, Users.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Users createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(104L);  // Überprüfen, ob die ID korrekt gesetzt wurde
        assertThat(createdUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(createdUser.getRolle()).isNotNull();  // Überprüfen, ob die Rolle gesetzt wurde
        assertThat(createdUser.getRolle().name()).isEqualTo(user.getRolle().name());  // Überprüfen, ob der Rollenname korrekt ist

        // Abrufen des erstellten Benutzers
        ResponseEntity<Users> getResponse = restTemplate.getForEntity(getRootUrl() + "/users/get/" + createdUser.getId(), Users.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Users fetchedUser = getResponse.getBody();
        assertThat(fetchedUser).isNotNull();
        assertThat(fetchedUser.getId()).isEqualTo(104L);  // Überprüfen, ob die ID korrekt abgerufen wird
        assertThat(fetchedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(fetchedUser.getRolle()).isNotNull();  // Überprüfen, ob die Rolle vorhanden ist
        assertThat(fetchedUser.getRolle().name()).isEqualTo(user.getRolle().name());  // Überprüfen, ob der Rollenname korrekt ist
    }


    @Test
    public void testUpdateAndRetrieveUser() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        adminUser.setId(106L);  // Setze eine neue ID für diesen Test
        adminUser.setrolle(Rolle.ADMIN);  // Verwenden der ADMIN-Enum-Instanz
        HttpEntity<Users> adminEntity = new HttpEntity<>(adminUser, headers);

        ResponseEntity<Users> createAdminResponse = restTemplate.postForEntity(getRootUrl() + "/users/create", adminEntity, Users.class);
        Users createdAdmin = createAdminResponse.getBody();
        assertThat(createAdminResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createdAdmin).isNotNull();

        user.setId(107L);

    }


    @Test
    public void testDeleteAndVerifyUserNotExists() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        adminUser.setId(109L);  // Setze eine neue ID für diesen Test
        adminUser.setrolle(Rolle.ADMIN);  // Sicherstellen, dass die Rolle gesetzt ist
        HttpEntity<Users> adminEntity = new HttpEntity<>(adminUser, headers);

        ResponseEntity<Users> createAdminResponse = restTemplate.postForEntity(getRootUrl() + "/users/create", adminEntity, Users.class);
        Users createdAdmin = createAdminResponse.getBody();
        assertThat(createAdminResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createdAdmin).isNotNull();

        user.setId(110L);  // Setze eine neue ID für diesen Test
        user.setrolle(Rolle.LESER);  // Sicherstellen, dass die Rolle gesetzt ist
        HttpEntity<Users> userEntity = new HttpEntity<>(user, headers);

        ResponseEntity<Users> createUserResponse = restTemplate.postForEntity(getRootUrl() + "/users/create", userEntity, Users.class);
        Users createdUser = createUserResponse.getBody();
        assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createdUser).isNotNull();

        // Löschen des Benutzers durch den Admin
        DeleteUserRequest deleteRequest = new DeleteUserRequest();
        deleteRequest.setUsername(adminUser.getUsername());  // Admin-Benutzername
        deleteRequest.setPassword(adminUser.getPassword());  // Admin-Passwort
        deleteRequest.setTargetUserId(createdUser.getId());

        HttpEntity<DeleteUserRequest> deleteEntity = new HttpEntity<>(deleteRequest, headers);
        ResponseEntity<String> deleteResponse = restTemplate.exchange(getRootUrl() + "/users/delete", HttpMethod.DELETE, deleteEntity, String.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isEqualTo("Nutzer wurde gelöscht");

        // Überprüfen, ob der Benutzer gelöscht wurde
        try {
            ResponseEntity<Users> getResponse = restTemplate.getForEntity(getRootUrl() + "/users/get/" + createdUser.getId(), Users.class);
            fail("Expected 404 Not Found, but got " + getResponse.getStatusCode());
        } catch (HttpClientErrorException.NotFound e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }


    @Test
    public void testGetUsers() {
        RestTemplate restTemplate = new RestTemplate();

        // Setze die HTTP-Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Erstelle zwei Benutzer
        user.setId(111L);  // Setze eine neue ID für den ersten Testbenutzer
        user.setrolle(Rolle.LESER);
        HttpEntity<Users> userEntity = new HttpEntity<>(user, headers);
        restTemplate.postForEntity(getRootUrl() + "/users/create", userEntity, Users.class);

        Users anotherUser = new Users();
        anotherUser.setId(112L);  // Setze eine neue ID für den zweiten Testbenutzer
        anotherUser.setUsername("anotheruser");
        anotherUser.setPassword("anotherpassword");
        anotherUser.setEmail("anotheruser@example.com");
        anotherUser.setrolle(Rolle.LESER);
        HttpEntity<Users> anotherUserEntity = new HttpEntity<>(anotherUser, headers);
        restTemplate.postForEntity(getRootUrl() + "/users/create", anotherUserEntity, Users.class);

        // Rufe alle Benutzer ab
        ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + "/users/all", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Überprüfe, ob die Antwort die beiden erstellten Benutzer enthält
        String responseBody = response.getBody();
        assertThat(responseBody).contains("User1234");  // Beispiel für einen Benutzernamen
        assertThat(responseBody).contains("anotheruser");
    }




}
