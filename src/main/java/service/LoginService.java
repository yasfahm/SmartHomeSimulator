package service;

import java.sql.SQLException;
import java.util.Map;

public class LoginService {
    public static Map<String, Object> login(final String username, final String password) {
        try {
            return DatabaseService.verifyLogin(username, password);
        }
        catch (SQLException e) {
            System.out.println("No users found for given combination");
        }
        return null;
    }
}
