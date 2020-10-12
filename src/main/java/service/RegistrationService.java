package service;

import constants.RegistrationStatus;
import constants.UserRoles;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

public class RegistrationService {

    public static RegistrationStatus registration(final String username, final String firstname, final String lastname, final String password, final String passwordVerification) {
        try {
            if(StringUtils.isEmpty(StringUtils.trim(firstname)) || StringUtils.isEmpty(StringUtils.trim(lastname))) {
                return RegistrationStatus.NAME_IS_EMPTY;
            }
            if (!password.equals(passwordVerification)) {
                return RegistrationStatus.PASSWORD_NOT_EQUAL;
            }

            if (DatabaseService.GetNumberOfUsername(username).size() < 1) {
                DatabaseService.createNewUser(username, password, firstname, lastname);
                DatabaseService.createNewUserRole(username, UserRoles.STRANGER.toString());
                return RegistrationStatus.USER_CREATED;
            }
            else {
                return RegistrationStatus.NOT_UNIQUE_USERNAME;
            }
        }
        catch (SQLException e) {
            System.out.println("No users found for given combination");
        }
        return null;
    }

    public static RegistrationStatus updatePassword(final String username, final String password, final String passwordVerification) {
        try {
            if (!password.equals(passwordVerification)) {
                return RegistrationStatus.PASSWORD_NOT_EQUAL;
            }

            if (DatabaseService.GetNumberOfUsername(username).size() == 1) {
                DatabaseService.updateUserPassword(username, password);
                return RegistrationStatus.PASSWORD_UPDATED;
            }
            else {
                return RegistrationStatus.USERNAME_NOT_FOUND;
            }
        }
        catch (SQLException e) {
            System.out.println("No users found for given combination");
        }
        return null;
    }
}
