package service;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseService
{
    private static Connection databaseConnection;
    private static QueryRunner queryRunner;

    public static void SetupDBController(String databaseUrl) throws SQLException
    {
        queryRunner = new QueryRunner();
        databaseConnection = DriverManager.getConnection(databaseUrl, "root", "");
    }

    private static int update(String command) throws SQLException
    {
        return queryRunner.update(databaseConnection, command);
    }

    private static <T> T insert(String command, ResultSetHandler<T> handler) throws SQLException
    {
        return queryRunner.insert(databaseConnection, command, handler);
    }

    private static <T> T query(String command, ResultSetHandler<T> handler) throws SQLException
    {
        return queryRunner.query(databaseConnection, command, handler);
    }

    public static Map<String, Object> verifyLogin(final String username, final String password) throws SQLException
    {
        return query("SELECT firstname, lastname FROM users WHERE username = \"" + username + "\" AND password = \"" + password + "\"", new MapHandler());
    }

    public static List<String> GetNumberOfUsername(final String username) throws SQLException
    {
        return query("SELECT username FROM users WHERE username LIKE \"" + username + "\"", new ColumnListHandler<>());
    }

    public static List<String> createNewUser(final String username, final String password, final String firstname, final String lastname) throws SQLException
    {
        return insert("INSERT INTO users VALUES (\""+ username +"\", \""+ password +"\", \""+ firstname +"\", \""+ lastname +"\")", new ColumnListHandler<>());
    }

    public static int updateUserPassword(final String username, final String password) throws SQLException
    {
        return update("UPDATE users SET password = \"" + password + "\" WHERE username LIKE \"" + username + "\"");
    }
}
