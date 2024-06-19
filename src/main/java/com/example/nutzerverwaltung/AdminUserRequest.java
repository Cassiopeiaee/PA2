package com.example.nutzerverwaltung;

public class AdminUserRequest {
    private Admin admin;
    private UpdatedUser updatedUser;

    // Getter and Setter for admin
    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    // Getter and Setter for updatedUser
    public UpdatedUser getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(UpdatedUser updatedUser) {
        this.updatedUser = updatedUser;
    }

    public static class Admin {
        private String username;
        private String password;


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class UpdatedUser {
        private Long id;
        private String username;
        private String password;
        private String email;
        private String rolle;

        // Getter and Setter for id, username, password, email, rolle
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRolle() {
            return rolle;
        }

        public void setRolle(String rolle) {
            this.rolle = rolle;
        }
    }
}
