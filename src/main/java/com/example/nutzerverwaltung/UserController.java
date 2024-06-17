package com.example.nutzerverwaltung;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UsersRepository repository;
    private final UserService userService;

    public UserController(UsersRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }


    @PostMapping("/users/create")
    Users createUser(@RequestBody Users user) {
        return userService.createUser(user);
    }

    @PutMapping("/users/{id}/update")
    public ResponseEntity<String> updateUser(@RequestBody AdminUserRequest request, @PathVariable Long id) {
        try{
            System.out.println("Admin Username: " + request.getAdmin().getUsername());
            System.out.println("Update User ID: " + request.getUpdatedUser().getId());

            Users updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok("Nutzer wurde erfolgreich Verändert");
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(403).body("Nur ein Admin kann einen Nutzer verändern");
        }catch(RuntimeException e){
            return ResponseEntity.status(500).body(e.getMessage());
        }

    }


    @GetMapping(value ="/users/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUsers() {
        return userService.getUsers();
    }


    @DeleteMapping("/users/delete")
    public ResponseEntity<String> deleteUser(@RequestBody DeleteUserRequest request) {
        boolean success = userService.deleteUser(request);
        if (!success){
            return ResponseEntity.status(403).body("Fehler: Nutzer konnte nicht gelöscht werden");
        }
        return ResponseEntity.ok("Nutzer wurde gelöscht");
    }


    @GetMapping(value = "/users/get/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        Users user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}

