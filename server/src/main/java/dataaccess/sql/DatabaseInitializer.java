package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Creates the chess database and its tables if they do not already exist.
 */
public class DatabaseInitializer {
    private static final String CREATE_USER_TABLE = """
            CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

    private static final String CREATE_AUTH_TABLE = """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken),
                FOREIGN KEY (username) REFERENCES user(username) ON DELETE CASCADE
            )""";

    private static final String CREATE_GAME_TABLE = """
            CREATE TABLE IF NOT EXISTS game (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game TEXT NOT NULL,
                PRIMARY KEY (gameID)
            )""";

    /**
     * Safe to call any number of times: existing databases and tables are left alone.
     */
    public static void initialize() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            createTable(conn, CREATE_USER_TABLE);
            createTable(conn, CREATE_AUTH_TABLE);
            createTable(conn, CREATE_GAME_TABLE);
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create tables", ex);
        }
    }

    private static void createTable(Connection conn, String statement) throws SQLException {
        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        }
    }
}