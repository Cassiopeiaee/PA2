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
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody Users updatedUser) {
        return userService.updateUser(id, updatedUser);
    }



    @GetMapping(value ="/users/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUsers() {
        return userService.getUsers();
    }



    @DeleteMapping("/users/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

}
