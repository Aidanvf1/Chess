package dataaccess;

import dataaccess.sql.DatabaseInitializer;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SQLAuthDAOTests {
    private SQLAuthDAO authDAO;

    @BeforeAll
    public static void createTables() throws DataAccessException {
        DatabaseInitializer.initialize();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        var userDAO = new SQLUserDAO();
        authDAO.clear();
        userDAO.clear();
        userDAO.createUser(new UserData("hobbs", "meow", "hobbs@mail.com"));
    }

    @Test
    public void clearRemovesAllAuthTokens() throws DataAccessException, SQLException {
        insertAuthDirectly("token1", "hobbs");
        insertAuthDirectly("token2", "hobbs");
        Assertions.assertEquals(2, countAuths());

        authDAO.clear();

        Assertions.assertEquals(0, countAuths());
    }

    @Test
    public void createAuthStoresToken() throws DataAccessException, SQLException {
        authDAO.createAuth(new AuthData("token1", "hobbs"));

        Assertions.assertEquals(1, countAuths());
        Assertions.assertEquals("hobbs", usernameForToken("token1"));
    }

    @Test
    public void createAuthUnknownUser() throws DataAccessException, SQLException {
        Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.createAuth(new AuthData("token1", "nobody")));
        Assertions.assertEquals(0, countAuths());
    }

    private void insertAuthDirectly(String token, String username) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
            statement.setString(1, token);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    private int countAuths() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT COUNT(*) FROM auth");
             var resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private String usernameForToken(String token) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT username FROM auth WHERE authToken = ?")) {
            statement.setString(1, token);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString(1) : null;
            }
        }
    }
}