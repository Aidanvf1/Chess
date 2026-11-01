package dataaccess;

import chess.ChessGame;
import dataaccess.sql.DatabaseInitializer;
import dataaccess.sql.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

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

    @Test
    public void getGameFindsGame() throws DataAccessException {
        int gameID = gameDAO.createGame("my game");

        GameData game = gameDAO.getGame(gameID);

        Assertions.assertEquals(new GameData(gameID, null, null, "my game", new ChessGame()), game);
    }

    @Test
    public void getGameNotFound() throws DataAccessException {
        Assertions.assertNull(gameDAO.getGame(999));
    }

    @Test
    public void listGamesReturnsAllGames() throws DataAccessException {
        int firstID = gameDAO.createGame("first");
        int secondID = gameDAO.createGame("second");

        Collection<GameData> games = gameDAO.listGames();

        Assertions.assertEquals(2, games.size());
        Assertions.assertTrue(games.contains(gameDAO.getGame(firstID)));
        Assertions.assertTrue(games.contains(gameDAO.getGame(secondID)));
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