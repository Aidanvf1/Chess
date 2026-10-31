package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("DELETE FROM auth")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear auth tokens", ex);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create auth token", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }
}