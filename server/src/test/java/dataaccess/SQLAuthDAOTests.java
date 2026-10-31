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
        authDAO.createAuth(new AuthData("token1", "hobbs"));

        authDAO.clear();

        Assertions.assertEquals(0, countAuths());
    }

    @Test
    public void createAuthStoresToken() throws DataAccessException, SQLException {
        authDAO.createAuth(new AuthData("token1", "hobbs"));

        Assertions.assertEquals(1, countAuths());
    }

    @Test
    public void createAuthUnknownUser() {
        Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.createAuth(new AuthData("token1", "nobody")));
    }

    @Test
    public void getAuthFindsToken() throws DataAccessException {
        AuthData auth = new AuthData("token1", "hobbs");
        authDAO.createAuth(auth);

        Assertions.assertEquals(auth, authDAO.getAuth("token1"));
    }

    @Test
    public void getAuthNotFound() throws DataAccessException {
        Assertions.assertNull(authDAO.getAuth("missing"));
    }

    @Test
    public void deleteAuthRemovesToken() throws DataAccessException {
        authDAO.createAuth(new AuthData("token1", "hobbs"));

        authDAO.deleteAuth("token1");

        Assertions.assertNull(authDAO.getAuth("token1"));
    }

    private int countAuths() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT COUNT(*) FROM auth");
             var resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}