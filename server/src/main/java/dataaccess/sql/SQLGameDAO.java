package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    private static final String SELECT_GAMES =
            "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";

    private final Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("DELETE FROM game")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear games", ex);
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO game (gameName, game) VALUES (?, ?)";
        int gameID = 0;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, gameName);
            statement.setString(2, gson.toJson(new ChessGame()));
            statement.executeUpdate();
            try (var keys = statement.getGeneratedKeys()) {
                keys.next();
                gameID = keys.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create game", ex);
        }
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = null;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(SELECT_GAMES + " WHERE gameID = ?")) {
            statement.setInt(1, gameID);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    game = readGame(resultSet);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get game", ex);
        }
        return game;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(SELECT_GAMES);
             var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                games.add(readGame(resultSet));
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to list games", ex);
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? "
                + "WHERE gameID = ?";
        int rowsUpdated;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gson.toJson(game.game()));
            statement.setInt(5, game.gameID());
            rowsUpdated = statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to update game", ex);
        }
        if (rowsUpdated == 0) {
            throw new DataAccessException("Game does not exist");
        }
    }

    private GameData readGame(ResultSet resultSet) throws SQLException {
        return new GameData(resultSet.getInt("gameID"),
                resultSet.getString("whiteUsername"),
                resultSet.getString("blackUsername"),
                resultSet.getString("gameName"),
                gson.fromJson(resultSet.getString("game"), ChessGame.class));
    }
}