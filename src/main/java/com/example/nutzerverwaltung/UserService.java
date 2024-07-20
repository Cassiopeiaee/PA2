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
        if(isUsernameTaken(user.getUsername())){
            throw new IllegalArgumentException("Username schon vergeben");
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root")) {
            String query = "INSERT INTO users (id, email, password, username, role) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, user.getId());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPassword());
                statement.setString(4, user.getUsername());
                statement.setString(5, user.getRolle().name());

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

    boolean isUsernameTaken(String username){
        try(Connection connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root")){
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, username);
                System.out.println("SQL-Query" + statement.toString());
                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Verbinden zur Datenbank", e);
        }
        return false;
    }


    public Users updateUser(Long id, AdminUserRequest request) {
        Users adminUser = authenticateUser(request.getAdmin().getUsername(), request.getAdmin().getPassword());
        if (adminUser == null || adminUser.getRolle() != Rolle.ADMIN) {
            throw new IllegalArgumentException("Nur ein Admin kann Nutzer ändern.");
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root")) {
            // Update der Nutzerdaten
            String updateQuery = "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, request.getUpdatedUser().getUsername());
                updateStatement.setString(2, request.getUpdatedUser().getEmail());
                updateStatement.setString(3, request.getUpdatedUser().getPassword());
                updateStatement.setString(4, request.getUpdatedUser().getRolle());
                updateStatement.setLong(5, id);

                int rowsUpdated = updateStatement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("Nutzer nicht gefunden.");
                }


                if (!id.equals(request.getUpdatedUser().getId())) {
                    String updateIdQuery = "UPDATE users SET id = ? WHERE id = ?";
                    try (PreparedStatement updateIdStatement = connection.prepareStatement(updateIdQuery)) {
                        updateIdStatement.setLong(1, request.getUpdatedUser().getId());
                        updateIdStatement.setLong(2, id);
                        updateIdStatement.executeUpdate();
                    }
                }

                Users updatedUser = new Users();
                updatedUser.setId(request.getUpdatedUser().getId());
                updatedUser.setUsername(request.getUpdatedUser().getUsername());
                updatedUser.setEmail(request.getUpdatedUser().getEmail());
                updatedUser.setPassword(request.getUpdatedUser().getPassword());
                updatedUser.setrolle(Rolle.valueOf(request.getUpdatedUser().getRolle()));
                return updatedUser;
            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Ausführen des SQL-Statements", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Verbinden zur Datenbank", e);
        }
    }

    public Users getUserById(Long id) {
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Users user = new Users();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setEmail(resultSet.getString("email"));

                String rolleString = resultSet.getString("role");
                if (rolleString != null && !rolleString.isEmpty()) {
                    user.setrolle(Rolle.valueOf(rolleString));
                } else {
                    user.setrolle(null);
                }

                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Verbinden zur Datenbank", e);
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

    public void deleteUser(Long id) {
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root")) {
            String query = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Fehler beim Ausführen des SQL-Statements");
                }

            } catch (SQLException e) {
                throw new RuntimeException("Fehler beim Ausführen des SQL-Statement", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Verbinden zur Datenbank", e);
        }
    }

    public Users authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/users?autoReconnect=true", "root", "root")) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                Users user = new Users();
                user.setId(result.getLong("id"));
                user.setUsername(result.getString("username"));
                user.setEmail(result.getString("email"));
                user.setPassword(result.getString("password"));
                user.setrolle(Rolle.valueOf(result.getString("role")));
                return user;
            }else {
                return null; //Authentification fehlgeschlagen
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Verbinden zur Datenbank");
        }
    }

    public boolean deleteUser(DeleteUserRequest request){
        Users requestUser = authenticateUser(request.getUsername(), request.getPassword());
        if (requestUser == null || requestUser.getRolle() != Rolle.ADMIN) {
            return false;
        }

        deleteUser(request.getTargetUserId());
        return true;
    }
}
