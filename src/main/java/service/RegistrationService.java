package service;

import constants.RegistrationStatus;

import java.sql.SQLException;

public class RegistrationService
{
    public static RegistrationStatus registration(final String username, final String password, final String passwordVerification)
    {
        try
        {
            if (!password.equals(passwordVerification))
            {
                return RegistrationStatus.PASSWORD_NOT_EQUAL;
            }

            if (DatabaseService.GetNumberOfUsername(username).size() < 1)
            {
                // TODO add label for firstname and lastname here
                DatabaseService.createNewUser(username, password, username, username);
                return RegistrationStatus.USER_CREATED;
            }
            else
            {
                return RegistrationStatus.NOT_UNIQUE_USERNAME;
            }
        }
        catch (SQLException e)
        {
            System.out.println("No users found for given combination");
        }
        return null;
    }

    public static RegistrationStatus updatePassword(final String username, final String password, final String passwordVerification)
    {
        try
        {
            if (!password.equals(passwordVerification))
            {
                return RegistrationStatus.PASSWORD_NOT_EQUAL;
            }

            if (DatabaseService.GetNumberOfUsername(username).size() == 1)
            {
                DatabaseService.updateUserPassword(username, password);
                return RegistrationStatus.PASSWORD_UPDATED;
            }
            else
            {
                return RegistrationStatus.USERNAME_NOT_FOUND;
            }
        }
        catch (SQLException e)
        {
            System.out.println("No users found for given combination");
        }
        return null;
    }
}
