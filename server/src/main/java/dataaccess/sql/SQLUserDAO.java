package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("DELETE FROM user")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear users", ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }
}