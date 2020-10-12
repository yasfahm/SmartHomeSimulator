package entity;

import constants.UserRoles;

public class UserRole {
    private String username;
    private UserRoles role;

    public String getUsername() {
        return username;
    }

    public UserRoles getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }
}
