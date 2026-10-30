package dataaccess;

import dataaccess.sql.DatabaseInitializer;
import dataaccess.sql.SQLUserDAO;
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
        insertUserDirectly("cavin");
        Assertions.assertEquals(2, countUsers());

        userDAO.clear();

        Assertions.assertEquals(0, countUsers());
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