package com.example.nutzerverwaltung;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Objects;

@Service
public class UserService {

    public Users createUser(Users user) {
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root")) {
            String sql = "INSERT INTO users (id, email, password, username, role) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, user.getId());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPassword());
                statement.setString(4, user.getUsername());
                statement.setString(5, user.getRole().name());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Nutzer erstellung Fehlgeschlagen, keine Zeilen wurden verändert.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Ausführen des SQL Statement", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Verbinden zur Datenbank", e);
        }
        return user;
    }

    public ResponseEntity<String> updateUser(Long id, Users updatedUser) {
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root")) {
            String query = "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, updatedUser.getUsername());
            preparedStatement.setString(2, updatedUser.getEmail());
            preparedStatement.setString(3, updatedUser.getPassword());
            preparedStatement.setString(4, updatedUser.getRole().name());
            preparedStatement.setLong(5, id);


            Long rowsUpdated = (long) preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                if (!Objects.equals(updatedUser.getId(), id)) {
                    String updateIdQuery = "UPDATE users SET id = ? WHERE id = ?";
                    PreparedStatement updateIdStatement = connection.prepareStatement(updateIdQuery);
                    updateIdStatement.setLong(1, updatedUser.getId());
                    updateIdStatement.setLong(2, id);
                    Long idRowsUpdated = (long) updateIdStatement.executeUpdate();
                    if (idRowsUpdated > 0) {
                        return ResponseEntity.ok("Nutzer Update erfolgreich, ID geändert.");
                    } else {
                        return ResponseEntity.status(404).body("Fehler beim Ändern der ID.");
                    }
                }
                return ResponseEntity.ok("Nutzer Update erfolgreich.");
            } else {
                return ResponseEntity.status(404).body("Nutzer nicht gefunden.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler");
        }
    }

    public String getUsers() {
        JSONArray jsonArray = new JSONArray();
        try (Connection connection1 = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root")) {
            Statement statement1 = connection1.createStatement();
            ResultSet result = statement1.executeQuery("SELECT * FROM users");
            while (result.next()) {
                JSONObject userObject = new JSONObject();
                userObject.put("id", result.getLong("id"));
                userObject.put("username", result.getString("username"));
                userObject.put("email", result.getString("email"));
                userObject.put("password", result.getString("password"));
                userObject.put("role", result.getString("role"));
                jsonArray.put(userObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public ResponseEntity<String> deleteUser(Long id) {
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root")) {
            String query = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    return ResponseEntity.status(404).body("Nutzer nicht gefunden");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Ausführen des SQL-Statement", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Verbinden zur Datenbank", e);
        }
        return ResponseEntity.ok("Nutzer erfolgreich gelöscht");
    }
}
