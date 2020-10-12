package service;

import constants.UserRoles;
import entity.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.EnumUtils;

import java.sql.SQLException;
import java.util.List;

public class RoleService {
    public static ObservableList<String> getAllRoles() {
        return FXCollections.observableArrayList(
            UserRoles.PARENT.toString(),
            UserRoles.CHILD.toString(),
            UserRoles.GUEST.toString(),
            UserRoles.STRANGER.toString()
        );
    }

    public static List<UserRole> getRoles() {
        try {
            return DatabaseService.getAllUserRoles();
        }
        catch (SQLException e)
        {
            System.out.println("There are no user roles");
        }
        return null;
    }

    public static void changeRole(final String username, final String role) {
        try {
            if (EnumUtils.isValidEnum(UserRoles.class, role)) {
                DatabaseService.updateUserRole(username, role);
            }
            else {
                System.out.println("Invalid Enum Used");
            }
        }
        catch (SQLException e) {
            System.out.println("Invalid values used");
        }
    }
}
