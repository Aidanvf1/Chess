package dataaccess;

import dataaccess.sql.DatabaseInitializer;
import dataaccess.sql.SQLGameDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SQLGameDAOTests {
    private SQLGameDAO gameDAO;

    @BeforeAll
    public static void createTables() throws DataAccessException {
        DatabaseInitializer.initialize();
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void clearRemovesAllGames() throws DataAccessException, SQLException {
        gameDAO.createGame("first");
        gameDAO.createGame("second");

        gameDAO.clear();

        Assertions.assertEquals(0, countGames());
    }

    @Test
    public void createGameStoresGame() throws DataAccessException, SQLException {
        gameDAO.createGame("my game");

        Assertions.assertEquals(1, countGames());
    }

    @Test
    public void createGameNullName() {
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    private int countGames() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT COUNT(*) FROM game");
             var resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}