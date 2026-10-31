package dataaccess;

import dataaccess.sql.DatabaseInitializer;
import dataaccess.sql.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SQLUserDAOTests {
    private SQLUserDAO userDAO;

    @BeforeAll
    public static void createTables() throws DataAccessException {
        DatabaseInitializer.initialize();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void clearRemovesAllUsers() throws DataAccessException, SQLException {
        insertUserDirectly("hobbs");
        insertUserDirectly("wilson");
        Assertions.assertEquals(2, countUsers());

        userDAO.clear();

        Assertions.assertEquals(0, countUsers());
    }

    @Test
    public void createUserStoresUser() throws DataAccessException {
        UserData user = new UserData("hobbs", "hash", "hobbs@mail.com");

        userDAO.createUser(user);

        Assertions.assertEquals(user, userDAO.getUser("hobbs"));
    }

    @Test
    public void createUserDuplicateUsername() throws DataAccessException, SQLException {
        userDAO.createUser(new UserData("hobbs", "hash", "hobbs@mail.com"));

        Assertions.assertThrows(DataAccessException.class,
                () -> userDAO.createUser(new UserData("hobbs", "other", "other@mail.com")));
        Assertions.assertEquals(1, countUsers());
    }

    @Test
    public void getUserFindsExistingUser() throws DataAccessException, SQLException {
        insertUserDirectly("hobbs");

        UserData found = userDAO.getUser("hobbs");

        Assertions.assertEquals(new UserData("hobbs", "hash", "hobbs@mail.com"), found);
    }

    @Test
    public void getUserNotFound() throws DataAccessException {
        Assertions.assertNull(userDAO.getUser("nobody"));
    }

    private void insertUserDirectly(String username) throws DataAccessException, SQLException {
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, "hash");
            statement.setString(3, username + "@mail.com");
            statement.executeUpdate();
        }
    }

    private int countUsers() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT COUNT(*) FROM user");
             var resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}