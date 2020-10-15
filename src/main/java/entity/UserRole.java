package entity;

import constants.UserRoles;

/**
 * Database entity for User Roles
 */
public class UserRole {
    /**
     * Variable for user's username
     */
    private String username;
    /**
     * Variable for username's role
     */
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
