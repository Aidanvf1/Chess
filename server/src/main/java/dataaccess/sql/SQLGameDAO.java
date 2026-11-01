package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
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
        throw new DataAccessException("not implemented");
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }
}